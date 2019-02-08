import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Random;

public class Miner extends User {

    public Block currentBlock;

    public Miner(String name, float balance) {
        super(name, balance);
    }

    public Miner(String id, String name, float balance) {
        super(id, name, balance);
    }

    public void fullProcess(String previousHash) {
        this.createBlock(previousHash);
        ArrayList<Transaction> transactions = this.findTransactions();
        this.addTransactionToBlock(transactions);
        this.mine();
    }

    public void createBlock(String previousHash) {
        this.currentBlock = new Block(previousHash);
    }

    public ArrayList<Transaction> findTransactions() {
        ArrayList<Transaction> transationsToValidate = new ArrayList<>();
        Random random = new Random();
        boolean blockIsFull = false;

        while (!blockIsFull) {
            int index = random.nextInt(Chain.getInstance().getTransactionsPool().size());
            Transaction transactionChecked = Chain.getInstance().getTransactionsPool().get(index);
            if(!transationsToValidate.contains(transactionChecked) && transactionChecked.getValidationStatus() == 2 && this.transactionIsValid(transactionChecked)) {
                transationsToValidate.add(transactionChecked);
                if(transationsToValidate.size() == Chain.BLOCK_SIZE) {
                    blockIsFull = true;
                }
            }
        }

        return transationsToValidate;
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
        for(Transaction transaction : transactions) {
            this.currentBlock.getTransactions().add(transaction);
        }
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

    public Block getCurrentBlock() {
        return this.currentBlock;
    }

    /*
    * ONLY FOR JUNIT TEST
     */
    public void addTransaction(Transaction transaction) {
        this.currentBlock.getTransactions().add(transaction);
    }
}
