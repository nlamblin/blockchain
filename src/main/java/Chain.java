import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

public class Chain {

    private static Chain chain = null;
    private ArrayList<Block> blocks;
    public static final int DIFFICULTY = 3;
    public static final int BLOCK_SIZE = 4;
    public static final double MIN_AMOUNT = 0.1;
    private Stack<Transaction> transactions;

    private Chain() {
        this.blocks = new ArrayList<Block>();
        this.transactions = new Stack<Transaction>();
    }

    public void putNewTransaction(Transaction transaction) {
        transactions.push(transaction);
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

	public Stack<Transaction> getTransactions() {
		return transactions;
	}

    
}