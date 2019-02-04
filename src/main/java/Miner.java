public class Miner extends User {

    public Miner(String name, float balance) {
        super(name, balance);
    }

    public Miner(String id, String name, float balance) {
        super(id, name, balance);
    }

    public boolean validateTransaction(Transaction transactionToValidate) {
        boolean transactionIsValid = true;
        User sender = Main.traders.get(transactionToValidate.getSender());
        User receiver = Main.traders.get(transactionToValidate.getReceiver());
        double senderBalance = sender.getBalance();
        double amount = transactionToValidate.getAmount();

        if(receiver == null) {
            transactionIsValid = false;
        }
        else if(amount > senderBalance ) {
            transactionIsValid = false;
        }

        if(transactionIsValid) {
            sender.setBalance(senderBalance-amount);
            receiver.setBalance(receiver.getBalance()+amount);
            Chain.getInstance().transactionsNotYetValidated.remove(transactionToValidate);
        }
        return transactionIsValid;
    }

}
