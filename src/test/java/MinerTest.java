import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class MinerTest {

    private static Trader trader1 = new Trader("trader1", 10);
    private static Trader trader2 = new Trader("trader2", 5);
    private static Trader trader3 = new Trader("trader3", 2);
    private static Miner miner = new Miner("miner", 1);

    @AfterClass
    public static void initClass() {
        Server.traders.clear();
        Server.miners.clear();
    }

    @Before
    public void initTest() {
        miner.setCurrentBlock(null);
        Chain.getInstance().getBlocks().clear();
    }

    @Test
    public void TestValidateTransaction_OK() {
        trader1.sendMoney(trader2.getPublicKey(), 1);
        Transaction transaction = miner.getCurrentBlock().getTransactions().get(0);
        assertTrue(Server.transactionIsValid(transaction));
    }

    @Test
    public void TestValidateTransaction_OkMin() {
        trader1.sendMoney(trader2.getPublicKey(), 0.1);
        Transaction transaction = miner.getCurrentBlock().getTransactions().get(0);
        assertTrue(Server.transactionIsValid(transaction));
    }

    @Test
    public void TestValidateTransaction_ReceiverNotFound() throws NoSuchAlgorithmException, InvalidKeySpecException {
        PublicKey oldPublicKey = trader3.getPublicKey();
        // to prevent the fact that the new public key is already the current public key
        String key = this.getNewKey(trader3);
        this.setNewKey(trader3, key);
        Transaction transaction = new Transaction(0.05, trader2.getPublicKey(), oldPublicKey);
        assertFalse(Server.transactionIsValid(transaction));
        trader3.setPublicKey(oldPublicKey); // restore the initial public key
    }

    @Test
    public void TestValidateTransaction_NotEnoughMoney() {
        Transaction transaction = new Transaction(20, trader1.getPublicKey(), trader3.getPublicKey());
        assertFalse(Server.transactionIsValid(transaction));
    }

    @Test
    public void TestValidateTransaction_NoMinAmount() {
        Transaction transaction = new Transaction(0.05, trader3.getPublicKey(), trader2.getPublicKey());
        assertFalse(Server.transactionIsValid(transaction));
    }

    @Test
    public void TestVerifySignature_NOK() throws NoSuchAlgorithmException, InvalidKeySpecException {
        // to prevent the fact that the new public key is already the current public key
        PublicKey oldPublicKey = trader3.getPublicKey();
        String key = this.getNewKey(trader3);
        Transaction transaction = new Transaction(1, trader3.getPublicKey(), trader2.getPublicKey());
        trader3.sign(transaction);
        this.setNewKey(trader3, key);
        assertFalse(Server.verifySignature(transaction));
        assertFalse(Server.transactionIsValid(transaction));
        trader3.setPublicKey(oldPublicKey); // restore the initial public key
    }

    @Test
    public void TestMine() {
        trader1.sendMoney(trader3.getPublicKey(), 1);
        trader2.sendMoney(trader3.getPublicKey(), 2);
        miner.setGpu(new GPU(miner.getCurrentBlock(),miner));
        miner.getGpu().mine();
        for(int i = 0; i < Chain.DIFFICULTY; i++) {
            assertEquals(miner.getCurrentBlock().getHash().charAt(i), '0');
        }
    }

    @Test
    public void TestAddTransactionsToBlock() {
        ArrayList<Transaction> transactions = new ArrayList<>();
        Transaction transaction1 = new Transaction(0.1, trader2.getPublicKey(), trader3.getPublicKey());
        Transaction transaction2 = new Transaction(0.2, trader3.getPublicKey(), trader2.getPublicKey());
        transactions.add(transaction1);
        transactions.add(transaction2);
        miner.createBlock();
        miner.getCurrentBlock().setTransactions(transactions);
        assertEquals(miner.getCurrentBlock().getTransactions().size(), transactions.size());
    }

    @Test
    public void TestCreateBlock_NewBlock() {
        miner.createBlock();
        assertEquals(miner.getCurrentBlock().getPreviousHash(), "####");
    }

    @Test
    public void TestCreateBlock_AlreadyExists() {
        Block block = new Block("758307cc8f73326078d4c793f85fb8cf0606fdc66b95ee2cfb0ca3cce11d333d");
        miner.setCurrentBlock(block);
        miner.createBlock();
        assertEquals(miner.getCurrentBlock().getPreviousHash(), "758307cc8f73326078d4c793f85fb8cf0606fdc66b95ee2cfb0ca3cce11d333d");
    }

    @Test
    public void TestValidateTransaction_Valid() {
        miner.createBlock();
        Transaction transaction = new Transaction(2, trader1.getPublicKey(), trader2.getPublicKey());
        trader1.sign(transaction);
        Server.validateNewTransaction(transaction);
        assertEquals(1, miner.getCurrentBlock().getTransactions().size());
    }

    @Test
    public void TestValidateTransaction_NotValid() {
        miner.createBlock();
        Transaction transaction = new Transaction(200, trader1.getPublicKey(), trader3.getPublicKey());
        trader1.sign(transaction);
        Server.validateNewTransaction(transaction);
        assertEquals(0, transaction.getValidationStatus());
        assertEquals(0, miner.getCurrentBlock().getTransactions().size());
    }

    @Test
    public void TestExchangeMoney() {
        Transaction transaction = new Transaction(1, trader1.getPublicKey(), trader2.getPublicKey());
        miner.exchangeMoney(transaction);
        assertEquals(9, trader1.getBalance(), 0.0);
        assertEquals(6, trader2.getBalance(), 0.0);
    }

    @Test
    public void TestMiningProcess_EnoughTransactions() {
        miner.createBlock();
        for(int i = 0; i < Chain.BLOCK_SIZE; i++) {
            miner.getCurrentBlock().getTransactions().add(new Transaction(0.2, trader3.getPublicKey(), trader2.getPublicKey()));
        }
        miner.miningProcess();
        assertEquals(1, Chain.getInstance().getBlocks().size());
        assertNull(miner.getCurrentBlock());
    }

    @Test
    public void TestMiningProcess_NotEnoughTransactions() {
        miner.createBlock();
        for(int i = 0; i < Chain.BLOCK_SIZE - 1; i++) {
            miner.getCurrentBlock().getTransactions().add(new Transaction(0.2, trader1.getPublicKey(), trader2.getPublicKey()));
        }
        miner.miningProcess();
        miner.miningProcess();
        assertEquals(0, Chain.getInstance().getBlocks().size());
        assertNotNull(miner.getCurrentBlock());
    }

    @Test
    public void TestChainIsValid_Yes() {
        Block block1 = new Block("####");
        block1.setHash("758307cc8f73326078d4c793f85fb8cf0606fdc66b95ee2cfb0ca3cce11d333d");
        Block block2 = new Block("758307cc8f73326078d4c793f85fb8cf0606fdc66b95ee2cfb0ca3cce11d333d");
        Chain.getInstance().getBlocks().add(block1);
        Chain.getInstance().getBlocks().add(block2);
        assertTrue(Chain.isValid());
    }

    @Test
    public void TestChainIsValid_No() {
        Block block1 = new Block("####");
        block1.setHash("758307cc8f73326078d4c793f85fb8cf0606fdc66b95ee2cfb0ca3cce11d333d");
        Block block2 = new Block("23d57d98c4abb9d264c094161d50056f4f37be8f59bb769f10e4307aa551fcfe");
        Chain.getInstance().getBlocks().add(block1);
        Chain.getInstance().getBlocks().add(block2);
        assertFalse(Chain.isValid());
    }

    @Test
    public void TestVerifySignature_OK() {
        trader1.sendMoney(trader2.getPublicKey(), 1);
        assertTrue(Server.verifySignature(miner.getCurrentBlock().getTransactions().get(0)));
    }

    private String getNewKey(User user) {
        String key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAr+jR296B8EZDXsD019JfKbYgeN+l43Dp+Gag6bU4LwTsDEa6upj5wxLLPGwH+D2Y6aSmmAFU6m4mFeAcNDEwYZ2OpT5X2E8B3L38Lh0Kkx39AooiLWHNBIWUtIXuziXbxOBsBjh5QlwvUL/Op1T7VVtiGrrn8RGA76eUI5WhfDQUqvpCF8Ee0W7IyypmmXzJnYyCqa8WxqvBcK4yUfAsBAwzCh7D8eYXrbbI9EB1nR7JTmJ9aYdhhrgDoUCdLPPQ+XfBAX569a51iN3/A9IMqGFogGn8ILJhw5rNHb68EQuT/4sO6l28LxJOApdBBT+89jdr9mzwPyG8/WmA4U/ofQIDAQAB";
        if(Tools.getStringFromKey(user.getPublicKey()).equals(key)) {
            key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqRuhpS7YWRzPRdShYq/cPZZyfFQwG/28KSRmkUNRkeihBfUKjpHC0iIfUKu2ia1Sj3SUU+N3zn5etzih+hUodiyWWcaiu0ir8PunnBL0KcGTj+ASjDVjJWDbnS3anPXUZIOg5ULFdBEy1vycQbBNHN0eYaNqSTwSelug5oxa+vntqbfANaK5pN5GTgdGTEy4HrZc2S3EVWuYXYBz0+a/+js5gdVwf/tXnp3tv0/tDODRPWifyjsZN0brTvTNxbS7WFxYBK6Fq3kZm/yjGKEquR9pOEamyR/p8//YiGf+eGxI9vU3HLXBnQLe+/2nf6AQmatmrWAQCzXyNboq2RpRsQIDAQAB";
        }
        return key;
    }

    private void setNewKey(User user, String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Base64.getDecoder().decode(key);
        user.setPublicKey(KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyBytes)));
    }
}
