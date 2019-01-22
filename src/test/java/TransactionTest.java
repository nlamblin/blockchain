import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TransactionTest {

    @Test
    public void TestConstructor() {
        String data = "Data";
        Transaction t = new Transaction(data);
        assertEquals(t.getData(), data);
        assertNotNull(t.getHash());
    }
}
