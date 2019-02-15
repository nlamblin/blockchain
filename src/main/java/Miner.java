import java.security.Signature;
import java.util.Base64;
import java.util.EmptyStackException;

public class Miner extends User implements Runnable{

    private Block currentBlock;
    private GPU gpu;
    private boolean gpuBusy;
    
    public Miner(String name, float balance) {
        super(name, balance);
        gpuBusy = false;
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
        }

        return transactionIsValid;
    }

    public void miningProcess() {
        if(this.currentBlock.getTransactions().size() == Chain.BLOCK_SIZE) {
            GPU gpu = new GPU(currentBlock,this);
        	//gpu.mine();
            gpuBusy = true;
            Thread t = new Thread(gpu);
			t.start();
            Chain.getInstance().getBlocks().add(this.currentBlock);
            this.currentBlock = null;
        }
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

	public GPU getGpu() {
		return gpu;
	}

	public void setGpu(GPU gpu) {
		this.gpu = gpu;
	}

	public void miningDone() {
		gpuBusy = false;
	}

	@Override
	public void run() {
		while (true) {
			if (!gpuBusy) {
				// looking for transactions
				try {
					Transaction newTransaction = Chain.getInstance().getTransactions().pop();
					System.out.println(name+"is taking care of: "+newTransaction.toString());
					this.createBlock();
			        this.validateNewTransaction(newTransaction);
			        this.miningProcess();
				}
				catch (EmptyStackException e) {
					try {
						System.out.println(name+": No transactions available: sleeping for a while.");
						Thread.sleep(5000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
			else { // Check if this is still relevant to mine
				System.out.println(name+" gpu is busy: try again later...");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
    
}
