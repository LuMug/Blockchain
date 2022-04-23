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
        synchronized (signer) {
            signer.init(true, privKey);
            signer.update(data, 0, data.length);
            return signer.generateSignature();
        }
    }

    public boolean verify(byte[] sig, byte[] data, AsymmetricKeyParameter publicKey) {
        synchronized (verifier) {
            verifier.init(false, publicKey);
            verifier.update(data, 0, data.length);
            return verifier.verifySignature(sig);
        }
    }

    public Ed25519PrivateKeyParameters privateKeyFromEncoded(byte[] encoded) {
        return new Ed25519PrivateKeyParameters(encoded, 0);
    }

    public Ed25519PublicKeyParameters publicKeyFromEncoded(byte[] encoded) {
        return new Ed25519PublicKeyParameters(encoded, 0);
    }

    public Ed25519PublicKeyParameters publicKeyFromPrivateKey(Ed25519PrivateKeyParameters privateKey) {
        return privateKey.generatePublicKey();
    }

    public byte[] sha256(byte[] digest) {
        synchronized (sha256Digest) {
            return sha256Digest.digest(digest);
        }
    }

    public String getAddress(Ed25519PublicKeyParameters publicKey) {
        return toBase64(sha256(publicKey.getEncoded()));
    }

    public String toBase64(byte[] data) {
        return base64Encoder.encodeToString(data);
    }

    public byte[] fromBase64(byte[] data) {
        return base64Decoder.decode(data);
    }

    public byte[] fromBase64(String data) {
        return base64Decoder.decode(data);
    }

    public byte[] hashBlock(int id, int difficulty, byte[] txHash, byte[] nonce, byte[] miner, byte[] lastHash, long timestamp) {
        var result = sha256(toBytes(id));
        xor(result, sha256(toBytes(difficulty)));
        xor(result, sha256(txHash));
        xor(result, sha256(nonce));
        xor(result, sha256(miner));
        xor(result, sha256(lastHash));
        xor(result, sha256(toBytes(timestamp)));
        return result;
    }

    private static byte[] toBytes(int v) {
        return new byte[]{
            (byte) ((v >> 030) & 255),
            (byte) ((v >> 020) & 255),
            (byte) ((v >> 010) & 255),
            (byte) ((v >> 000) & 255)
        };
    }

    private static byte[] toBytes(long v) {
        return new byte[]{
            (byte) ((v >> 070) & 255),
            (byte) ((v >> 060) & 255),
            (byte) ((v >> 050) & 255),
            (byte) ((v >> 040) & 255),
            (byte) ((v >> 030) & 255),
            (byte) ((v >> 020) & 255),
            (byte) ((v >> 010) & 255),
            (byte) ((v >> 000) & 255)
        };
    }

    private static void xor(byte[] arr1, byte[] arr2) {
        for (int i = 0; i < arr1.length; i++) {
            arr1[i] ^= arr2[i];
        }
    }

}