package ch.samt.blockchain.common.utils.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;

public class CryptoUtils {
    
    private static MessageDigest sha256Digest;
    private static KeyPairGenerator ecKeyGen;
    private static KeyFactory ecdsaKeyFactory;

    static {
        try {
            Security.addProvider(new BouncyCastleProvider());

            sha256Digest = MessageDigest.getInstance("SHA-256");

            ecdsaKeyFactory = KeyFactory.getInstance("ECDSA", "BC");

            KeyPairGenerator ecKeyGen = KeyPairGenerator.getInstance("EC");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1"); // Exception
            ecKeyGen.initialize(ecSpec, new SecureRandom());
            ecKeyGen.initialize(ecSpec);
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    /*
    https://metamug.com/article/security/sign-verify-digital-signature-ecdsa-java.html
    */
    
    public static byte[] SHA256(byte[] digest) {
        return sha256Digest.digest(digest);
    }

    public static KeyPair generateKeyPair() {
        return ecKeyGen.generateKeyPair();
    }

    public static PublicKey publicKeyFromPrivateKey(PrivateKey privateKey)
        throws InvalidKeySpecException {

        ECPrivateKey ecPrivateKey = (ECPrivateKey) privateKey;

        ECParameterSpec ecParams = ecPrivateKey.getParameters();
        ECPoint q = ecParams.getG().multiply(ecPrivateKey.getD());

        ECPublicKeySpec publicKeySpec = new ECPublicKeySpec(q, ecParams);

        return ecdsaKeyFactory.generatePublic(publicKeySpec);
    }

    public static void main(String[] args) {
        var keypair = generateKeyPair();
        var pub = keypair.getPublic();
        var priv = keypair.getPrivate();
        
        System.out.println(toBase64(pub.getEncoded()));
        System.out.println(toBase64(priv.getEncoded()));
    
        try {
            var pub2 = publicKeyFromPrivateKey(priv);


        System.out.println(toBase64(pub2.getEncoded()));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    private static String toBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

}