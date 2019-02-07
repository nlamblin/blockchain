import java.sql.Timestamp;
import java.util.ArrayList;

public class Block {

    private String hash;
    private String previousHash;
    private String merkleRootHash;
    private ArrayList<Transaction> transactions;
    private Timestamp timestamp;
    private int nonce;

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
            for(int i = 1; i < previousLayer.size(); i = i+2) {
                layer.add(Tools.applyHash(previousLayer.get(i-1) + previousLayer.get(i)));
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

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
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
                "\t Previous hash : " + this.previousHash + "\n" +
                "\t Merkle root hash : " + this.merkleRootHash + "\n" +
                "\t Timestamp : " + this.timestamp + "\n" +
                "\t Nonce : " + this.nonce + "\n" +
                "\t Transactions : \n" + transactionsString;
    }
}
