package ch.samt.blockchain.common.utils.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
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
import java.security.Signature;
import java.security.SignatureException;
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
    
    private MessageDigest sha256Digest;
    private KeyPairGenerator ecKeyGen;
    private KeyFactory ecdsaKeyFactory;
    private SecureRandom secureRandom;
    private Signature ecdsaSign;

    public CryptoUtils() {
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

            ecdsaSign = Signature.getInstance("SHA256withECDSA");
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchProviderException e) {
            e.printStackTrace();
        }
    }
    
    public byte[] SHA256(byte[] digest) {
        return sha256Digest.digest(digest);
    }

    public KeyPair generateECDSAKeyPair() {
        return ecKeyGen.generateKeyPair();
    }

    public PublicKey publicECDSAKeyFromPrivateKey(PrivateKey privateKey)
        throws InvalidKeySpecException {

        ECPrivateKey ecPrivateKey = (ECPrivateKey) privateKey;

        ECParameterSpec ecParams = ecPrivateKey.getParameters();
        ECPoint q = ecParams.getG().multiply(ecPrivateKey.getD());

        ECPublicKeySpec publicKeySpec = new ECPublicKeySpec(q, ecParams);

        return ecdsaKeyFactory.generatePublic(publicKeySpec);
    }

    public byte[] signSHA256withECDSA(byte[] data, PrivateKey privateKey) {
        try {
            ecdsaSign.initSign(privateKey);
            ecdsaSign.update(data);
            return ecdsaSign.sign();
        } catch (InvalidKeyException | SignatureException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean verifySHA256withECDSA(byte[] data, byte[] signature, PublicKey publicKey) {
        try {
            ecdsaSign.initVerify(publicKey);
            ecdsaSign.update(data);
            return ecdsaSign.verify(signature);
        } catch (InvalidKeyException | SignatureException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*public static void main(String[] args) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException, InvalidKeyException, SignatureException {
        // Bouncy Castle Provider
        Security.addProvider(new BouncyCastleProvider());
        
        // Secure random
        SecureRandom secureRandom = new SecureRandom();

        // Key generator
        X9ECParameters curveParams = CustomNamedCurves.getByName("Curve25519");
        ECParameterSpec ecSpec = new ECParameterSpec(curveParams.getCurve(), curveParams.getG(), curveParams.getN(), curveParams.getH(), curveParams.getSeed());
        KeyPairGenerator ecKeyGen = KeyPairGenerator.getInstance("EC", "BC");
        ecKeyGen.initialize(ecSpec);
        ecKeyGen.initialize(ecSpec, secureRandom);
        ecKeyGen.initialize(ecSpec);

        // Signer
        Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");

        // Generate Key pair
        var keypair = ecKeyGen.generateKeyPair();
        var pub = keypair.getPublic();
        var priv = keypair.getPrivate();

        // Signature
        byte[] data = "Some data".getBytes();
        ecdsaSign.initSign(priv);
        ecdsaSign.update(data);
        // java.security.SignatureException: Curve not supported: java.security.spec.ECParameterSpec@6069db5
        byte[] sig = ecdsaSign.sign();


    }

    // https://github.com/starkbank/ecdsa-java

    private static String toBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }*/

}