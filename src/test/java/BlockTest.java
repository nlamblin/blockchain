import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;

import static org.junit.Assert.*;

public class BlockTest {

    private Trader trader1;
    private Trader trader2;
    private Trader trader3;
    private Miner miner;

    @Before
    public void beforeTests() {
        this.trader1 = new Trader("abc100", "trader1", 20);
        this.trader2 = new Trader("def200", "trader2", 40);
        this.trader3 = new Trader("ghi300", "trader3", 12);
        this.miner = new Miner("miner", 1);
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
        assertEquals("979c6cf44f6ffd172542c45625766842a9345b036da54b7e575f1313cd220411", miner.getCurrentBlock().getMerkleRootHash());
    }

    @Test
    public void TestMerkleRootHash_5Transactions() {
        this.addCommonTransactionsToTest();
        Transaction transaction5 = this.createTransactionToTest(1.6, trader2, trader1, 1549299499);
        miner.addTransaction(transaction5);
        miner.getCurrentBlock().createMerkleTree();
        assertEquals("4f019acc886b914960371d3475e4bd040d06daf60c511a30af2c1fa2f871e071", miner.getCurrentBlock().getMerkleRootHash());
    }

    @Test
    public void TestMerkleRootHash_6Transactions() {
        this.addCommonTransactionsToTest();
        Transaction transaction5 = this.createTransactionToTest(1.6, trader2, trader1, 1549299499);
        Transaction transaction6 = this.createTransactionToTest(1.9, trader1, trader3, 1549299499);
        miner.addTransaction(transaction5);
        miner.addTransaction(transaction6);
        miner.getCurrentBlock().createMerkleTree();
        assertEquals("d3280a05a538b5ea6041487c9cc0939f76ccb518280d70080a9037cd1fdf9989", miner.getCurrentBlock().getMerkleRootHash());
    }

    @Test
    public void TestMerkleRootHash_7Transactions() {
        this.addCommonTransactionsToTest();
        Transaction transaction5 = this.createTransactionToTest(1.6, trader2, trader1, 1549299499);
        Transaction transaction6 = this.createTransactionToTest(1.9, trader1, trader3, 1549299499);
        Transaction transaction7 = this.createTransactionToTest(3, trader2, trader3, 1549299497);
        miner.addTransaction(transaction5);
        miner.addTransaction(transaction6);
        miner.addTransaction(transaction7);
        miner.getCurrentBlock().createMerkleTree();
        assertEquals("758307cc8f73326078d4c793f85fb8cf0606fdc66b95ee2cfb0ca3cce11d333d", miner.getCurrentBlock().getMerkleRootHash());
    }

    private Transaction createTransactionToTest(double amount, Trader sender, Trader receiver, long timestamp) {
        Transaction transaction = new Transaction(amount, sender.getId(), receiver.getId());
        transaction.setTimestamp(new Timestamp(timestamp));
        transaction.generateHash();
        return transaction;
    }

    private void addCommonTransactionsToTest() {
        miner.createBlock("####");
        
        Transaction transaction1 = this.createTransactionToTest(1.1, trader1, trader2, 1549299372);
        Transaction transaction2 = this.createTransactionToTest(0.6, trader2, trader3, 1549299482);
        Transaction transaction3 = this.createTransactionToTest(0.4, trader3, trader1, 1549299494);
        Transaction transaction4 = this.createTransactionToTest(1.4, trader3, trader2, 1549299497);

        miner.addTransaction(transaction1);
        miner.addTransaction(transaction2);
        miner.addTransaction(transaction3);
        miner.addTransaction(transaction4);
    }
}
