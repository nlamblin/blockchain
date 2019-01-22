public class Transaction {

    private String data;
    private String hash;

    public Transaction(String data) {
        this.data = data;
        this.hash = Tools.applyHash(this.data);
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHash() {
        return this.hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
