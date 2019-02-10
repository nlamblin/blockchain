import java.sql.Timestamp;
import java.util.Random;

public class Miner extends User {

    private Block currentBlock;

    public Miner(String name, float balance) {
        super(name, balance);
    }

    public Miner(String id, String name, float balance) {
        super(id, name, balance);
    }

    public void notify(Transaction newTransaction) {
        if(this.currentBlock == null) this.createBlock();
        this.validateNewTransaction(newTransaction);
    }

    public void createBlock() {
        String previousBlockHash = (Chain.getInstance().getBlocks().isEmpty()) ? "####" : Chain.getInstance().getBlocks().get(Chain.getInstance().getBlocks().size()-1).getHash();
        this.currentBlock = new Block(previousBlockHash);
    }

    public void validateNewTransaction(Transaction newTransaction) {
        if(this.transactionIsValid(newTransaction) && newTransaction.getValidationStatus() == 2) {
            this.currentBlock.getTransactions().add(newTransaction);
            if(this.currentBlock.getTransactions().size() == Chain.BLOCK_SIZE) {
                this.miningProcess();
            }
        }
        else {
            newTransaction.setValidationStatus(0);
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

        return transactionIsValid;
    }

    public void miningProcess() {
        this.mine();
        this.currentBlock = null;
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

    public void setCurrentBlock(Block block) {
        this.currentBlock = block;
    }
}
