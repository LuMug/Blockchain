package ch.samt.blockchain.forger;

import java.nio.file.Path;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
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

        writeFile(file, content.getBytes());

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

        System.out.println("Amount:\t\t" + packet.getAmount());
        System.out.println("Recipient:\t" + cryptoUtils.toBase64(packet.getRecipient()));
        System.out.println("Sender:\t\t" + cryptoUtils.getAddress(cryptoUtils.publicKeyFromEncoded(packet.getSenderPublicKey())));
        System.out.println("Signature:\t" + cryptoUtils.toBase64(packet.getSignature()));
        System.out.println("LastHash:\t" + cryptoUtils.toBase64(packet.getLastTransactionHash()));
    }

    public static void tx(String privKeyPath, String to, String amount, String outPath, byte[] lastTxHash) {
        var amountLong = 0L;
        try {
            amountLong = Long.parseLong(amount);
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount: " + amount);
            return;
        }
        
        var priv = loadPrivateKey(privKeyPath);
        var senderPublicKey = cryptoUtils.publicKeyFromPrivateKey(priv).getEncoded();
        var recipient = cryptoUtils.fromBase64(to);
        
        // Forge transaction

        var data = SendTransactionPacket.toSign(recipient, senderPublicKey, amountLong, lastTxHash);
        
        byte[] signature = null;
        try {
            signature = cryptoUtils.sign(data, priv);
        } catch (DataLengthException | CryptoException e) {
            System.err.println("Error while signing tx: " + e.getMessage());
        }
        
        var packet = SendTransactionPacket.create(recipient, senderPublicKey, amountLong, lastTxHash, signature);
        writeFile(outPath, packet);

        System.out.println("\nTransaction packet written to output\n");
    }

    public static void tx(String privkeyPath, String to, String amount, String outPath, String lastTxPath) {
        var lastTx = readFile(lastTxPath);

        tx(privkeyPath, to, amount, outPath, cryptoUtils.sha256(lastTx));
    }

    public static void tx(String privkeyPath, String to, String amount, String outPath) {
        // http request to get lastTx
        System.out.println("Not implemented yet");
    }

    public static void tx(String privkeyPath, String to, String amount, String outPath, boolean first) {
        tx(privkeyPath, to, amount, outPath, new byte[32]);
    }

    private static Ed25519PrivateKeyParameters loadPrivateKey(String path) {
        byte[] raw = readFile(path);
        byte[] encoded = cryptoUtils.fromBase64(raw);

        var priv = cryptoUtils.privateKeyFromEncoded(encoded);

        return priv;
    }

    private static byte[] readFile(String path) {
        var file = Path.of(path);

        try {
            return Files.readAllBytes(file);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(0);
            return null;
        }
    }

    private static void writeFile(String path, byte[] data) {
        var file = Path.of(path);

        try {
            Files.write(file, data);
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
            System.exit(0);
        }
    }

    private static void writeFile(Path path, byte[] data) {
        try {
            Files.write(path, data);
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
            System.exit(0);
        }
    }

}
