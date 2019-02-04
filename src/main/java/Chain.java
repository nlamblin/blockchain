import java.util.ArrayList;

public class Chain {

    private static Chain chain = null;
    private ArrayList<Block> blocks;
    public ArrayList<Transaction> transactionsNotYetValidated;
    public static final long DIFFICULTY = 0;
    public static final int BLOCK_SIZE = 16;
    public static final double MIN_AMOUNT = 0.1;

    private Chain() {
        this.blocks = new ArrayList<Block>();
        this.transactionsNotYetValidated = new ArrayList<Transaction>();
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

    public boolean isCorrect() {
        boolean result = true;
        int i = 1;
        System.out.println(blocks.size());
        while(i < this.blocks.size() || !result) {
            if (!this.blocks.get(i).getPreviousHash().equals(this.blocks.get(i-1).getHash()))
                result = false;
            i++;
        }
        return result;
    }

    public ArrayList<Block> getBlocks() {
        return this.blocks;
    }
}