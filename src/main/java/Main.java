import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main {

    public static Map<PublicKey, Trader> traders = new HashMap<>();
    public static Map<PublicKey, Miner> miners = new HashMap<>();

    public static void main(String[] args) {
        Trader trader1 = new Trader("trader1", 50);
        Trader trader2 = new Trader("trader2", 60);
        Trader trader3 = new Trader("trader3", 30);
        Miner miner = new Miner("miner", 1);
        Miner miner2 = new Miner("miner2", 1);

        trader1.sendMoney(trader2.getPublicKey(), 2);
        trader2.sendMoney(trader1.getPublicKey(), 0.5);
        trader1.sendMoney(trader3.getPublicKey(), 1);
        trader3.sendMoney(trader2.getPublicKey(), 1.2);

        trader1.sendMoney(trader2.getPublicKey(), 2.8);
        trader2.sendMoney(trader1.getPublicKey(), 0.4);
        trader1.sendMoney(trader3.getPublicKey(), 1.9);
        trader3.sendMoney(trader2.getPublicKey(), 1.3);

        trader1.sendMoney(trader2.getPublicKey(), 4.6);
        trader2.sendMoney(trader1.getPublicKey(), 0.7);
        trader1.sendMoney(trader3.getPublicKey(), 2.1);
        trader3.sendMoney(trader2.getPublicKey(), 2.2);

    	List<Callable<Miner>> miners;
    	ExecutorService executorServiceMiners; 
    	List<Callable<Miner>> minersEnCours;
    	executorServiceMiners = Executors.newFixedThreadPool(2); // Pool d'users
		
		miners= new ArrayList<>();
		miners.add(miner);
		miners.add(miner2);
		
		minersEnCours = new ArrayList<Callable<Miner>>(miners);
		
		User fini;
		try {
			fini = executorServiceMiners.invokeAny(miners);
			minersEnCours.remove(fini);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Appelle la méthode call de tous les users. L'exécution reprend quand l'un d'eux à fini
		
		for (Callable<Miner> u : minersEnCours) { // Shutdown des autres utilisateurs en passant par leur exécuteur
			Miner mu = (Miner)u;
			mu.getExecutor().shutdownNow();
		}
    	
        if(miner.chainIsValid())
            System.out.println("we did it\n"+Chain.getInstance().toString());
        else
            System.out.println("Chain is not valid !");
         
    }
}