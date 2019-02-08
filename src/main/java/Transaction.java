import java.sql.Timestamp;

public class Transaction {

    private double amount;
    private String hash;
    private Timestamp timestamp;
    private String senderId;
    private String receiverId;
    private int validationStatus; // 0 : not validated, 1 : validated , 2 : not yet validated

    public Transaction(double amount, String senderId, String receiverId) {
        this.amount = amount;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.validationStatus = 2;
        generateHash();
    }

    public void generateHash() {
        this.hash = Tools.applyHash(this.amount + this.senderId + this.receiverId + this.timestamp);
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

    public String getSender() { return senderId; }

    public void setSenderId(String sender) { this.senderId = senderId; }

    public String getReceiver() { return receiverId; }

    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    public int getValidationStatus() {
        return this.validationStatus;
    }

    public void setValidationStatus(int status) {
        this.validationStatus = status;
    }

    public String toString() {
        return "\t\t Hash : " + this.hash + "\n" +
            "\t\t Amount : " + this.amount + "\n" +
            "\t\t Sender : " + this.senderId + "\n" +
            "\t\t Receiver : " + this.receiverId + "\n" +
            "\t\t Timestamp : " + this.timestamp + "\n" +
            "\t\t Validated : " + this.validationStatus + "\n";
    }
}
