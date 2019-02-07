import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class MinerTest {

    Trader trader1;
    Trader trader2;
    Trader trader3;
    Miner miner;

    @Before
    public void clearMaps() {
        Main.traders.clear();
        Main.miners.clear();
        Chain.getInstance().transactionsPool.clear();

        trader1 = new Trader("trader1", 10);
        trader2 = new Trader("id123", "trader2", 5);
        trader3 = new Trader("xyz123", "trader3", 2);
        miner = new Miner("miner", 1);
    }

    @Test
    public void TestValidateTransaction_OK() {
        trader1.sendMoney("id123", 1);
        Transaction transaction = Chain.getInstance().transactionsPool.get(0);

        assertTrue(miner.transactionIsValid(transaction));
        assertEquals(9, trader1.getBalance(), 0.0);
        assertEquals(6, trader2.getBalance(), 0.0);
    }

    @Test
    public void TestValidateTransaction_OkMin() {
        trader1.sendMoney("id123", 0.1);
        Transaction transaction = Chain.getInstance().transactionsPool.get(0);

        assertTrue(miner.transactionIsValid(transaction));
        assertEquals(9.9, trader1.getBalance(), 0.0);
        assertEquals(5.1, trader2.getBalance(), 0.0);
    }

    @Test
    public void TestValidateTransaction_ReceiverNotFound() {
        trader1.sendMoney("id12345", 1);
        Transaction transaction = Chain.getInstance().transactionsPool.get(0);

        assertEquals(10, trader1.getBalance(), 0.0);
        assertFalse(miner.transactionIsValid(transaction));
    }

    @Test
    public void TestValidateTransaction_NotEnoughMoney() {
        trader1.sendMoney("id123", 12);
        Transaction transaction = Chain.getInstance().transactionsPool.get(0);

        assertFalse(miner.transactionIsValid(transaction));
        assertEquals(10, trader1.getBalance(), 0.0);
        assertEquals(5, trader2.getBalance(), 0.0);
    }

    @Test
    public void TestValidateTransaction_MinAmount() {
        trader1.sendMoney("id123", 0.05);
        Transaction transaction = Chain.getInstance().transactionsPool.get(0);

        assertFalse(miner.transactionIsValid(transaction));
        assertEquals(10, trader1.getBalance(), 0.0);
        assertEquals(5, trader2.getBalance(), 0.0);
    }

    @Test
    public void TestMine() {
        int initialSize = Chain.getInstance().getBlocks().size();
        trader1.sendMoney("xyz123", 1);
        trader2.sendMoney("xyz123", 2);
        miner.createBlock("####");

        for(Transaction transaction : Chain.getInstance().getTransactionsPool()) {
            miner.transactionIsValid(transaction);
            miner.addTransaction(transaction);
        }

        miner.mine();

        String hash = miner.currentBlock.getHash();
        for(int i = 0; i < Chain.DIFFICULTY; i++) {
            assertEquals(hash.charAt(i), '0');
        }
        assertEquals(initialSize+1, Chain.getInstance().getBlocks().size());
    }

    @Test
    public void TestAddTransaction() {
        miner.createBlock("####");
        int initialSize = miner.currentBlock.getTransactions().size();
        miner.addTransaction(new Transaction(10, "id123", "xyz123"));
        assertEquals(initialSize + 1, miner.currentBlock.getTransactions().size());
    }

}
