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
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;

public class CryptoUtils {
    
    private static MessageDigest sha256Digest;
    private static KeyPairGenerator ecKeyGen;
    private static KeyFactory ecdsaKeyFactory;
    private static SecureRandom secureRandom;

    static {
        try {
            Security.addProvider(new BouncyCastleProvider());

            sha256Digest = MessageDigest.getInstance("SHA-256");

            ecdsaKeyFactory = KeyFactory.getInstance("ECDSA", "BC");

            secureRandom = new SecureRandom();

            X9ECParameters curveParams = CustomNamedCurves.getByName("Curve25519");
            ECParameterSpec ecSpec = new ECParameterSpec(curveParams.getCurve(), curveParams.getG(), curveParams.getN(), curveParams.getH(), curveParams.getSeed());
            ecKeyGen = KeyPairGenerator.getInstance("EC", "BC");
            ecKeyGen.initialize(ecSpec);
            
            ecKeyGen.initialize(ecSpec, secureRandom);
            ecKeyGen.initialize(ecSpec);
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }
    
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

    /*public static void main(String[] args) {
        var keypair = generateKeyPair();
        var pub = keypair.getPublic();
        var priv = keypair.getPrivate();
        
        System.out.println(toBase64(pub.getEncoded()));
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println(toBase64(priv.getEncoded()));
    
        try {
            var pub2 = publicKeyFromPrivateKey(priv);

            System.out.println();
            System.out.println();
            System.out.println();
        System.out.println(toBase64(pub2.getEncoded()));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    private static String toBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }*/

}