import java.security.MessageDigest;

class Tools {

    static String applyHash(String data) {
        try {
            MessageDigest msgDigest = MessageDigest.getInstance("SHA-256");
            byte[] hash = msgDigest.digest(data.getBytes("UTF-8"));
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
}
