package ch.samt.blockchain.forger;

import java.nio.file.Path;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;

import ch.samt.blockchain.common.protocol.Protocol;
import ch.samt.blockchain.common.protocol.SendTransactionPacket;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;

public class Forger {

    public static final String DEFAULT_KEY_FILE = "key.priv";

    public static void gen(String path) {
        var file = Path.of(path);

        if (Files.isDirectory(file)) {
            file = Path.of(path, DEFAULT_KEY_FILE);
        }

        var keyPair = Protocol.CRYPTO.generateKeyPair();

        var raw = Protocol.CRYPTO.getPrivateKey(keyPair).getEncoded();
        var content = Protocol.CRYPTO.toBase64(raw);

        writeFile(file, content.getBytes());

        System.out.println("\nPrivate key written to output\n");

        var pub = Protocol.CRYPTO.getPublicKey(keyPair);
        var address = Protocol.CRYPTO.getAddress(pub);

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
        System.out.println("Recipient:\t" + Protocol.CRYPTO.toBase64(packet.getRecipient()));
        System.out.println("Sender:\t\t" + Protocol.CRYPTO.getAddress(Protocol.CRYPTO.publicKeyFromEncoded(packet.getSenderPublicKey())));
        System.out.println("Signature:\t" + Protocol.CRYPTO.toBase64(packet.getSignature()));
        System.out.println("LastHash:\t" + Protocol.CRYPTO.toBase64(packet.getLastTransactionHash()));
    }

    public static void tx(Ed25519PrivateKeyParameters priv, String to, String amount, String outPath, byte[] lastTxHash) {
        var amountLong = 0L;
        try {
            amountLong = Long.parseLong(amount);
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount: " + amount);
            return;
        }
        
        var senderPublicKey = Protocol.CRYPTO.publicKeyFromPrivateKey(priv).getEncoded();
        var recipient = Protocol.CRYPTO.fromBase64(to);
        
        // Forge transaction

        var data = SendTransactionPacket.toSign(recipient, senderPublicKey, amountLong, lastTxHash);
        
        byte[] signature = null;
        try {
            signature = Protocol.CRYPTO.sign(data, priv);
        } catch (DataLengthException | CryptoException e) {
            System.err.println("Error while signing tx: " + e.getMessage());
        }
        
        var packet = SendTransactionPacket.create(recipient, senderPublicKey, amountLong, lastTxHash, signature);
        writeFile(outPath, packet);

        System.out.println("\nTransaction packet written to output\n");
    }

    public static void tx(String privkeyPath, String to, String amount, String outPath, String lastTxPath) {
        var raw = readFile(lastTxPath);

        var lastTxPacket = new SendTransactionPacket(raw);

        var lastHash = Protocol.CRYPTO.hashTx(
            lastTxPacket.getSenderPublicKey(),
            lastTxPacket.getRecipient(),
            lastTxPacket.getAmount(),
            lastTxPacket.getLastTransactionHash(),
            lastTxPacket.getSignature());

        var priv = loadPrivateKey(privkeyPath);

        tx(priv, to, amount, outPath, lastHash);
    }

    public static void tx(String privkeyPath, String to, String amount, String outPath, String server, int port) {
        String res;
        
        var priv = loadPrivateKey(privkeyPath);

        var address = Protocol.CRYPTO.getAddress(Protocol.CRYPTO.publicKeyFromPrivateKey(priv));

        try {
            var client = HttpClient.newHttpClient();
            var request = HttpRequest
                .newBuilder()
                .uri(URI.create("http://" + server + ":" + port + "/getLastTx/" + address.replaceAll("\\/", "%2F")))
                .POST(HttpRequest.BodyPublishers.noBody())
                .header("Accept", "application/json")
                .build();

            var response = client.send(request, HttpResponse.BodyHandlers.ofString());

            res = response.body();
        } catch (IOException | InterruptedException e) {
            System.err.println("Couldn't request last_tx_hash from server: " + e.getMessage());
            return;
        }

        JsonObject obj;

        try {
            obj = JsonParser.parseString(res).getAsJsonObject();
        } catch (JsonParseException e) {
            System.err.println("Invalid JSON response from server");
            return;
        }
        
        var status = obj.get("status");
        if (status == null) {
            System.err.println("Invalid JSON response from server");
            return;
        }

        var v = status.getAsString();

        byte[] raw;

        if (v.equals("Not Found")) {
            raw = new byte[32];
        } else {
            if (!v.equals("Ok")) {
                System.err.println("Error response for last hash request: " + v);
                return;
            }
    
            var lastHash = obj.get("hash");
    
            if (lastHash == null) {
                System.err.println("Invalid JSON response from server");
                return;
            }

            raw = Protocol.CRYPTO.fromBase64(lastHash.getAsString());
        }

        tx(priv, to, amount, outPath, raw);
    }

    public static void tx(String privkeyPath, String to, String amount, String outPath, boolean first) {
        tx(loadPrivateKey(privkeyPath), to, amount, outPath, new byte[32]);
    }

    private static Ed25519PrivateKeyParameters loadPrivateKey(String path) {
        byte[] raw = readFile(path);
        byte[] encoded = Protocol.CRYPTO.fromBase64(raw);

        var priv = Protocol.CRYPTO.privateKeyFromEncoded(encoded);

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
