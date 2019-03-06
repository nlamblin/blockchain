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
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


public class Main {

    public static Map<PublicKey, Trader> traders = new HashMap<>();
    public static Map<PublicKey, Miner> miners = new HashMap<>();
    public static List<Transaction> currentRound = new ArrayList<>();
	public static volatile LinkedBlockingQueue<Transaction> pool = new LinkedBlockingQueue<>();
	static Iterator<Transaction> iterator = pool.iterator();
	 
	static List<Callable<Miner>> minersEnCours;
    
    public static void main(String[] args) throws InterruptedException {
        Trader trader1 = new Trader("trader1", 50);
        Trader trader2 = new Trader("trader2", 60);
        Trader trader3 = new Trader("trader3", 30);
        Miner miner = new Miner("miner", 1);
        Miner miner2 = new Miner("miner2", 1);
        Chain c = Chain.getInstance();
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
    	executorServiceMiners = Executors.newFixedThreadPool(3); // Pool d'users
		
		miners= new ArrayList<>();
		miners.add(miner);
		miners.add(miner2);
		
		minersEnCours = new ArrayList<Callable<Miner>>(miners);
		Miner fini;
		for (int i = 0 ; i < Chain.BLOCK_SIZE ; i++) {
			Transaction t = pool.poll();
			for(Map.Entry<PublicKey, Miner> entry : Main.miners.entrySet()) {
				entry.getValue().getToExecute().add(t);
	        }
		}

		//R1
		try {
			fini = executorServiceMiners.invokeAny(miners);
			Block newBlockOnTheBlock = fini.getCurrentBlock();
			Chain.getInstance().getBlocks().add(fini.getCurrentBlock());
			minersEnCours.remove(fini);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Appelle la méthode call de tous les users. L'exécution reprend quand l'un d'eux à fini
		
		for (Callable<Miner> u : minersEnCours) { // Shutdown des autres utilisateurs en passant par leur exécuteur
			Miner mu = (Miner)u;
			mu.getExecutor().shutdownNow();
		}

		/*
		 * Changin da pool
		 */
		
		miner.getToExecute().clear();
		miner2.getToExecute().clear();
		
		for (int i = 0 ; i < Chain.BLOCK_SIZE ; i++) {
			Transaction t = pool.poll();
			for(Map.Entry<PublicKey, Miner> entry : Main.miners.entrySet()) {
				entry.getValue().getToExecute().add(t);
	        }
		}
		
		try {
			fini = executorServiceMiners.invokeAny(miners);
			Block newBlockOnTheBlock = fini.getCurrentBlock();
			System.out.println("Adding: "+newBlockOnTheBlock);
			Chain.getInstance().getBlocks().add(fini.getCurrentBlock());
			minersEnCours.remove(fini);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Appelle la méthode call de tous les users. L'exécution reprend quand l'un d'eux à fini
		
		for (Callable<Miner> u : minersEnCours) { // Shutdown des autres utilisateurs en passant par leur exécuteur
			Miner mu = (Miner)u;
			mu.getExecutor().shutdownNow();
		}

		miner.getToExecute().clear();
		miner2.getToExecute().clear();
		
		for (int i = 0 ; i < Chain.BLOCK_SIZE ; i++) {
			Transaction t = pool.poll();
			for(Map.Entry<PublicKey, Miner> entry : Main.miners.entrySet()) {
				entry.getValue().getToExecute().add(t);
	        }
		}
		
		try {

	    	fini = executorServiceMiners.invokeAny(miners);
			majChain(fini);
			
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Appelle la méthode call de tous les users. L'exécution reprend quand l'un d'eux à fini
		
		for (Callable<Miner> u : minersEnCours) { // Shutdown des autres utilisateurs en passant par leur exécuteur
			Miner mu = (Miner)u;
			mu.getExecutor().shutdownNow();
		}
		

		if(miner.chainIsValid())
            System.out.println(Chain.getInstance().toString());
	    else
	        System.out.println("Chain is not valid !");
		
		
        
    }
    public static void validateNewTransaction(Transaction newTransaction) {
        if(transactionIsValid(newTransaction) && newTransaction.getValidationStatus() == 2) {
            Main.pool.add(newTransaction);
        }
        else {
            newTransaction.setValidationStatus(0);
        }
    }
    public static boolean transactionIsValid(Transaction transactionToValidate) {
        boolean transactionIsValid = true;

        User sender = Main.traders.get(transactionToValidate.getSender());
        User receiver = Main.traders.get(transactionToValidate.getReceiver());
        double amount = transactionToValidate.getAmount();

        if(receiver == null) {
            transactionIsValid = false;
        }
        else if(amount > sender.getBalance()) {
            transactionIsValid = false;
        }
        else if(amount < Chain.MIN_AMOUNT) {
            transactionIsValid = false;
        }
        else if(!verifySignature(transactionToValidate)) {
            transactionIsValid = false;
        }

        return transactionIsValid;
    }
    
    public static boolean verifySignature(Transaction transaction) {
        boolean result = false;
        try {
            Signature publicSignature = Signature.getInstance("SHA256withRSA");
            User sender = Main.traders.get(transaction.getSender());
            publicSignature.initVerify(sender.getPublicKey());
            Tools.updateForSignature(publicSignature, transaction);
            byte[] signatureBytes = Base64.getDecoder().decode(transaction.getSignature());
            result = publicSignature.verify(signatureBytes);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public static void notify(Transaction transaction) {
        validateNewTransaction(transaction);
	}
    
    public static synchronized void majChain(Miner fini) {
		Block newBlockOnTheBlock = fini.getCurrentBlock();
		System.out.println("Adding: "+newBlockOnTheBlock);
		Chain.getInstance().getBlocks().add(fini.getCurrentBlock());
		minersEnCours.remove(fini);
    }
}