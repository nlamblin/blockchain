import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class ChainTest {

    @Test
    public void TestAddBlock() {
        Chain chain = Chain.getInstance();
        int initialSize = chain.getBlocks().size();
        chain.addBlock(new Block("77685afb04ce2a8b334464fa0508ebcc97b8a75695a97fede1794453906f1245"));
        assertEquals(initialSize+1, chain.getBlocks().size());
    }

    @Test
    public void TestChainIsCorrect() {
        Chain chain = Chain.getInstance();

        Trader trader1 = new Trader("trader1", 10);
        Trader trader2 = new Trader("trader2", 20);
        Trader trader3 = new Trader("trader3", 25);

        Transaction transaction1 = new Transaction(2, trader1.getId(), trader2.getId());
        Transaction transaction2 = new Transaction(0.5, trader2.getId(), trader1.getId());
        Transaction transaction3 = new Transaction(1, trader1.getId(), trader3.getId());
        Transaction transaction4 = new Transaction(1, trader3.getId(), trader2.getId());

        Block block1 = new Block("###");
        block1.addTransaction(transaction1);
        block1.addTransaction(transaction2);
        block1.addTransaction(transaction3);
        block1.addTransaction(transaction4);
        block1.getMerkleRoot();
        block1.generateHash();
        chain.addBlock(block1);

        Transaction transaction5 = new Transaction(2.8, trader1.getId(), trader2.getId());
        Transaction transaction6 = new Transaction(0.4, trader2.getId(), trader1.getId());
        Transaction transaction7 = new Transaction(1.9, trader1.getId(), trader3.getId());
        Transaction transaction8 = new Transaction(1.3, trader3.getId(), trader2.getId());

        Block block2 = new Block(chain.getBlocks().get(chain.getBlocks().size()-1).getHash());
        block2.addTransaction(transaction5);
        block2.addTransaction(transaction6);
        block2.addTransaction(transaction7);
        block2.addTransaction(transaction8);
        block2.getMerkleRoot();
        block2.generateHash();
        chain.addBlock(block2);

        Transaction transaction9 = new Transaction(4.6, trader1.getId(), trader2.getId());
        Transaction transaction10 = new Transaction(0.5, trader2.getId(), trader1.getId());
        Transaction transaction11 = new Transaction(2.1, trader1.getId(), trader3.getId());
        Transaction transaction12 = new Transaction(2.2, trader3.getId(), trader2.getId());

        Block block3 = new Block(chain.getBlocks().get(chain.getBlocks().size()-1).getHash());
        block3.addTransaction(transaction9);
        block3.addTransaction(transaction10);
        block3.addTransaction(transaction11);
        block3.addTransaction(transaction12);
        block3.getMerkleRoot();
        block3.generateHash();
        chain.addBlock(block3);

        assertTrue(chain.isCorrect());
    }

}
