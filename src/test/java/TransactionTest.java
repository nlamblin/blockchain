import org.junit.AfterClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class TransactionTest {

    @AfterClass
    public static void initClass() {
        Main.traders.clear();
        Main.miners.clear();
    }

    @Test
    public void TestConstructor() {
        double amount = 5;
        Trader trader1 = new Trader("trader1", 20);
        Trader trader2 = new Trader("trader2", 40);

        Transaction t = new Transaction(amount, trader1.getId(), trader2.getId());

        assertEquals(t.getAmount(), amount, 0.0);
        assertEquals(t.getSender(), trader1.getId());
        assertEquals(t.getReceiver(), trader2.getId());
        assertNotNull(t.getTimestamp());
        assertEquals(t.getValidationStatus(), 2);
    }

    @Test
    public void TestGenerateHash() {
        Transaction t = new Transaction(10, new Trader("trader1", 50).getId(), new Trader("trader2", 140).getId());
        t.generateHash();
        assertNotNull(t.getHash());
    }

    @Test
    public void TestAlreadyValidateTrue() {
        Transaction t = new Transaction(10, new Trader("trader1", 50).getId(), new Trader("trader2", 140).getId());
        t.setValidationStatus(1);
        assertEquals(1, t.getValidationStatus());
    }
}
