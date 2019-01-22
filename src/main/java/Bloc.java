import java.sql.Timestamp;
import java.util.ArrayList;

public class Bloc {

    private String hash;
    private String previousHash;
    // merkle root
    private ArrayList<Transaction> transactions;
    private Timestamp timestamp;
    private long nonce;

    public Bloc(String previousHash, ArrayList<Transaction> transactions, long nonce) {
        this.previousHash = previousHash;
        this.transactions = transactions;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.nonce = nonce;
        this.hash = Tools.applyHash(this.previousHash + this.timestamp + this.nonce); // ne pas oublier merkle tree
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

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }
}
