import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.security.PublicKey;
import java.security.Signature;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;


public class Server implements Runnable{

	public static final int TARGET_BLOCK_TIME = 60; // Every 60s we want a new block
	public static final int TARGET_NO_BLOCK = 10; // Every ten block we adjust
	public static final int ESTIMATED_ELAPSED_TIME_BLOCK = TARGET_BLOCK_TIME * TARGET_NO_BLOCK; // there should be 600s between intervals
	public static final int MAX_MINERS = 10;
	public static final int MIN_MINERS = 2;
	public final static int MAX_INCREASE_COEF=4;
	public final static double MIN_DECREASE_COEF=0.25;
	public static int TRANSACTION_BY_BLOCK;
	
	
	
    public static volatile Map<PublicKey, Trader> traders = new HashMap<>();
    public static volatile Map<PublicKey, Miner> miners = new HashMap<>();
	public static volatile LinkedBlockingQueue<Transaction> pool = new LinkedBlockingQueue<>();
	public static List<Callable<Miner>> callableMiners;
	public static ExecutorService executorServiceMiners;
	private static boolean isRunning;
	public static PrintWriter writer;
	private int blocNo;
	
	public static ArrayList<PrintWriter> writers = new ArrayList<PrintWriter>();
	
	
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
    
    public static synchronized void majChain(Miner winner) {
		Block newBlockOnTheBlock = winner.getCurrentBlock();
		System.out.println("Adding: "+newBlockOnTheBlock);
		Chain.getInstance().getBlocks().add(winner.getCurrentBlock());
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
		blocNo = 0 ; 
		LinkedBlockingQueue<Transaction> pool = Server.pool;
		while (isRunning) {
			try {
				while (pool.size() < Chain.BLOCK_SIZE) 
					Thread.yield();
				
				randomizeMiners();
				sendTransactions();
				firstMiner = executorServiceMiners.invokeAny(callableMiners); // Appelle la méthode call de tous les users. L'exécution reprend quand l'un d'eux à fini
				Block newBlockOnTheBlock = firstMiner.getCurrentBlock();
				/*
				 * DEMO
				 */
				System.out.println("size: "+Chain.getInstance().getBlocks().size()+" - "+" minage terminé en: "+newBlockOnTheBlock.getTimeToMine()+" (cpu: "+newBlockOnTheBlock.getCpuTimeToMine()+")");
				exchangeMoney(newBlockOnTheBlock.getTransactions());
				Chain.getInstance().getBlocks().add(newBlockOnTheBlock);				
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			
			for (Callable<Miner> u : callableMiners) { 
				((Miner) u).getToExecute().clear();
			}
			blocNo++; // increments variable blocNo by one
		}
		serverShutdown();		
	}
	
	private void randomizeMiners() {
		boolean changement = ThreadLocalRandom.current().nextBoolean();// TODO Auto-generated method stub
		boolean addOrRemove = ThreadLocalRandom.current().nextBoolean();
		
		if(changement && addOrRemove && (callableMiners.size() + 1 <= MAX_MINERS )) {
			Miner newMiner = new Miner("newKid "+blocNo,1);
			Server.callableMiners.add(newMiner);
	    	
		}
		else if (changement && !addOrRemove && (callableMiners.size() -1 >= MIN_MINERS)) {
			Miner oldMiner = (Miner) callableMiners.get(0);
			callableMiners.remove(0);
			miners.remove(oldMiner.getPublicKey());
		}
	}

	/*
	 * Based on https://bitcoin.stackexchange.com/questions/5838/how-is-difficulty-calculated
	 */
	public static void adjustDifficulty() {
		List<Block> blocks =  Chain.getInstance().getBlocks();
		
		
		int lastIndex = blocks.size()-1;
		int firstIndex = blocks.size()-(TARGET_NO_BLOCK);
		
		Block last = blocks.get(lastIndex);
		Block first = blocks.get(firstIndex);
		
		int elapsedSeconds = (int)(last.getTimestamp().getTime()-first.getTimestamp().getTime());
		
		double ratio = Math.round(elapsedSeconds / ESTIMATED_ELAPSED_TIME_BLOCK);
		
		ratio = ratio > MAX_INCREASE_COEF ? MAX_INCREASE_COEF : ratio;
		ratio = ratio < MIN_DECREASE_COEF ? MIN_DECREASE_COEF : ratio;
		Chain.DIFFICULTY *= ratio;
	}

	public static void addMiner(int i) {
		System.out.println("Nouveau mineur.");
		Miner m = new Miner("miner"+i,1);
		Server.callableMiners.add(m);
	}
	
	public static void clearMiners() {
		System.out.println("Suppression des mineurs.");
		Server.callableMiners.clear();
		addMiner(0);
	}

	/*
	 * Code to run for a proper server shutdown
	 */
	public static void serverShutdown() {
		TransactionGenerator.setIsRunning(false);
		if(Chain.isValid()) {
			System.out.println(Chain.getInstance().getBlocks());
		}
            //writer.write(Chain.getInstance().toString());
	    else
	        System.out.println("Chain is not valid !");
		writer.close();
	}
	

	public static void setRunning(boolean isRunning) {
		Server.isRunning = isRunning;
	}	

}