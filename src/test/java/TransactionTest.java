import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TransactionTest {

    @Test
    public void TestConstructor() {
        String data = "Data";
        Transaction t = new Transaction(data);
        assertEquals(t.getData(), data);
    }
}
