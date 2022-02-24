package ch.samt.blockchain.common.utils.crypto;

public class CryptoUtils {
    
    private static SHA256Digest sha256Digest;

    static {
        try {
            sha256Digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }
    
    public static byte[] SHA256(byte[] digest) {
        return sha256Digest.digest(digest);
    }

}