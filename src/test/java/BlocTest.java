import org.junit.Test;
import java.util.ArrayList;
import static org.junit.Assert.*;

public class BlocTest {

    @Test
    public void TestConstructor() {
        String previousHash = "77685afb04ce2a8b334464fa0508ebcc97b8a75695a97fede1794453906f1245";
        long nonce = 10388575;
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(new Transaction("Transaction1"));
        transactions.add(new Transaction("Transaction2"));
        transactions.add(new Transaction("Transaction3"));
        transactions.add(new Transaction("Transaction4"));

        Bloc b = new Bloc(previousHash, transactions, nonce);

        assertEquals(b.getPreviousHash(), previousHash);
        assertEquals(b.getNonce(), nonce);
        assertEquals(b.getTransactions().toString(), transactions.toString());
        assertNotNull(b.getTimestamp());
        assertNotNull(b.getHash());
    }

    @Test
    public void TestMerkleRootHash() {
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(new Transaction("Transaction1"));
        transactions.add(new Transaction("Transaction2"));
        transactions.add(new Transaction("Transaction3"));
        transactions.add(new Transaction("Transaction4"));

        Bloc b = new Bloc("77685afb04ce2a8b334464fa0508ebcc97b8a75695a97fede1794453906f1245", transactions, 10388575);
        assertEquals(b.getMerkleRootHash(), "2be3700f862f4052d5819a2c5c2cb8c5c82840ed75741084d457311b2f7d8a8c");
    }
}
