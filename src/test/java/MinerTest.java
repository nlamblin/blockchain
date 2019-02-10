import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class MinerTest {

    private static Trader trader1 = new Trader("trader1", 10);
    private static Trader trader2 = new Trader("id123", "trader2", 5);
    private static Trader trader3 = new Trader("xyz123", "trader3", 2);
    private static Miner miner = new Miner("miner", 1);

    @AfterClass
    public static void initClass() {
        Main.traders.clear();
        Main.miners.clear();
    }

    @Before
    public void initTest() {
        miner.setCurrentBlock(null);
        Chain.getInstance().getBlocks().clear();
    }

    @Test
    public void TestValidateTransaction_OK() {
        trader1.sendMoney(trader2.getId(), 1);
        Transaction transaction = miner.getCurrentBlock().getTransactions().get(0);
        assertTrue(miner.transactionIsValid(transaction));
    }

    @Test
    public void TestValidateTransaction_OkMin() {
        trader1.sendMoney(trader2.getId(), 0.1);
        Transaction transaction = miner.getCurrentBlock().getTransactions().get(0);
        assertTrue(miner.transactionIsValid(transaction));
    }

    @Test
    public void TestValidateTransaction_ReceiverNotFound() {
        Transaction transaction = new Transaction(0.05, trader2.getId(), "id12345");
        assertFalse(miner.transactionIsValid(transaction));
    }

    @Test
    public void TestValidateTransaction_NotEnoughMoney() {
        Transaction transaction = new Transaction(20, trader1.getId(), trader3.getId());
        assertFalse(miner.transactionIsValid(transaction));
    }

    @Test
    public void TestValidateTransaction_NoMinAmount() {
        Transaction transaction = new Transaction(0.05, trader3.getId(), trader2.getId());
        assertFalse(miner.transactionIsValid(transaction));
    }

    @Test
    public void TestMine() {
        trader1.sendMoney(trader3.getId(), 1);
        trader2.sendMoney(trader3.getId(), 2);
        miner.mine();
        for(int i = 0; i < Chain.DIFFICULTY; i++) {
            assertEquals(miner.getCurrentBlock().getHash().charAt(i), '0');
        }
    }

    @Test
    public void TestAddTransactionsToBlock() {
        ArrayList<Transaction> transactions = new ArrayList<>();
        Transaction transaction1 = new Transaction(0.1, trader2.getId(), trader3.getId());
        Transaction transaction2 = new Transaction(0.2, trader3.getId(), trader2.getId());
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
        Transaction transaction = new Transaction(2, trader1.getId(), trader2.getId());
        miner.validateNewTransaction(transaction);
        assertEquals(1, miner.getCurrentBlock().getTransactions().size());
    }

    @Test
    public void TestValidateTransaction_NotValid() {
        miner.createBlock();
        Transaction transaction = new Transaction(2, trader1.getId(), "abcde12345");
        miner.validateNewTransaction(transaction);
        assertEquals(0, transaction.getValidationStatus());
        assertEquals(0, miner.getCurrentBlock().getTransactions().size());
    }

    @Test
    public void TestExchangeMoney() {
        Transaction transaction = new Transaction(1, trader1.getId(), trader2.getId());
        miner.exchangeMoney(transaction);
        assertEquals(9, trader1.getBalance(), 0.0);
        assertEquals(6, trader2.getBalance(), 0.0);
    }

    @Test
    public void testMiningProcess_EnoughTransactions() {
        miner.createBlock();
        for(int i = 0; i < Chain.BLOCK_SIZE; i++) {
            miner.getCurrentBlock().getTransactions().add(new Transaction(0.2, trader1.getId(), trader2.getId()));
        }
        miner.miningProcess();
        assertEquals(1, Chain.getInstance().getBlocks().size());
        assertNull(miner.getCurrentBlock());
    }

    @Test
    public void TestMiningProcess_NotEnoughTransactions() {
        miner.createBlock();
        for(int i = 0; i < Chain.BLOCK_SIZE - 1; i++) {
            miner.getCurrentBlock().getTransactions().add(new Transaction(0.2, trader1.getId(), trader2.getId()));
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
        assertTrue(miner.chainIsValid());
    }

    @Test
    public void TestChainIsValid_No() {
        Block block1 = new Block("####");
        block1.setHash("758307cc8f73326078d4c793f85fb8cf0606fdc66b95ee2cfb0ca3cce11d333d");
        Block block2 = new Block("23d57d98c4abb9d264c094161d50056f4f37be8f59bb769f10e4307aa551fcfe");
        Chain.getInstance().getBlocks().add(block1);
        Chain.getInstance().getBlocks().add(block2);
        assertFalse(miner.chainIsValid());
    }
}
