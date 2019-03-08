import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

public class Trader extends User {

    public Trader(String name, float balance) {
        super(name, balance);
    }

    public Transaction sendMoney(PublicKey receiverKey, double amount) {
        Transaction transaction = new Transaction(amount, this.publicKey, receiverKey);
        this.sign(transaction);
        Chain.getInstance().putNewTransaction(transaction);
        return transaction;
    }

    public void sign(Transaction transaction) {
        try {
            Signature privateSignature = Signature.getInstance("SHA256withRSA");
            privateSignature.initSign(this.privateKey);
            Tools.updateForSignature(privateSignature, transaction);
            byte[] signature = privateSignature.sign();
            transaction.setSignature(Base64.getEncoder().encodeToString(signature));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
