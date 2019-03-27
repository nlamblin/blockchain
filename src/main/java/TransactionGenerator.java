import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public class TransactionGenerator implements Runnable{

	private Trader sender,receiver;
	public static final double MAX_PROPORTION = 0.15; //Max amount a trader will send 
	public static final double MIN_PROPORTION = 0.05;
	public static final int DELAY_BETWEEN_TRANSACTIONS = 500; // Time in ms 
	private static boolean isRunning;
	
	public TransactionGenerator() {
		super();
	}
	
	@Override
	public void run() {
		isRunning = true;
		while (isRunning) {
			go();
			try {
				Thread.sleep(DELAY_BETWEEN_TRANSACTIONS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void shuffle() {
		ArrayList<Trader> localTraders = new ArrayList<Trader>(Server.traders.values());
		if (localTraders.size() > 1) {
			Collections.shuffle(localTraders);
			sender = localTraders.get(0);
			receiver = localTraders.get(1);
		}
	}
	
	public Transaction createTransaction() {
		double proportion = ThreadLocalRandom.current().nextDouble(MIN_PROPORTION, MAX_PROPORTION);
		if (sender.getBalance()*proportion > sender.getBalance()) {
			return null;
		}
		sender.sendMoney(receiver.getPublicKey(), sender.getBalance()*proportion);
		return new Transaction(sender.getBalance()*proportion, sender.getPublicKey(), receiver.getPublicKey());
	}
	
	/*
	 * Whole thread action is in a synchronized method to ensure thread safety
	 */
	public synchronized void go(){
		shuffle();
		Transaction t = createTransaction();
	}

	public Trader getSender() {
		return sender;
	}

	public void setSender(Trader sender) {
		this.sender = sender;
	}

	public Trader getReceiver() {
		return receiver;
	}

	public void setReceiver(Trader receiver) {
		this.receiver = receiver;
	}

	public static boolean getIsRunning() {
		return isRunning;
	}

	public static void setIsRunning(boolean isRunning) {
		TransactionGenerator.isRunning = isRunning;
	}
	
	
	
	
	
}
