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
    private List<Transaction> toExecute;
    private int lag; // 
    
    public List<Transaction> getToExecute() {
		return toExecute;
	}
    
    public Miner(String name, float balance, int lag) {
        super(name, balance);
        this.lag = lag;
        toExecute = new ArrayList<Transaction>();
    }


	public Miner(String name, float balance) {
        super(name, balance);
        toExecute = new ArrayList<Transaction>();
        this.lag = 0;
	}


    public void createBlock() {
        String previousBlockHash = (Chain.getInstance().getBlocks().isEmpty()) ? "####" : Chain.getInstance().getBlocks().get(Chain.getInstance().getBlocks().size() - 1).getHash();
        this.currentBlock = new Block(previousBlockHash, this.name, new LinkedList<Transaction>(toExecute),Chain.getInstance().DIFFICULTY);
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

			Block newBlock = f.get().getCurrentBlock();
	    	Chain.getInstance().getBlocks().add(this.currentBlock);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
