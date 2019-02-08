import java.util.HashMap;
import java.util.Map;

public class Main {

    public static Map<String, Trader> traders = new HashMap<>();
    public static Map<String, Miner> miners = new HashMap<>();

    public static void main(String[] args) {
        Trader trader1 = new Trader("trader1", 50);
        Trader trader2 = new Trader("trader2", 60);
        Trader trader3 = new Trader("trader3", 30);
        Miner miner = new Miner("miner", 1);

        trader1.sendMoney(trader2.getId(), 2);
        trader2.sendMoney(trader1.getId(), 0.5);
        trader1.sendMoney(trader3.getId(), 1);
        trader3.sendMoney(trader2.getId(), 1.2);
        miner.fullProcess("###");

        trader1.sendMoney(trader2.getId(), 2.8);
        trader2.sendMoney(trader1.getId(), 0.4);
        trader1.sendMoney(trader3.getId(), 1.9);
        trader3.sendMoney(trader2.getId(), 1.3);
        miner.fullProcess(miner.getCurrentBlock().getHash());

        trader1.sendMoney(trader2.getId(), 4.6);
        trader2.sendMoney(trader1.getId(), 0.7);
        trader1.sendMoney(trader3.getId(), 2.1);
        trader3.sendMoney(trader2.getId(), 2.2);
        miner.fullProcess(miner.getCurrentBlock().getHash());

        miner.chainIsValid();

        System.out.println(Chain.getInstance().toString());
    }
}