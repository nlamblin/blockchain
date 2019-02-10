import org.junit.AfterClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class TransactionTest {

    private static Trader trader1 = new Trader("trader1", 20);
    private static Trader trader2 = new Trader("trader2", 40);

    @AfterClass
    public static void initClass() {
        Main.traders.clear();
        Main.miners.clear();
    }

    @Test
    public void TestConstructor() {
        double amount = 5;

        Transaction t = new Transaction(amount, trader1.getPublicKey(), trader2.getPublicKey());

        assertEquals(t.getAmount(), amount, 0.0);
        assertEquals(t.getSender(), trader1.getPublicKey());
        assertEquals(t.getReceiver(), trader2.getPublicKey());
        assertNotNull(t.getTimestamp());
        assertEquals(t.getValidationStatus(), 2);
    }

    @Test
    public void TestGenerateHash() {
        Transaction t = new Transaction(10, trader1.getPublicKey(), trader1.getPublicKey());
        t.generateHash();
        assertNotNull(t.getHash());
    }

    @Test
    public void TestAlreadyValidateTrue() {
        Transaction t = new Transaction(10, trader1.getPublicKey(), trader1.getPublicKey());
        t.setValidationStatus(1);
        assertEquals(1, t.getValidationStatus());
    }
}