import java.security.PublicKey;
import java.security.Signature;
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


public class Server {

    public static volatile Map<PublicKey, Trader> traders = new HashMap<>();
    public static volatile Map<PublicKey, Miner> miners = new HashMap<>();
	public static volatile LinkedBlockingQueue<Transaction> pool = new LinkedBlockingQueue<>();
	public static List<Callable<Miner>> callableMiners;
	public static ExecutorService executorServiceMiners;
	
	static List<Callable<Miner>> minersEnCours;
    
    public static void go() throws InterruptedException {
    	Chain c = Chain.getInstance();
        initTraders();
        initMiners();
		Miner firstMiner;

		// Serveur loop
		int blocNo = 0 ; 
		LinkedBlockingQueue<Transaction> pool = Server.pool;
		while (true) {
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
		blocNo++; // incrementing variable blocNo by one
		
			if(Chain.isValid())
	            System.out.println(Chain.getInstance().toString());
		    else
		        System.out.println("Chain is not valid !");
		}

    }

    public static void exchangeMoney(List<Transaction> transactions) {
    	System.out.println(transactions.size());
    	for (Transaction transaction: transactions) {
    		System.out.println("transaction prise en compte. "+transactions.indexOf(transaction));
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
    	callableMiners= new ArrayList<>();
        callableMiners.add(miner);
        callableMiners.add(miner2);
        callableMiners.add(miner3);
		callableMiners.add(miner4);
		executorServiceMiners = Executors.newFixedThreadPool(callableMiners.size()); // Pool d'users
		minersEnCours = new ArrayList<Callable<Miner>>(callableMiners);
    }
}