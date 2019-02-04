import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {

    @Before
    public void clearMaps() {
        Main.traders.clear();
        Main.miners.clear();
    }

    @Test
    public void TestConstructor1() {
        String name = "user";
        float balance = 10;
        User user = new User(name, balance);
        assertEquals(user.getName(), name);
        assertEquals(user.getBalance(), balance, 0.0);
        assertNotNull(user.getId());
    }

    @Test
    public void TestConstructor2() {
        String id = "123456abcdef";
        String name = "user";
        float balance = 10;
        User user = new User(id, name, balance);
        assertEquals(user.getName(), name);
        assertEquals(user.getBalance(), balance, 0.0);
        assertNotNull(id, user.getId());
    }

    @Test
    public void TestSendMoney() {
        Trader trader1 = new Trader("trader1", 10);
        int initialSize = Chain.getInstance().transactionsNotYetValidated.size();
        trader1.sendMoney("toto12345", 1);
        assertEquals(initialSize+1, Chain.getInstance().transactionsNotYetValidated.size());
    }

    @Test
    public void TestValidateTransaction_OK() {
        Trader trader1 = new Trader("trader1", 10);
        Trader trader2 = new Trader("id123", "trader2", 5);
        Miner miner = new Miner("miner", 1);

        trader1.sendMoney("id123", 1);
        Transaction transaction = Chain.getInstance().transactionsNotYetValidated.get(0);

        assertTrue(miner.validateTransaction(transaction));
        assertEquals(9, trader1.getBalance(), 0.0);
        assertEquals(6, trader2.getBalance(), 0.0);
    }

    @Test
    public void TestValideTransaction_ReceiverNotFound() {
        Trader trader1 = new Trader("trader1", 10);
        Miner miner = new Miner("miner", 1);
        trader1.sendMoney("id12345", 1);
        Transaction transaction = Chain.getInstance().transactionsNotYetValidated.get(0);

        assertFalse(miner.validateTransaction(transaction));
    }
}
