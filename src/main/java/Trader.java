public class Trader extends User {

    public Trader(String name, float balance) {
        super(name, balance);
    }

    public Trader(String id, String name, float balance) {
        super(id, name, balance);
    }

    public void sendMoney(String receiver, double amount) {
        Transaction transaction = new Transaction(amount, this.id, receiver);
        Chain.getInstance().transactionsNotYetValidated.add(transaction);
    }

}
