import java.util.ArrayList;

public class Chain {

    private static Chain chain = null;
    private ArrayList<Block> blocks;
    public ArrayList<Transaction> transactionsPool;
    public static final int DIFFICULTY = 3;
    public static final int BLOCK_SIZE = 4;
    public static final double MIN_AMOUNT = 0.1;

    private Chain() {
        this.blocks = new ArrayList<Block>();
        this.transactionsPool = new ArrayList<Transaction>();
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

    public ArrayList<Transaction> getTransactionsPool() {
        return this.transactionsPool;
    }

    public String toString() {
        String chainString = "";
        for(Block block : blocks) {
            chainString += "Block n°: " + blocks.indexOf(block) + "\n" +
                            block.toString() + "\n\n\n";
        }
        return chainString;
    }
}