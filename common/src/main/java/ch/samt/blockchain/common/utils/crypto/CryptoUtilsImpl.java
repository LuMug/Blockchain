package ch.samt.blockchain.common.utils.crypto;

public class CryptoUtils {
    
    private static SHA256Digest sha256Digest;
    private static KeyPairGenerator ecKeyGen;

    static {
        try {
            sha256Digest = MessageDigest.getInstance("SHA-256");

            KeyPairGenerator ecKeyGen = KeyPairGenerator.getInstance("EC");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
            ecKeyGen.initialize(ecSpec);;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }
    
    public static byte[] SHA256(byte[] digest) {
        return sha256Digest.digest(digest);
    }

    public static KeyPair generateKeyPair() {
        KeyPair kp = ecKeyGen.generateKeyPair();
        PublicKey pub = kp.getPublic();
        PrivateKey pvt = kp.getPrivate()
    }

}