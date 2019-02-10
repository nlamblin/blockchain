import java.util.ArrayList;
import java.util.Map;

public class Chain {

    private static Chain chain = null;
    private ArrayList<Block> blocks;
    public static final int DIFFICULTY = 3;
    public static final int BLOCK_SIZE = 4;
    public static final double MIN_AMOUNT = 0.1;

    private Chain() {
        this.blocks = new ArrayList<Block>();
    }

    public void putNewTransaction(Transaction transaction) {
        for(Map.Entry<String, Miner> entry : Main.miners.entrySet()) {
            entry.getValue().notify(transaction);
        }
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