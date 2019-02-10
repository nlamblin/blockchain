import org.junit.AfterClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TraderTest {

    @AfterClass
    public static void initClass() {
        Main.traders.clear();
        Main.miners.clear();
    }

    @Test
    public void TestSendMoney() {
        Trader trader1 = new Trader("trader1", 10);
        Trader trader2 = new Trader("id123", "trader2", 5);
        Miner miner = new Miner("miner", 5);
        // miner.createBlock("####");
        // int initialSize = miner.getCurrentBlock().getTransactions().size();
        trader1.sendMoney("id123", 1);
        assertEquals(1, miner.getCurrentBlock().getTransactions().size());
    }

}
