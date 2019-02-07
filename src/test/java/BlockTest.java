import org.junit.Test;

import java.sql.Timestamp;

import static org.junit.Assert.*;

public class BlockTest {

    @Test
    public void TestConstructor() {
        String previousHash = "77685afb04ce2a8b334464fa0508ebcc97b8a75695a97fede1794453906f1245";
        Block b = new Block(previousHash);

        assertEquals(b.getPreviousHash(), previousHash);
    }

    @Test
    public void TestMerkleRootHash() {
        Trader trader1 = new Trader("abc100", "trader1", 20);
        Trader trader2 = new Trader("def200", "trader2", 40);
        Trader trader3 = new Trader("ghi300", "trader3", 12);
        Miner miner = new Miner("miner", 1);

        Transaction transaction1 = new Transaction(1.1, trader1.getId(), trader2.getId());
        transaction1.setTimestamp(new Timestamp(1549299372));
        transaction1.generateHash();

        Transaction transaction2 = new Transaction(0.6, trader2.getId(), trader3.getId());
        transaction2.setTimestamp(new Timestamp(1549299482));
        transaction2.generateHash();

        Transaction transaction3 = new Transaction(0.4, trader3.getId(), trader1.getId());
        transaction3.setTimestamp(new Timestamp(1549299494));
        transaction3.generateHash();

        Transaction transaction4 = new Transaction(1.4, trader3.getId(), trader2.getId());
        transaction4.setTimestamp(new Timestamp(1549299497));
        transaction4.generateHash();

        miner.createBlock("####");
        miner.addTransaction(transaction1);
        miner.addTransaction(transaction2);
        miner.addTransaction(transaction3);
        miner.addTransaction(transaction4);
        miner.getCurrentBlock().createMerkleTree();
        assertEquals("979c6cf44f6ffd172542c45625766842a9345b036da54b7e575f1313cd220411", miner.getCurrentBlock().getMerkleRootHash());
    }
}
