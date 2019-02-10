import java.security.*;

public class User {

    protected String name;
    protected double balance;
    protected PublicKey publicKey;
    protected PrivateKey privateKey;

    public User(String name, double balance) {
        this.name = name;
        this.balance = balance;
        this.generateKeys();

        if(this instanceof Miner) {
            Main.miners.put(this.getPublicKey(), (Miner) this);
        }
        if(this instanceof Trader) {
            Main.traders.put(this.getPublicKey(), (Trader) this);
        }
    }

    private void generateKeys() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
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

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

}
