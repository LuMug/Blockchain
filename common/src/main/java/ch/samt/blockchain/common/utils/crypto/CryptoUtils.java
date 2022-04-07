package ch.samt.blockchain.common.utils.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class CryptoUtils {

    private MessageDigest sha256Digest;
    private SecureRandom secureRandom;
    private Ed25519KeyPairGenerator keyPairGenerator;
    private Signer signer;
    private Signer verifier;
    private Encoder base64Encoder;
    private Decoder base64Decoder;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }
    
    public CryptoUtils() {
        try {
            this.sha256Digest = MessageDigest.getInstance("SHA-256");
            this.secureRandom = new SecureRandom();
            this.keyPairGenerator = new Ed25519KeyPairGenerator();
            this.signer = new Ed25519Signer();
            this.verifier = new Ed25519Signer();
            this.base64Encoder = Base64.getEncoder();
            this.base64Decoder = Base64.getDecoder();

            keyPairGenerator.init(new Ed25519KeyGenerationParameters(secureRandom));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public byte[] SHA256(byte[] digest) {
        return sha256Digest.digest(digest);
    }

    public Ed25519PublicKeyParameters getPublicKey(AsymmetricCipherKeyPair keyPair) {
        return (Ed25519PublicKeyParameters) keyPair.getPublic();
    }

    public Ed25519PrivateKeyParameters getPrivateKey(AsymmetricCipherKeyPair keyPair) {
        return (Ed25519PrivateKeyParameters) keyPair.getPrivate();
    }

    public AsymmetricCipherKeyPair generateKeyPair() {
        return keyPairGenerator.generateKeyPair();
    }

    public byte[] sign(byte[] data, AsymmetricKeyParameter privKey)
            throws DataLengthException, CryptoException {
        signer.init(true, privKey);
        signer.update(data, 0, data.length);
        return signer.generateSignature();
    }

    public boolean verify(byte[] sig, byte[] data, AsymmetricKeyParameter publicKey) {
        verifier.init(false, publicKey);
        verifier.update(data, 0, data.length);
        return verifier.verifySignature(sig);
    }

    public Ed25519PrivateKeyParameters privateKeyFromEncoded(byte[] encoded) {
        return new Ed25519PrivateKeyParameters(encoded, 0);
    }

    public Ed25519PublicKeyParameters publicKeyFromPrivateKey(Ed25519PrivateKeyParameters privateKey) {
        return privateKey.generatePublicKey();
    }

    public byte[] sha256(byte[] digest) {
        sha256Digest.update(digest);
        return sha256Digest.digest();
    }

    /*public static void main(String[] args) throws CryptoException {

        var cu = new CryptoUtils();
        var keypair = cu.generateKeyPair();

        var data = "CIAOOO".getBytes();
        
        var sig = cu.sign(data, keypair.getPrivate());
        var res = cu.verify(sig, data, keypair.getPublic());
        System.out.println(res);

        System.out.println(toBase64(cu.getPublicKey(keypair).getEncoded()));
        var pub = cu.publicKeyFromPrivateKey(cu.getPrivateKey(keypair));
        System.out.println(toBase64(pub.getEncoded()));
    }
*/

    public String getAddress(Ed25519PublicKeyParameters publicKey) {
        return toBase64(sha256(publicKey.getEncoded()));
    }

    public String toBase64(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public byte[] fromBase64(byte[] data) {
        return Base64.getDecoder().decode(data);
    }

    public byte[] fromBase64(String data) {
        return Base64.getDecoder().decode(data);
    }

}