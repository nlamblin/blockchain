import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

public class Chain {

    private volatile static Chain chain = null; // volatile for thread safety
    private volatile ArrayList<Block> blocks;
    public static int DIFFICULTY = 3;
    public static final int BLOCK_SIZE = 5;
    public static final double MIN_AMOUNT = 0.1;
    
    private Chain() {
        this.blocks = new ArrayList<Block>();
    }

    public synchronized void putNewTransaction(Transaction transaction) {
       Server.notify(transaction);
    }

    public static boolean isValid() {
        boolean result = true;
        int i = 1;
        while(i < Chain.getInstance().getBlocks().size() && result) {
        	if (!Chain.getInstance().getBlocks().get(i).getPreviousHash().equals(Chain.getInstance().getBlocks().get(i-1).getHash())) {
                result = false;
            }
            i++;
        }
        return result;
    }
    
    public String toString() {
        StringBuilder sb = new StringBuilder("num;mined_in(total);mined_in(cpu);mined_by:difficulty\n");
        int i = 0;
        for(Block block : blocks) {
            sb.append(i+";"+block.getTimeToMine()+";"+block.getCpuTimeToMine()+";"+block.getParent()+";"+block.getDifficulty()+"\n");
            i++;
        }
        return sb.toString();
    }

    public static Chain getInstance() {
        if(chain == null) {
            chain = new Chain();
        }
        return chain;
    }

    public void addBlock(Block block) {
        this.blocks.add(block);
    }

    public ArrayList<Block> getBlocks() {
        return this.blocks;
    }


    
}