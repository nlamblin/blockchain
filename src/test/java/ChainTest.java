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

        Transaction transaction1 = new Transaction("Data of transaction 1");
        Transaction transaction2 = new Transaction("Data of transaction 2");
        Transaction transaction3 = new Transaction("Data of transaction 3");
        Transaction transaction4 = new Transaction("Data of transaction 4");

        Block block1 = new Block("###");
        block1.addTransaction(transaction1);
        block1.addTransaction(transaction2);
        block1.addTransaction(transaction3);
        block1.addTransaction(transaction4);
        block1.getMerkleRoot();
        block1.generateHash();
        chain.addBlock(block1);

        Transaction transaction5 = new Transaction("Data of transaction 5");
        Transaction transaction6 = new Transaction("Data of transaction 6");
        Transaction transaction7 = new Transaction("Data of transaction 7");
        Transaction transaction8 = new Transaction("Data of transaction 8");

        Block block2 = new Block(chain.getBlocks().get(chain.getBlocks().size()-1).getHash());
        block2.addTransaction(transaction5);
        block2.addTransaction(transaction6);
        block2.addTransaction(transaction7);
        block2.addTransaction(transaction8);
        block2.getMerkleRoot();
        block2.generateHash();
        chain.addBlock(block2);

        Transaction transaction9 = new Transaction("Data of transaction 9");
        Transaction transaction10 = new Transaction("Data of transaction 10");
        Transaction transaction11 = new Transaction("Data of transaction 11");
        Transaction transaction12 = new Transaction("Data of transaction 12");

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
