import java.security.PublicKey;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Transaction {

    private double amount;
    private String hash;
    private Timestamp timestamp;
    private PublicKey senderKey;
    private PublicKey receiverKey;
    private int validationStatus; // 0 : not validated, 1 : validated , 2 : not yet validated
    private String signature;

    public Transaction(double amount, PublicKey senderKey, PublicKey receiverKey) {
        this.amount = amount;
        this.senderKey = senderKey;
        this.receiverKey = receiverKey;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.validationStatus = 2;
        generateHash();
    }

    public void generateHash() {
        String senderKeyString = Tools.getStringFromKey(this.senderKey);
        String receiverKeyString = Tools.getStringFromKey(this.receiverKey);
        this.hash = Tools.applyHash(this.amount + senderKeyString + receiverKeyString + this.timestamp);
    }

    public double getAmount() { return this.amount; }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getHash() {
        return this.hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Timestamp getTimestamp() { return timestamp; }

    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public PublicKey getSender() { return senderKey; }

    public void setSenderKey(String sender) { this.senderKey = senderKey; }

    public PublicKey getReceiver() { return receiverKey; }

    public void setReceiverKey(PublicKey receiverKey) { this.receiverKey = receiverKey; }

    public int getValidationStatus() {
        return this.validationStatus;
    }

    public void setValidationStatus(int status) {
        this.validationStatus = status;
    }

    public String getSignature() {
        return this.signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String detailledView() {
        return "\t\t Hash : " + this.hash + "\n" +
            "\t\t Amount : " + this.amount + "\n" +
            "\t\t Sender : " + Tools.getStringFromKey(this.senderKey) + "\n" +
            "\t\t Receiver : " + Tools.getStringFromKey(this.receiverKey) + "\n" +
            "\t\t Timestamp : " + this.timestamp + "\n" +
            "\t\t Validated : " + this.validationStatus + "\n";
    }
    
    public String toString() {
    	NumberFormat formatter = new DecimalFormat("#0.00");     
    return "\nSender : " + Server.traders.get(this.senderKey).getName() + " " +
            "Receiver : " + Server.traders.get(this.receiverKey).getName() + " for "+formatter.format(this.amount)+
            " at: "+getTimestamp();
            	
    }
}
