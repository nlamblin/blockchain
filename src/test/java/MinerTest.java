import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class MinerTest {

    private Trader trader1;
    private Trader trader2;
    private Trader trader3;
    private Miner miner;

    @Before
    public void clearMap() {
        Main.miners.clear();
        Main.traders.clear();
        this.trader1 = new Trader("trader1", 10);
        this.trader2 = new Trader("id123", "trader2", 5);
        this.trader3 = new Trader("xyz123", "trader3", 2);
        this.miner = new Miner("miner", 1);
        miner.getNewTransactions().clear();
    }

    @Test
    public void TestValidateTransaction_OK() {
        trader1.sendMoney("id123", 1);
        Transaction transaction = miner.getNewTransactions().get(0);
        assertTrue(miner.transactionIsValid(transaction));
    }

    @Test
    public void TestValidateTransaction_OkMin() {
        trader1.sendMoney("id123", 0.1);
        Transaction transaction = miner.getNewTransactions().get(0);
        assertTrue(miner.transactionIsValid(transaction));
    }

    @Test
    public void TestValidateTransaction_ReceiverNotFound() {
        trader1.sendMoney("id12345", 1);
        Transaction transaction = miner.getNewTransactions().get(0);
        assertFalse(miner.transactionIsValid(transaction));
        assertEquals(0, transaction.getValidationStatus());
    }

    @Test
    public void TestValidateTransaction_NotEnoughMoney() {
        trader1.sendMoney("id123", 12);
        Transaction transaction = miner.getNewTransactions().get(0);
        assertFalse(miner.transactionIsValid(transaction));
        assertEquals(0, transaction.getValidationStatus());
    }

    @Test
    public void TestValidateTransaction_NoMinAmount() {
        trader1.sendMoney("id123", 0.05);
        Transaction transaction = miner.getNewTransactions().get(0);
        assertFalse(miner.transactionIsValid(transaction));
        assertEquals(0, transaction.getValidationStatus());
    }

    @Test
    public void TestMine() {
        int initialSize = Chain.getInstance().getBlocks().size();
        trader1.sendMoney("xyz123", 1);
        trader2.sendMoney("xyz123", 2);
        miner.createBlock("####");
        miner.addTransactionToBlock(miner.getNewTransactions());
        miner.mine();

        String hash = miner.currentBlock.getHash();
        for(int i = 0; i < Chain.DIFFICULTY; i++) {
            assertEquals(hash.charAt(i), '0');
        }
        assertEquals(initialSize+1, Chain.getInstance().getBlocks().size());
        assertEquals(9, trader1.getBalance(), 0.0);
        assertEquals(3, trader2.getBalance(), 0.0);
        assertEquals(5, trader3.getBalance(), 0.0);
    }

    @Test
    public void TestAddTransactionsToBlock() {
        ArrayList<Transaction> transactions = new ArrayList<>();
        Transaction transaction1 = new Transaction(0.1, "id123", "xyz123");
        Transaction transaction2 = new Transaction(0.2, "xyz123", "id123");
        transactions.add(transaction1);
        transactions.add(transaction2);
        miner.createBlock("####");
        miner.addTransactionToBlock(transactions);
        assertEquals(miner.currentBlock.getTransactions().size(), transactions.size());
    }

}
