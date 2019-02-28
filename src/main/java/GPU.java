import java.sql.Timestamp;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * Class used to compute hash
 */
public class GPU implements Runnable{
	
	Block currentBlock;
	Miner parent;	
	private final AtomicBoolean running = new AtomicBoolean(false);
	
	public GPU(Block currentBlock, Miner parent) {
		super();
		this.currentBlock = currentBlock;
		this.parent = parent;
	}

	public void mine() {
		    String hash = "";
	        int nonce;
	        boolean found = false;
	        this.currentBlock.createMerkleTree();
	        while(!found) {
	            Random random = new Random();
	            nonce = random.nextInt(Integer.MAX_VALUE);
	            this.currentBlock.setTimestamp(new Timestamp(System.currentTimeMillis()));
	            this.currentBlock.setNonce(nonce);
	            hash = this.currentBlock.generateHash();
	            if(hash.substring(0, Chain.DIFFICULTY).matches("^[0]{"+ Chain.DIFFICULTY+"}$")) { // hash must starts by at least DIFFICULTY of 0
	                found = true;
	            }
	        }
	        for(Transaction transaction : this.currentBlock.getTransactions()) {
	            parent.exchangeMoney(transaction);
	            transaction.setValidationStatus(1);
	        }
	        this.currentBlock.setHash(hash);
	        System.out.println("(GPU) "+parent.name+": j'ai fini de miner! ");
	        Chain.getInstance().blockSolved(this.parent);
    }

	@Override
	public void run() {
		try {
			running.set(true);
			System.out.println("(GPU) Mining START");
			mine();
			System.out.println("(GPU) "+parent.name+" minage de "+currentBlock.getHash()+" terminé");
			parent.miningDone();
		}
		catch (NullPointerException e) {
			System.out.println();
		}	
		
	}

	public void kill() {
		running.set(false);
	}
}
