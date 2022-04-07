package ch.samt.blockchain.forger;

import java.nio.file.Path;

import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;

import ch.samt.blockchain.common.protocol.SendTransactionPacket;
import ch.samt.blockchain.common.utils.crypto.CryptoUtils;

import java.io.IOException;
import java.nio.file.Files;

public class Forger {

    public static final String DEFAULT_KEY_FILE = "key.priv";

    private static CryptoUtils cryptoUtils;

    static {
        cryptoUtils = new CryptoUtils();
    }
    
    public static void gen(String path) {
        var file = Path.of(path);

        if (Files.isDirectory(file)) {
            file = Path.of(path, DEFAULT_KEY_FILE);
        }

        var keyPair = cryptoUtils.generateKeyPair();

        var raw = cryptoUtils.getPrivateKey(keyPair).getEncoded();
        var content = cryptoUtils.toBase64(raw);

        try {
            Files.write(file, content.getBytes());
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
            return;
        }

        System.out.println("\nPrivate key written to output\n");

        var pub = cryptoUtils.getPublicKey(keyPair);
        var address = cryptoUtils.getAddress(pub);

        System.out.println("Wallet Address: " + address);
        System.out.println();
    }

    public static void dump(String path) {
        var file = Path.of(path);

        byte[] raw = null;
        try {
            raw = Files.readAllBytes(file);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return;
        }

        SendTransactionPacket packet = null;
        try {
            packet = new SendTransactionPacket(raw);
        } catch (Exception e) {
            System.err.println("Invalid tx");
            return;
        }

        System.out.println("Amount: " + packet.getAmount());
        System.out.println("Timestamp: " + packet.getTimestamp());
        System.out.println("LastHash: " + packet.getLastTransactionHash());
        System.out.println("Recipient: " + cryptoUtils.toBase64(packet.getRecipient()));
        System.out.println("Sender: " + cryptoUtils.toBase64(packet.getSender()));
        System.out.println("Signature: " + cryptoUtils.toBase64(packet.getSignature()));
    }
    
    public static void tx(String privkeyPath, String to, String amount, String outPath) {
        //var priv = loadPrivateKey(privkeyPath);
        //var pub = cryptoUtils.publicKeyFromPrivateKey(priv);
        
        // Forge transaction

        //SendTransactionPacket.create(recipient, sender, amount, timestamp, lastTransactionHash, signature)
    }


    
    private static Ed25519PrivateKeyParameters loadPrivateKey(String path) {
        var file = Path.of(path);

        byte[] raw = null;
        try {
            raw = Files.readAllBytes(file);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return null;
        }

        byte[] encoded = cryptoUtils.fromBase64(raw);

        var priv = cryptoUtils.privateKeyFromEncoded(encoded);

        return priv;
    }

}
