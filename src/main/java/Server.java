import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.security.PublicKey;
import java.security.Signature;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;


public class Server implements Runnable{

	public int TARGET_BLOCK_TIME; // bitcoin : 10min
	public int TARGET_NO_BLOCK; //144
    public static volatile Map<PublicKey, Trader> traders = new HashMap<>();
    public static volatile Map<PublicKey, Miner> miners = new HashMap<>();
	public static volatile LinkedBlockingQueue<Transaction> pool = new LinkedBlockingQueue<>();
	public static List<Callable<Miner>> callableMiners;
	public static ExecutorService executorServiceMiners;
	private static boolean isRunning;
	public static PrintWriter writer;
	
	static List<Callable<Miner>> minersEnCours;
    
    public static void init() {
    	Chain c = Chain.getInstance();
        isRunning = true;
    }
    
	public static void exchangeMoney(List<Transaction> transactions) {
    	for (Transaction transaction: transactions) {
    		User sender = Server.traders.get(transaction.getSender());
	        User receiver = Server.traders.get(transaction.getReceiver());
	        double amount = transaction.getAmount();
	        sender.setBalance(sender.getBalance()-amount);
	        receiver.setBalance(receiver.getBalance()+amount);
    	}
    }

	public static void validateNewTransaction(Transaction newTransaction) {
        if(transactionIsValid(newTransaction) && newTransaction.getValidationStatus() == 2) {
            Server.pool.add(newTransaction);
        }
        else {
            newTransaction.setValidationStatus(0);
        }
    }
    public static boolean transactionIsValid(Transaction transactionToValidate) {
        boolean transactionIsValid = true;

        User sender = Server.traders.get(transactionToValidate.getSender());
        User receiver = Server.traders.get(transactionToValidate.getReceiver());
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
            User sender = Server.traders.get(transaction.getSender());
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
    
    public static void sendTransactions() {
    	for (int i = 0 ; i < Chain.BLOCK_SIZE ; i++) {
			Transaction t = pool.poll();
			for(Map.Entry<PublicKey, Miner> entry : Server.miners.entrySet()) {
				entry.getValue().getToExecute().add(t);
	        }
		}
    }

	@Override
	public void run() {
		Miner firstMiner;		
		int blocNo = 0 ; 
		LinkedBlockingQueue<Transaction> pool = Server.pool;
		while (isRunning) {
			try {
				while (pool.size() < Chain.BLOCK_SIZE) 
					Thread.yield();
				sendTransactions();
				firstMiner = executorServiceMiners.invokeAny(callableMiners); // Appelle la méthode call de tous les users. L'exécution reprend quand l'un d'eux à fini
				Block newBlockOnTheBlock = firstMiner.getCurrentBlock();
				exchangeMoney(newBlockOnTheBlock.getTransactions());
				Chain.getInstance().getBlocks().add(firstMiner.getCurrentBlock());
				minersEnCours.remove(firstMiner);
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			} 
			
			
			for (Callable<Miner> u : callableMiners) { 
				((Miner) u).getToExecute().clear();
			}
			blocNo++; // increments variable blocNo by one
			
		}
	}
	
	public static void adjustDifficulty() {
		
	}


	/*
	 * Code to run when server shut down is called.
	 */
	public static void serverShutdown() {
		TransactionGenerator.setIsRunning(false);
		if(Chain.isValid())
            writer.write(Chain.getInstance().toString());
	    else
	        System.out.println("Chain is not valid !");
		writer.close();
	}
	

	public static void setRunning(boolean isRunning) {
		Server.isRunning = isRunning;
	}	

}