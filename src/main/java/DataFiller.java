import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public class DataFiller {
	
	public static void fill() {
		initCsv();
		initTraders();
		initMiners();
	}
	
	public static void initTraders() {
    	Trader trader1 = new Trader("trader1", 50);
        Trader trader2 = new Trader("trader2", 60);
        Trader trader3 = new Trader("trader3", 30);
        
    	trader1.sendMoney(trader2.getPublicKey(), 2);
        trader2.sendMoney(trader1.getPublicKey(), 0.5);
        trader1.sendMoney(trader3.getPublicKey(), 1);
        trader3.sendMoney(trader2.getPublicKey(), 1.2);

        trader1.sendMoney(trader2.getPublicKey(), 2.8);
        trader2.sendMoney(trader1.getPublicKey(), 0.4);
        trader1.sendMoney(trader3.getPublicKey(), 1.9);
        trader3.sendMoney(trader2.getPublicKey(), 1.3);
        
        Thread t = new Thread(new TransactionGenerator());
        t.start();
    }
    
    public static void initMiners() {
    	Miner miner = new Miner("miner", 1);
        Miner miner2 = new Miner("miner2", 1);
        Miner miner3 = new Miner("miner3", 1);
        Miner miner4 = new Miner("miner4", 1);
    	Server.callableMiners = new ArrayList<>();
    	Server.callableMiners.add(miner);
    	Server.callableMiners.add(miner2);
    	Server.callableMiners.add(miner3);
    	Server.callableMiners.add(miner4);
    	Server.executorServiceMiners = Executors.newFixedThreadPool(Server.callableMiners.size()); // Pool d'users
    	Server.minersEnCours = new ArrayList<Callable<Miner>>(Server.callableMiners);
    }
    
    public static void initCsv() {
    	try {
    		LocalDateTime ldt = LocalDateTime.now();
    		int mo = ldt.getMonthValue();
    		int j = ldt.getDayOfMonth();
    		int h = ldt.getHour();
    		int m = ldt.getMinute();
    		int s = ldt.getSecond();
			Server.writer = new PrintWriter(new File("src/main/resources/"+j+"-"+mo+" "+h+":"+m+":"+s+".csv"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
