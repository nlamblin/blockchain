import java.util.HashMap;
import java.util.Map;

public class Main {

    public static Map<String, Trader> traders = new HashMap<>();
    public static Map<String, Miner> miners = new HashMap<>();

    public static void main(String[] args) {
        Chain chain = Chain.getInstance();

        Trader trader1 = new Trader("trader1", 10);
        Trader trader2 = new Trader("trader2", 20);
        Trader trader3 = new Trader("trader3", 25);

        Transaction transaction1 = new Transaction(2, trader1.getId(), trader2.getId());
        Transaction transaction2 = new Transaction(0.5, trader2.getId(), trader1.getId());
        Transaction transaction3 = new Transaction(1, trader1.getId(), trader3.getId());
        Transaction transaction4 = new Transaction(1.2, trader3.getId(), trader2.getId());
        Transaction transaction5 = new Transaction(2.8, trader1.getId(), trader2.getId());
        Transaction transaction6 = new Transaction(0.4, trader2.getId(), trader1.getId());
        Transaction transaction7 = new Transaction(1.9, trader1.getId(), trader3.getId());
        Transaction transaction8 = new Transaction(1.3, trader3.getId(), trader2.getId());

        Block block1 = new Block("###");
        block1.addTransaction(transaction1);
        block1.addTransaction(transaction2);
        block1.addTransaction(transaction3);
        block1.addTransaction(transaction4);
        block1.addTransaction(transaction5);
        block1.addTransaction(transaction6);
        block1.addTransaction(transaction7);
        block1.addTransaction(transaction8);
        block1.getMerkleRoot();
        block1.generateHash();
        chain.addBlock(block1);

        Transaction transaction9 = new Transaction(4.6, trader1.getId(), trader2.getId());
        Transaction transaction10 = new Transaction(0.5, trader2.getId(), trader1.getId());
        Transaction transaction11 = new Transaction(2.1, trader1.getId(), trader3.getId());
        Transaction transaction12 = new Transaction(2.2, trader3.getId(), trader2.getId());

        Block block2 = new Block(chain.getBlocks().get(chain.getBlocks().size()-1).getHash());
        block2.addTransaction(transaction9);
        block2.addTransaction(transaction10);
        block2.addTransaction(transaction11);
        block2.addTransaction(transaction12);
        block2.getMerkleRoot();
        block2.generateHash();
        chain.addBlock(block2);
    }
}
