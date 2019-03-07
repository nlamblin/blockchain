import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.List;
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
    private List<Transaction> toExecute;
    
    public List<Transaction> getToExecute() {
		return toExecute;
	}


	public Miner(String name, float balance) {
        super(name, balance);
        pending = new LinkedList<Transaction>();
        toExecute = new ArrayList<Transaction>();
	}


    public void createBlock() {
        String previousBlockHash = (Chain.getInstance().getBlocks().isEmpty()) ? "####" : Chain.getInstance().getBlocks().get(Chain.getInstance().getBlocks().size() - 1).getHash();
        this.currentBlock = new Block(previousBlockHash, this.name, new LinkedList<Transaction>(toExecute));
    } 
    
    public Miner call() {
    	
        createBlock();
        //Transactions supposées vérifiées rajoutées par la chaîne
        
        
        gpu = new GPU(currentBlock,this);
        executor = Executors.newSingleThreadExecutor();
        Future<GPU> f = executor.submit(gpu);
        while (!f.isDone()) {
        	if (executor.isShutdown()) {
        		f.cancel(false);
        	}
        }
		return this;
    }
    
    public synchronized void addBloc(Future<GPU> f) {
    	try {

			Block newBlock = f.get().currentBlock;
	    	Chain.getInstance().getBlocks().add(this.currentBlock);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void exchangeMoney(Transaction transaction) {
        User sender = Server.traders.get(transaction.getSender());
        User receiver = Server.traders.get(transaction.getReceiver());
        double amount = transaction.getAmount();
        sender.setBalance(sender.getBalance()-amount);
        receiver.setBalance(receiver.getBalance()+amount);
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
	public ExecutorService getExecutor() {
		return executor;
	}
    
}
