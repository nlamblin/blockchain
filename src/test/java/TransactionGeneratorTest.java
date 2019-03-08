import org.hamcrest.core.Is;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Timestamp;
import java.util.Base64;import java.util.GregorianCalendar;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class TransactionGeneratorTest {

    private static Trader trader1 = new Trader("trader1", 40);
    private static Trader trader2 = new Trader( "trader2", 20);
    private static TransactionGenerator tGen = new TransactionGenerator();


    @Before
    public void initTest() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] keys = new String[2];
        PublicKey[] publicKeys = new PublicKey[2];
        keys[0] = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAr+jR296B8EZDXsD019JfKbYgeN+l43Dp+Gag6bU4LwTsDEa6upj5wxLLPGwH+D2Y6aSmmAFU6m4mFeAcNDEwYZ2OpT5X2E8B3L38Lh0Kkx39AooiLWHNBIWUtIXuziXbxOBsBjh5QlwvUL/Op1T7VVtiGrrn8RGA76eUI5WhfDQUqvpCF8Ee0W7IyypmmXzJnYyCqa8WxqvBcK4yUfAsBAwzCh7D8eYXrbbI9EB1nR7JTmJ9aYdhhrgDoUCdLPPQ+XfBAX569a51iN3/A9IMqGFogGn8ILJhw5rNHb68EQuT/4sO6l28LxJOApdBBT+89jdr9mzwPyG8/WmA4U/ofQIDAQAB";
        keys[1] = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqRuhpS7YWRzPRdShYq/cPZZyfFQwG/28KSRmkUNRkeihBfUKjpHC0iIfUKu2ia1Sj3SUU+N3zn5etzih+hUodiyWWcaiu0ir8PunnBL0KcGTj+ASjDVjJWDbnS3anPXUZIOg5ULFdBEy1vycQbBNHN0eYaNqSTwSelug5oxa+vntqbfANaK5pN5GTgdGTEy4HrZc2S3EVWuYXYBz0+a/+js5gdVwf/tXnp3tv0/tDODRPWifyjsZN0brTvTNxbS7WFxYBK6Fq3kZm/yjGKEquR9pOEamyR/p8//YiGf+eGxI9vU3HLXBnQLe+/2nf6AQmatmrWAQCzXyNboq2RpRsQIDAQAB";
        for(int i = 0; i < keys.length; i++) {
            byte[] keyBytes = Base64.getDecoder().decode(keys[i]);
            publicKeys[i] = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyBytes));
        }
        trader1.setPublicKey(publicKeys[0]);
        trader2.setPublicKey(publicKeys[1]);

        Server.traders.clear();
        Server.miners.clear();
        tGen.setReceiver(null);
    	tGen.setSender(null);
    }
    
    @Test
    public void TestShuffleAffectationEnoughTraders() {
        Server.traders.put(trader1.getPublicKey(),trader1);
        Server.traders.put(trader2.getPublicKey(),trader2);
    	assertNull(tGen.getSender());
    	assertNull(tGen.getReceiver());
    	tGen.shuffle();
    	assertNotNull(tGen.getSender());
    	assertNotNull(tGen.getReceiver());
    }
    
    @Test
    public void TestShuffleAffectationNotEnoughTraders() {
        Server.traders.put(trader1.getPublicKey(),trader1);
    	assertNull(tGen.getSender());
    	assertNull(tGen.getReceiver());
    	tGen.shuffle();

    	boolean sNull, rNull;
    	sNull = tGen.getSender() == null ? true : false;
    	rNull = tGen.getReceiver() == null ? true : false;
    	
    	assertTrue(sNull || rNull);
    	
    }

    @Test
    public void TestRandomAmount() {
        Transaction t;
        tGen.setSender(trader1);
        tGen.setReceiver(trader2);
        
    	for (int i = 0 ; i < 100 ; i++) {
        	t = tGen.createTransaction();
        	assertThat(t.getAmount(), allOf(greaterThan(trader1.getBalance()*TransactionGenerator.MIN_PROPORTION)
        			, lessThan(trader1.getBalance()*TransactionGenerator.MAX_PROPORTION)));
    	}
    	
    }
}
