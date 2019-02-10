import java.security.Key;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

class Tools {

    static String applyHash(String data) {
        try {
            MessageDigest msgDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = msgDigest.digest(data.getBytes(UTF_8));
            StringBuilder hexaData = new StringBuilder();
            for (byte hash1 : hash) {
                String hexa = Integer.toHexString(0xff & hash1);
                if (hexa.length() == 1)
                    hexaData.append('0');
                hexaData.append(hexa);
            }
            return hexaData.toString();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    static void updateForSignature(Signature signature, Transaction transaction) throws SignatureException {
        String senderKeyString = Tools.getStringFromKey(transaction.getSender());
        String receiverKeyString = Tools.getStringFromKey(transaction.getReceiver());
        String data = transaction.getAmount() + senderKeyString + receiverKeyString + transaction.getTimestamp();
        signature.update(data.getBytes(UTF_8));
    }
}
