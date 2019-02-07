import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ChainTest {

    @Before
    public void before() {
        Chain.getInstance().getBlocks().clear();
    }

    @Test
    public void TestAddBlock() {
        Chain chain = Chain.getInstance();
        int initialSize = chain.getBlocks().size();
        chain.addBlock(new Block("77685afb04ce2a8b334464fa0508ebcc97b8a75695a97fede1794453906f1245"));
        assertEquals(initialSize+1, chain.getBlocks().size());
    }

    // @TODO
    /*@Test
    public void TestChainIsCorrect() {

    }*/

}
