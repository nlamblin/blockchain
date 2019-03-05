import java.security.Signature;
import java.util.Base64;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Miner extends User implements Callable{

    private Block currentBlock;
    private GPU gpu;
    private ExecutorService executor;
    private Queue<Transaction> pending;
    
    public Miner(String name, float balance) {
        super(name, balance);
        pending = new LinkedList<Transaction>();
    }


    public void createBlock() {
        if(this.currentBlock == null) {
            String previousBlockHash = (Chain.getInstance().getBlocks().isEmpty()) ? "####" : Chain.getInstance().getBlocks().get(Chain.getInstance().getBlocks().size() - 1).getHash();
            this.currentBlock = new Block(previousBlockHash, this.name);
            for (int i = 0 ; i < Chain.BLOCK_SIZE ; i++) {
            	//System.out.println("adding transaction: "+pending.peek());
            	this.currentBlock.getTransactions().add(pending.poll()); // LIFO on pending transactions 
            }
        }
    }

    public void validateNewTransaction(Transaction newTransaction) {
        if(this.transactionIsValid(newTransaction) && newTransaction.getValidationStatus() == 2) {
            this.pending.add(newTransaction);
        }
        else {
            newTransaction.setValidationStatus(0);
        }
    }

    public boolean verifySignature(Transaction transaction) {
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

    public boolean transactionIsValid(Transaction transactionToValidate) {
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
        else if(!this.verifySignature(transactionToValidate)) {
            transactionIsValid = false;
        }

        return transactionIsValid;
    }

    public Miner call() {
        while(this.pending.size() <= Chain.BLOCK_SIZE) {
        	Thread.yield(); // 
        }
        
        createBlock();
        gpu = new GPU(currentBlock,this);
        executor = Executors.newSingleThreadExecutor();
        Future<GPU> f = executor.submit(gpu);
        while (!f.isDone()) {
        	if (executor.isShutdown()) {
        		System.out.println(name+": cancelling.");
        		f.cancel(true);
        	}
        }
        if (!f.isCancelled()) {
        	//addBloc(f);
        }
        else {
        	System.out.println(name+": cancelled.");
        }
        //this.currentBlock = null; 
		return this;
    }
    
    public synchronized void addBloc(Future<GPU> f) {
    	try {

			Block newBlock = f.get().currentBlock;
	    	System.out.println(name+": solved the block. Adding it to the chain...");
	        Chain.getInstance().getBlocks().add(this.currentBlock);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void exchangeMoney(Transaction transaction) {
        User sender = Main.traders.get(transaction.getSender());
        User receiver = Main.traders.get(transaction.getReceiver());
        double amount = transaction.getAmount();
        sender.setBalance(sender.getBalance()-amount);
        receiver.setBalance(receiver.getBalance()+amount);
    }

    public boolean chainIsValid() {
        boolean result = true;
        int i = 1;
        while(i < Chain.getInstance().getBlocks().size() && result) {
        	System.out.println("crash possible");
        	System.out.println("expected size: "+Chain.getInstance().getBlocks().size());
            if (!Chain.getInstance().getBlocks().get(i).getPreviousHash().equals(Chain.getInstance().getBlocks().get(i-1).getHash())) {
                result = false;
                System.out.println("Hash annoncé comme précédent: "+Chain.getInstance().getBlocks().get(i-1).getHash());
                System.out.println("Hash précédent: "+Chain.getInstance().getBlocks().get(i).getPreviousHash());
            }
            i++;
        }
        return result;
    }

    public Block getCurrentBlock() {
        return this.currentBlock;
    }

    public void setCurrentBlock(Block block) {
        this.currentBlock = block;
    }

	public GPU getGpu() {
		return gpu;
	}

	public void setGpu(GPU gpu) {
		this.gpu = gpu;
	}


	public void notify(Transaction transaction) {
        this.validateNewTransaction(transaction);
	}


	public ExecutorService getExecutor() {
		return executor;
	}
    
}
