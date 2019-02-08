import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Random;

public class Miner extends User {

    public Block currentBlock;
    private ArrayList<Transaction> newTransactions;

    public Miner(String name, float balance) {
        super(name, balance);
        this.newTransactions = new ArrayList<>();
    }

    public Miner(String id, String name, float balance) {
        super(id, name, balance);
        this.newTransactions = new ArrayList<>();
    }

    public void miningProcess() {
        String previousHash = (Chain.getInstance().getBlocks().isEmpty()) ? "####" : Chain.getInstance().getBlocks().get(Chain.getInstance().getBlocks().size()-1).getHash();
        this.createBlock(previousHash);
        ArrayList<Transaction> validatedTransactions = this.validatedTransactions();
        this.addTransactionToBlock(validatedTransactions);
        this.mine();
        this.newTransactions.clear();
    }

    public void createBlock(String previousHash) {
        this.currentBlock = new Block(previousHash);
    }

    public ArrayList<Transaction> validatedTransactions() {
        ArrayList<Transaction> transationsValidated = new ArrayList<>();
        for(Transaction transactionChecked : this.newTransactions) {
            if(transactionChecked.getValidationStatus() == 2 && this.transactionIsValid(transactionChecked)) {
                transationsValidated.add(transactionChecked);
            }
        }

        return transationsValidated;
    }

    public void mine() {
        String hash = "";
        int nonce;
        boolean found = false;
        this.currentBlock.createMerkleTree();
        while(!found) {
            Random random = new Random();
            nonce = random.nextInt(Integer.MAX_VALUE);
            this.currentBlock.setTimestamp(new Timestamp(System.currentTimeMillis()));
            this.currentBlock.setNonce(nonce);
            hash = this.currentBlock.generateHash();
            if(hash.substring(0, Chain.DIFFICULTY).matches("^[0]{"+ Chain.DIFFICULTY+"}$")) { // hash must starts by at least DIFFICULTY of 0
                found = true;
            }
        }
        for(Transaction transaction : this.currentBlock.getTransactions()) {
            transaction.setValidationStatus(1);
            User sender = Main.traders.get(transaction.getSender());
            User receiver = Main.traders.get(transaction.getReceiver());
            double amount = transaction.getAmount();
            sender.setBalance(sender.getBalance()-amount);
            receiver.setBalance(receiver.getBalance()+amount);
        }
        this.currentBlock.setHash(hash);
        Chain.getInstance().addBlock(this.currentBlock);
    }

    public void addTransactionToBlock(ArrayList<Transaction> transactions) {
        this.currentBlock.setTransactions(transactions);
    }

    public boolean transactionIsValid(Transaction transactionToValidate) {
        boolean transactionIsValid = true;

        User sender = Main.traders.get(transactionToValidate.getSender());
        User receiver = Main.traders.get(transactionToValidate.getReceiver());
        double amount = transactionToValidate.getAmount();

        if(receiver == null) {
            transactionIsValid = false;
        }
        else if(amount > sender.getBalance()) {
            transactionIsValid = false;
        }
        else if(amount < Chain.MIN_AMOUNT) {
            transactionIsValid = false;
        }

        if(!transactionIsValid) transactionToValidate.setValidationStatus(0);

        return transactionIsValid;
    }

    public boolean chainIsValid() {
        boolean result = true;
        int i = 1;
        while(i < Chain.getInstance().getBlocks().size() || !result) {
            if (!Chain.getInstance().getBlocks().get(i).getPreviousHash().equals(Chain.getInstance().getBlocks().get(i-1).getHash()))
                result = false;
            i++;
        }
        return result;
    }

    public void notify(Transaction transaction) {
        newTransactions.add(transaction);
        if(this.newTransactions.size() == Chain.getInstance().BLOCK_SIZE) {
            this.miningProcess();
        }
    }

    public Block getCurrentBlock() {
        return this.currentBlock;
    }

    public ArrayList<Transaction> getNewTransactions() {
        return this.newTransactions;
    }

    /*
    * ONLY FOR JUNIT TEST
     */
    public void addTransaction(Transaction transaction) {
        this.currentBlock.getTransactions().add(transaction);
    }
}
