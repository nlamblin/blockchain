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
        ArrayList<Transaction> transactions = this.findTransactionsToValidate();
        this.validateTransactions(transactions);
        if(!this.currentBlock.getTransactions().isEmpty()) {
            this.mine();
        }
    }

    public void createBlock(String previousHash) {
        this.currentBlock = new Block(previousHash);
    }

    public void addTransaction(Transaction transaction) {
        this.currentBlock.getTransactions().add(transaction);
    }

    public ArrayList<Transaction> findTransactionsToValidate() {
        ArrayList<Transaction> transationsToValidate = new ArrayList();
        Random random = new Random();

        while (transationsToValidate.size() < Chain.BLOCK_SIZE) {
            int index = random.nextInt(Chain.getInstance().getTransactionsPool().size() - 1);
            Transaction transactionChecked = Chain.getInstance().getTransactionsPool().get(index);
            if(!transactionChecked.getAlreadyValidate()) {
                transationsToValidate.add(transactionChecked);
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
            transaction.setAlreadyValidate();
        }
        this.currentBlock.setHash(hash);
        Chain.getInstance().addBlock(this.currentBlock);
    }

    public void validateTransactions(ArrayList<Transaction> transactions) {
        for(Transaction transaction : transactions) {
            boolean transactionIsValid = this.transactionIsValid(transaction);
            if(transactionIsValid) this.addTransaction(transaction);
        }
    }

    public boolean transactionIsValid(Transaction transactionToValidate) {
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
        else if(amount < Chain.MIN_AMOUNT) {
            transactionIsValid = false;
        }

        if(transactionIsValid) {
            sender.setBalance(senderBalance-amount);
            receiver.setBalance(receiver.getBalance()+amount);
        }
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

}
