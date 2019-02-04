import java.util.UUID;

public class User {

    protected String id;
    protected String name;
    protected double balance;

    public User(String name, double balance) {
        this(UUID.randomUUID().toString(), name, balance);
    }

    public User(String id, String name, double balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;

        if(this instanceof Miner) {
            Main.miners.put(this.id, (Miner) this);
        }
        if(this instanceof Trader) {
            Main.traders.put(this.id, (Trader) this);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
