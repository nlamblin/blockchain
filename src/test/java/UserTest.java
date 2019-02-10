import org.junit.AfterClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {

    @AfterClass
    public static void initClass() {
        Main.traders.clear();
        Main.miners.clear();
    }

    @Test
    public void TestConstructor() {
        String name = "user";
        float balance = 10;
        User user = new User(name, balance);
        assertEquals(user.getName(), name);
        assertEquals(user.getBalance(), balance, 0.0);
        assertNotNull(user.getPublicKey());
    }
}
