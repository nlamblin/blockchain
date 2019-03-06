import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Block {

    private String hash;
    private String previousHash;
    private String merkleRootHash;
    private List<Transaction> transactions;
    private Timestamp timestamp;
    private int nonce;
    private String parent;

    public Block(String previousHash, String parent, List<Transaction> toExecute) {
        this.previousHash = previousHash;
        this.transactions = new ArrayList<Transaction>();
        this.parent = parent;
        this.transactions = toExecute;
    }
    
    public Block(Block b) {
    	this.parent = b.parent; 
    	this.previousHash = b.previousHash;
    	this.transactions = b.transactions;
    }
    
    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.transactions = new ArrayList<Transaction>();
    }

    public String generateHash() {
        return Tools.applyHash(this.previousHash + this.merkleRootHash + this.timestamp + this.nonce);
    }

    public void createMerkleTree() {
        int countNodesLeft = this.transactions.size();
        ArrayList<String> previousLayer = new ArrayList<String>();
        for(Transaction t : this.transactions) {
            previousLayer.add(t.getHash());
        }
        ArrayList<String> layer = previousLayer;
        while(countNodesLeft > 1) {
            layer = new ArrayList<String>();
            int i = 0;
            while (previousLayer.size() - i > 1) {
                layer.add(Tools.applyHash(previousLayer.get(i) + previousLayer.get(i + 1)));
                i += 2;
            }
            if(previousLayer.size() - i == 1) {
                layer.add(previousLayer.get(previousLayer.size()-1));
            }
            countNodesLeft = layer.size();
            previousLayer = layer;
        }
        this.merkleRootHash = layer.get(0); // merkle root hash
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getMerkleRootHash() {
        return this.merkleRootHash;
    }

    public void setMerkleRootHash(String merkleRootHash) {
        this.merkleRootHash = merkleRootHash;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public String toString() {
        String transactionsString = "";
        for(Transaction transaction : this.transactions) {
            transactionsString += transaction.toString() + "\n";
        }

        return "\t Hash : " + this.hash + "\n" +
        		"\t Mined by : " + this.parent + "\n"	+
                "\t Previous hash : " + this.previousHash + "\n" +
                "\t Merkle root hash : " + this.merkleRootHash + "\n" +
                "\t Timestamp : " + this.timestamp + "\n" +
                "\t Nonce : " + this.nonce + "\n" +
                "\t Transactions : \n" + transactionsString;
    }
    
    public String getParent() {
    	return this.parent;
    }
}
