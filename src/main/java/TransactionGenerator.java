import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class TransactionGenerator implements Runnable{

	private Trader sender,receiver;
	public static final double MAX_PROPORTION = 0.15; //Max amount a trader will send 
	public static final double MIN_PROPORTION = 0.05;
	public static final int DELAI_BETWEEN_TRANSACTIONS = 2000; // Time in ms 
	
	public TransactionGenerator() {
		super();
	}

	public static void main(String [] args) {
		Trader trader1 = new Trader("trader1", 40);
	    Trader trader2 = new Trader( "trader2", 20);
	    double max = trader2.getBalance() * MAX_PROPORTION;
	    double min = trader2.getBalance() * MIN_PROPORTION;
	    System.out.println(max+"-"+min);
	    
	    for (int i = 0 ; i < 10 ; i++)
	    	System.out.println(trader2.getBalance()*ThreadLocalRandom.current().nextDouble(MIN_PROPORTION, MAX_PROPORTION));
		

	    
	}
	
	@Override
	public void run() {
		while (true) {
			try {
					
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void shuffle() {
		try {
			ArrayList<Trader> a = new ArrayList<Trader>(Server.traders.values());
			Collections.shuffle(a);
			sender = a.get(0);
			receiver = a.get(1);
		}
		catch (IndexOutOfBoundsException e) {
			System.out.println("Not enough traders for a transaction to happens");
		}
	}
	
	public Transaction createTransaction() {
		double proportion = ThreadLocalRandom.current().nextDouble(MIN_PROPORTION, MAX_PROPORTION);
		return new Transaction(sender.getBalance()*proportion, sender.getPublicKey(), receiver.getPublicKey());
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
