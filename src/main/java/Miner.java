import java.security.Signature;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Random;

public class Miner extends User {

    private Block currentBlock;

    public Miner(String name, float balance) {
        super(name, balance);
    }

    public void notify(Transaction newTransaction) {
        this.createBlock();
        this.validateNewTransaction(newTransaction);
        this.miningProcess();
    }

    public void createBlock() {
        if(this.currentBlock == null) {
            String previousBlockHash = (Chain.getInstance().getBlocks().isEmpty()) ? "####" : Chain.getInstance().getBlocks().get(Chain.getInstance().getBlocks().size() - 1).getHash();
            this.currentBlock = new Block(previousBlockHash);
        }
    }

    public void validateNewTransaction(Transaction newTransaction) {
        if(this.transactionIsValid(newTransaction) && newTransaction.getValidationStatus() == 2) {
            this.currentBlock.getTransactions().add(newTransaction);
        }
        else {
            newTransaction.setValidationStatus(0);
        }
    }

    public boolean verifySignature(Transaction transaction) {
        boolean result = false;
        try {
            Signature publicSignature = Signature.getInstance("SHA256withRSA");
            User sender = Main.traders.get(transaction.getSender());
            publicSignature.initVerify(sender.getPublicKey());
            Tools.updateForSignature(publicSignature, transaction);
            byte[] signatureBytes = Base64.getDecoder().decode(transaction.getSignature());
            result = publicSignature.verify(signatureBytes);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
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
        else if(!this.verifySignature(transactionToValidate)) {
            transactionIsValid = false;
<<<<<<< HEAD
=======
            System.out.println("here");
>>>>>>> 86b76c4... feature/test : remove user id, use public and private key + sign method + verifySignature method
        }

        return transactionIsValid;
    }

    public void miningProcess() {
        if(this.currentBlock.getTransactions().size() == Chain.BLOCK_SIZE) {
            this.mine();
            Chain.getInstance().getBlocks().add(this.currentBlock);
            this.currentBlock = null;
        }
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
            exchangeMoney(transaction);
            transaction.setValidationStatus(1);
        }
        this.currentBlock.setHash(hash);
    }

    public void exchangeMoney(Transaction transaction) {
        User sender = Main.traders.get(transaction.getSender());
        User receiver = Main.traders.get(transaction.getReceiver());
        double amount = transaction.getAmount();
        sender.setBalance(sender.getBalance()-amount);
        receiver.setBalance(receiver.getBalance()+amount);
    }

    public boolean chainIsValid() {
        boolean result = true;
        int i = 1;
        while(i < Chain.getInstance().getBlocks().size() && result) {
            if (!Chain.getInstance().getBlocks().get(i).getPreviousHash().equals(Chain.getInstance().getBlocks().get(i-1).getHash())) {
                result = false;
            }
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
