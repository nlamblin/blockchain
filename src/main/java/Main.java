import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static Map<PublicKey, Trader> traders = new HashMap<>();
    public static Map<PublicKey, Miner> miners = new HashMap<>();

    public static void main(String[] args) {
        Trader trader1 = new Trader("trader1", 50);
        Trader trader2 = new Trader("trader2", 60);
        Trader trader3 = new Trader("trader3", 30);
        Miner miner = new Miner("miner", 1);

        trader1.sendMoney(trader2.getPublicKey(), 2);
        trader2.sendMoney(trader1.getPublicKey(), 0.5);
        trader1.sendMoney(trader3.getPublicKey(), 1);
        trader3.sendMoney(trader2.getPublicKey(), 1.2);

        trader1.sendMoney(trader2.getPublicKey(), 2.8);
        trader2.sendMoney(trader1.getPublicKey(), 0.4);
        trader1.sendMoney(trader3.getPublicKey(), 1.9);
        trader3.sendMoney(trader2.getPublicKey(), 1.3);

        trader1.sendMoney(trader2.getPublicKey(), 4.6);
        trader2.sendMoney(trader1.getPublicKey(), 0.7);
        trader1.sendMoney(trader3.getPublicKey(), 2.1);
        trader3.sendMoney(trader2.getPublicKey(), 2.2);

        if(miner.chainIsValid())
            System.out.println(Chain.getInstance().toString());
        else
            System.out.println("Chain is not valid !");
    }
}