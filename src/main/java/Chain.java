import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

public class Chain {

    private volatile static Chain chain = null; // volatile for thread safety
    private volatile ArrayList<Block> blocks;
    public static final int DIFFICULTY = 5;
    public static final int BLOCK_SIZE = 4;
    public static final double MIN_AMOUNT = 0;
    
    private Chain() {
        this.blocks = new ArrayList<Block>();
    }

    public synchronized void putNewTransaction(Transaction transaction) {
       Server.notify(transaction);
    }

    public String toString() {
        String chainString = "";
        for(Block block : blocks) {
            chainString += "Block n°: " + blocks.indexOf(block) + "\n" +
                    block.toString() + "\n\n";
        }
        return chainString;
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