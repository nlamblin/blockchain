import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ServerTest {    

	@Test
	public void TestBalanceUnaffectedBeforeMining() {
	    DataFiller.initMiners();
	    Trader trader1 = new Trader("trader1", 50);
        Trader trader2 = new Trader("trader2", 60);
        trader1.sendMoney(trader2.getPublicKey(), 1);
        trader1.sendMoney(trader2.getPublicKey(), 1);
        trader1.sendMoney(trader2.getPublicKey(), 1);
        trader1.sendMoney(trader2.getPublicKey(), 1);
        
        assertEquals(trader1.getBalance(),50,0.05);

	}
    
}
