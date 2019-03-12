import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


public class Main {

	public static final int TIME_TO_RUN = 600000;
	
    public static void main(String[] args) throws InterruptedException {
        Server s = new Server();
        Server.init();
        DataFiller.fill(2);
        Thread main = new Thread(s);
        main.start();
        
        while (Chain.getInstance().getBlocks().size() < 10) {
        	Thread.yield();
        }
                
        System.out.println("shutting down");
        Server.setRunning(false);
        
    }
}