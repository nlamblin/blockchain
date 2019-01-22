import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.*;

public class BlockTest {

    @Test
    public void TestConstructor() {
        String previousHash = "77685afb04ce2a8b334464fa0508ebcc97b8a75695a97fede1794453906f1245";
        Block b = new Block(previousHash);

        assertEquals(b.getPreviousHash(), previousHash);
        assertNotNull(b.getTimestamp());
    }

    @Test
    public void TestMerkleRootHash() {
        Transaction transaction1 = new Transaction("Transaction1");
        Transaction transaction2 = new Transaction("Transaction2");
        Transaction transaction3 = new Transaction("Transaction3");
        Transaction transaction4 = new Transaction("Transaction4");

        Block b = new Block("77685afb04ce2a8b334464fa0508ebcc97b8a75695a97fede1794453906f1245");
        b.addTransaction(transaction1);
        b.addTransaction(transaction2);
        b.addTransaction(transaction3);
        b.addTransaction(transaction4);
        b.getMerkleRoot();
        assertEquals(b.getMerkleRootHash(), "2be3700f862f4052d5819a2c5c2cb8c5c82840ed75741084d457311b2f7d8a8c");
    }

    @Test
    public void TestAddTransaction() {
        Transaction transaction1 = new Transaction("Transaction1");
        Block b = new Block("77685afb04ce2a8b334464fa0508ebcc97b8a75695a97fede1794453906f1245");
        int initialSize = b.getTransactions().size();
        b.addTransaction(transaction1);
        assertEquals(initialSize+1, b.getTransactions().size());
    }
}
