import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.sql.Timestamp;
import java.util.Random;
import java.util.concurrent.Callable;

/*
 * Class used to compute hash
 */
public class GPU implements Callable{
	private Block currentBlock;
	private volatile Miner parent;
	ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
    
	
	public GPU(Block currentBlock, Miner parent) {
		super();
		this.currentBlock = new Block(currentBlock);
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
            transaction.setValidationStatus(1);
        }
        this.currentBlock.setHash(hash);
    }

	@Override
	public GPU call() {
		long wallStartTime = System.currentTimeMillis();
		long cpuStartTime = bean.getCurrentThreadCpuTime();
		mine();
		parent.setCurrentBlock(currentBlock);
		long cpuEndTime = bean.getCurrentThreadCpuTime();
		long wallEndTime = System.currentTimeMillis();
		currentBlock.setTimeToMine(wallEndTime - wallStartTime);
		currentBlock.setCpuTimeToMine((cpuEndTime - cpuStartTime)/1000000);
		return this;
	}

	public Block getCurrentBlock() {
		return currentBlock;
	}

	public Miner getParent() {
		return parent;
	}
	
	
	
	
}
