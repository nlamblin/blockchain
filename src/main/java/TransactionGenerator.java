import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public class TransactionGenerator implements Runnable{

	private Trader sender,receiver;
	public static final double MAX_PROPORTION = 0.55; //Max amount a trader will send 
	public static final double MIN_PROPORTION = 0.45;
	public static final int DELAY_BETWEEN_TRANSACTIONS = 1000; // Time in ms 
	public static AtomicBoolean running;
	
	public TransactionGenerator() {
		super();
	}
	
	@Override
	public void run() {
		running = new AtomicBoolean(true);
		while (true) {
			go();
			try {
				Thread.sleep(DELAY_BETWEEN_TRANSACTIONS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void kill() {
		this.running.set(false);
	}
	
	public void shuffle() {
		try {
			ArrayList<Trader> a = new ArrayList<Trader>(Server.traders.values());
			Collections.shuffle(a);
			sender = a.get(0);
			receiver = a.get(1);
		}
		catch (IndexOutOfBoundsException e) {
			System.out.println("Not enough traders for a transaction to take place.");
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
	
	
	
}
