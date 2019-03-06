import java.security.*;

public class User {

    protected String name;
    protected double balance;
    protected PublicKey publicKey;
    protected PrivateKey privateKey;
    protected GPU gpu;

    public User(String name, double balance) {
        this.name = name;
        this.balance = balance;
        this.generateKeys();

        if(this instanceof Miner) {
            Server.miners.put(this.publicKey, (Miner) this);
        }
        else if(this instanceof Trader) {
            Server.traders.put(this.publicKey, (Trader) this);
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
