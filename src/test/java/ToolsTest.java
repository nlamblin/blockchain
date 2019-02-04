import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ToolsTest {

    @Test
    public void TestApplyHash() {
        String hash = Tools.applyHash("Some data to hash");
        assertEquals(hash, "f9d06a6c534e312190f2715b1d7feacc5c6c7edd39200a89aa47b7eaf42ce131");
    }

}
