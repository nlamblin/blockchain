import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Timestamp;
import java.util.Base64;

import static org.junit.Assert.*;

public class BlockTest {

    private static Trader trader1 = new Trader("trader1", 20);
    private static Trader trader2 = new Trader( "trader2", 40);
    private static Trader trader3 = new Trader("trader3", 12);
    private static Miner miner = new Miner("miner", 1);

    @Before
    public void initTest() throws NoSuchAlgorithmException, InvalidKeySpecException {
        miner.setCurrentBlock(null);

        String[] keys = new String[3];
        PublicKey[] publicKeys = new PublicKey[3];
        keys[0] = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAr+jR296B8EZDXsD019JfKbYgeN+l43Dp+Gag6bU4LwTsDEa6upj5wxLLPGwH+D2Y6aSmmAFU6m4mFeAcNDEwYZ2OpT5X2E8B3L38Lh0Kkx39AooiLWHNBIWUtIXuziXbxOBsBjh5QlwvUL/Op1T7VVtiGrrn8RGA76eUI5WhfDQUqvpCF8Ee0W7IyypmmXzJnYyCqa8WxqvBcK4yUfAsBAwzCh7D8eYXrbbI9EB1nR7JTmJ9aYdhhrgDoUCdLPPQ+XfBAX569a51iN3/A9IMqGFogGn8ILJhw5rNHb68EQuT/4sO6l28LxJOApdBBT+89jdr9mzwPyG8/WmA4U/ofQIDAQAB";
        keys[1] = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqRuhpS7YWRzPRdShYq/cPZZyfFQwG/28KSRmkUNRkeihBfUKjpHC0iIfUKu2ia1Sj3SUU+N3zn5etzih+hUodiyWWcaiu0ir8PunnBL0KcGTj+ASjDVjJWDbnS3anPXUZIOg5ULFdBEy1vycQbBNHN0eYaNqSTwSelug5oxa+vntqbfANaK5pN5GTgdGTEy4HrZc2S3EVWuYXYBz0+a/+js5gdVwf/tXnp3tv0/tDODRPWifyjsZN0brTvTNxbS7WFxYBK6Fq3kZm/yjGKEquR9pOEamyR/p8//YiGf+eGxI9vU3HLXBnQLe+/2nf6AQmatmrWAQCzXyNboq2RpRsQIDAQAB";
        keys[2] = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApH+Z8pNQWZyytSbYAI3aCqIlVMV4WQ2MfClfUYjr1utxvpkeMmuxDDacrP+GvJiQAO+oHU/vXwTO0e/Uj5kv6lFusw6h3+N9XDiE//YtEJcnOtUs7UVFteH61ACs/AKO+lu7ToUSFy9TK0qPWYuWNbY9SuHEvfs0rvrMBos1ETD74aXoK5eTG9KthZZQgCpBS1XlXu467Hk+xo+SzjNK9eYfQS9cITdeoKpJHSYBjZAoKDRmuvjfunTed3Zh3z3/CeWhqr4gmt0P1cSVtlynmAI37FJxeRC9fg89B3+7vslA73omOeA8Hd5gToG2z0LyldPpJ2LAFdvA+G5gK+cJ+wIDAQAB";
        for(int i = 0; i < keys.length; i++) {
            byte[] keyBytes = Base64.getDecoder().decode(keys[i]);
            publicKeys[i] = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyBytes));
        }
        trader1.setPublicKey(publicKeys[0]);
        trader2.setPublicKey(publicKeys[1]);
        trader3.setPublicKey(publicKeys[2]);
    }

    @AfterClass
    public static void clearClass() {
        Server.traders.clear();
        Server.miners.clear();
    }

    @Test
    public void TestConstructor() {
        String previousHash = "77685afb04ce2a8b334464fa0508ebcc97b8a75695a97fede1794453906f1245";
        Block b = new Block(previousHash);
        assertEquals(b.getPreviousHash(), previousHash);
    }

    @Test
    public void TestMerkleRootHash_4Transactions() {
        this.addCommonTransactionsToTest();
        miner.getCurrentBlock().createMerkleTree();
        assertEquals("3f2da31a97978a2ddd7abce440204d813215ad8ee75389f62e83dcbe87eede7c", miner.getCurrentBlock().getMerkleRootHash());
    }

    @Test
    public void TestMerkleRootHash_5Transactions() {
        this.addCommonTransactionsToTest();
        Transaction transaction5 = this.createTransactionToTest(1.6, trader2, trader1, 1549299499);
        miner.getCurrentBlock().getTransactions().add(transaction5);
        miner.getCurrentBlock().createMerkleTree();
        assertEquals("4b530143b7e5d307c0beb8856b36e1280f0928ccb6db921f8dfadd27299ca49d", miner.getCurrentBlock().getMerkleRootHash());
    }

    @Test
    public void TestMerkleRootHash_6Transactions() {
        this.addCommonTransactionsToTest();
        Transaction transaction5 = this.createTransactionToTest(1.6, trader2, trader1, 1549299499);
        Transaction transaction6 = this.createTransactionToTest(1.9, trader1, trader3, 1549299499);
        miner.getCurrentBlock().getTransactions().add(transaction5);
        miner.getCurrentBlock().getTransactions().add(transaction6);
        miner.getCurrentBlock().createMerkleTree();
        assertEquals("24cc925ea8a048b8ad9a45bd579f07749ad43c519f797ae8e08fd05a02f38ee2", miner.getCurrentBlock().getMerkleRootHash());
    }

    @Test
    public void TestMerkleRootHash_7Transactions() {
        this.addCommonTransactionsToTest();
        Transaction transaction5 = this.createTransactionToTest(1.6, trader2, trader1, 1549299499);
        Transaction transaction6 = this.createTransactionToTest(1.9, trader1, trader3, 1549299499);
        Transaction transaction7 = this.createTransactionToTest(3, trader2, trader3, 1549299497);
        miner.getCurrentBlock().getTransactions().add(transaction5);
        miner.getCurrentBlock().getTransactions().add(transaction6);
        miner.getCurrentBlock().getTransactions().add(transaction7);
        miner.getCurrentBlock().createMerkleTree();
        assertEquals("b77f7b943815c60ac1ec09f5cff38f14e2eaccd4b6b51d9a99f280919a03f3d0", miner.getCurrentBlock().getMerkleRootHash());
    }

    private Transaction createTransactionToTest(double amount, Trader sender, Trader receiver, long timestamp) {
        Transaction transaction = new Transaction(amount, sender.getPublicKey(), receiver.getPublicKey());
        transaction.setTimestamp(new Timestamp(timestamp));
        transaction.generateHash();
        return transaction;
    }

    private void addCommonTransactionsToTest() {
        miner.createBlock();
        Transaction transaction1 = this.createTransactionToTest(1.1, trader1, trader2, 1549299372);
        Transaction transaction2 = this.createTransactionToTest(0.6, trader2, trader3, 1549299482);
        Transaction transaction3 = this.createTransactionToTest(0.4, trader3, trader1, 1549299494);
        Transaction transaction4 = this.createTransactionToTest(1.4, trader3, trader2, 1549299497);
        miner.getCurrentBlock().getTransactions().add(transaction1);
        miner.getCurrentBlock().getTransactions().add(transaction2);
        miner.getCurrentBlock().getTransactions().add(transaction3);
        miner.getCurrentBlock().getTransactions().add(transaction4);
    }
}
