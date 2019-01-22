public class Transaction {

    private String data;
    private String hash;

    public Transaction(String data) {
        this.data = data;
    }

    public String getData() {
        return this.data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
