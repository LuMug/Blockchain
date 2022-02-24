package ch.samt.blockchain.common.utils.crypto;

public class CryptoUtils {
    
    private static SHA256Digest sha256Digest;
    private static KeyPairGenerator ecKeyGen;

    static {
        try {
            Security.addProvider();

            sha256Digest = MessageDigest.getInstance("SHA-256");

            KeyPairGenerator ecKeyGen = KeyPairGenerator.getInstance("EC");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
            ecKeyGen.initialize(ecSpec);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }
    
    public static byte[] SHA256(byte[] digest) {
        return sha256Digest.digest(digest);
    }

    public static KeyPair generateKeyPair() {
        return ecKeyGen.generateKeyPair();
    }

    public static PublicKey fromPrivateKey(PrivateKey privateKey) {
        ECPrivateKey epvt = (ECPrivateKey) privateKey;

        return epvt.getS().toByteArray();
    }

}