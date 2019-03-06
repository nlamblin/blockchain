import org.junit.AfterClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TraderTest {

    @AfterClass
    public static void clearClass() {
        Server.traders.clear();
        Server.miners.clear();
    }

    @Test
    public void TestSendMoney() {
        Trader trader1 = new Trader("trader1", 10);
        Trader trader2 = new Trader("trader2", 5);
        Miner miner = new Miner("miner", 5);
        trader1.sendMoney(trader2.getPublicKey(), 1);
        assertEquals(1, miner.getCurrentBlock().getTransactions().size());
        assertNotNull(miner.getCurrentBlock().getTransactions().get(0).getSignature());
    }

}
