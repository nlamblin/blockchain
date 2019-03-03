import java.sql.Timestamp;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * Class used to compute hash
 */
public class GPU implements Callable{
	
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
    }

	@Override
	public GPU call() {
		running.set(true);
		System.out.println("(GPU) Mining START");
		mine();
		return this;
	}

	public void kill() {
		running.set(false);
	}
}
