import com.sun.jmx.snmp.SnmpStatusException;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Bloc {

    private String hash;
    private String previousHash;
    private String merkleRootHash;
    private ArrayList<Transaction> transactions;
    private Timestamp timestamp;
    private long nonce;

    public Bloc(String previousHash, ArrayList<Transaction> transactions, long nonce) {
        this.previousHash = previousHash;
        this.transactions = transactions;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.nonce = nonce;
        this.merkleRootHash = getMerkleRoot();
        this.hash = Tools.applyHash(this.previousHash + this.merkleRootHash + this.timestamp + this.nonce);
    }

    private String getMerkleRoot() {
        int countNodesLeft = this.transactions.size();
        ArrayList<String> previousLayer = new ArrayList<String>();
        for(Transaction t : this.transactions) {
            previousLayer.add(t.getHash());
        }
        ArrayList<String> layer = previousLayer;
        while(countNodesLeft > 1) {
            layer = new ArrayList<String>();
            for(int i = 1; i < previousLayer.size(); i = i+2) {
                // System.out.println(previousLayer.get(i-1) + " + " + previousLayer.get(i) + " = " + Tools.applyHash(previousLayer.get(i-1) + previousLayer.get(i)));
                layer.add(Tools.applyHash(previousLayer.get(i-1) + previousLayer.get(i)));
            }
            countNodesLeft = layer.size();
            previousLayer = layer;
        }
        return layer.get(0); // merkle root hash
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
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

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }
}
