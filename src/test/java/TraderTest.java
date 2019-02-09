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
        Miner miner = new Miner("miner", 5);
        int initialSize = miner.getNewTransactions().size();
        trader1.sendMoney("toto12345", 1);
        assertEquals(initialSize+1, miner.getNewTransactions().size());
    }

}
