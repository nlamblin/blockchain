import java.util.ArrayList;

public class Chain {

    private static Chain chain = null;
    private ArrayList<Block> blocks;
    public static final long DIFFICULTY = 0;
    public static final int BLOCK_SIZE = 0;
    public static final double MIN_AMOUNT = 0.1;

    private Chain() {
        this.blocks = new ArrayList<Block>();
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