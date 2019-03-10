import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

public class ServerTest {    

    @Test
    public void TestRetrieveInitialAmount() throws InterruptedException {
    	Server s = new Server();
	    Server.init();
	    Thread main = new Thread(s);
	    main.start();
	    
	    Thread.sleep(5000);
	    s.setRunning(false);
	    
	    assertNotNull(null);
	}
}
