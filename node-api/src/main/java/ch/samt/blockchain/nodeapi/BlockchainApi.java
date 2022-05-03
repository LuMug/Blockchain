package ch.samt.blockchain.nodeapi;

import java.util.List;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import org.bouncycastle.crypto.util.DigestFactory;

import ch.samt.blockchain.common.protocol.Protocol;
import ch.samt.blockchain.nodefull.HighLevelNode;
import ch.samt.blockchain.nodefull.models.Block;
import ch.samt.blockchain.nodefull.models.Transaction;
import spark.Route;
import spark.Service;

public class BlockchainApi extends HighLevelNode implements HttpServer {

    public static final int MAX_THREADS = 20;

    private Service http;
    private int port;

    public BlockchainApi(int apiPort, int nodePort, String db) {
        super(nodePort, db);
        this.port = apiPort;
    }

    public BlockchainApi(Service http, int nodePort, String db) {
        super(nodePort, db);
        this.http = http;
    }

    public BlockchainApi(int apiPort, int nodePort) {
        super(nodePort);
        this.port = apiPort;
    }

    public BlockchainApi(Service http, int nodePort) {
        super(nodePort);
        this.http = http;
    }

    @Override
    public void init() {
        if (http == null) {
            this.http = Service
                    .ignite()
                    .port(port)
                    .threadPool(MAX_THREADS);
        }

        http.options("/*", (req, res) -> {
            String accessControlRequestHeaders = req.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                res.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
    
            String accessControlRequestMethod = req.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                res.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
    
            return "OK";
        });

        http.before((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Cache-Control", "no-cache");
            res.header("Access-Control-Allow-Headers", "Content-Type, Accept");
            res.header("Access-Control-Allow-Methods", "*");
            res.header("Access-Control-Max-Age", "1728000");
        });

        http.post("/getBlockchainHeight", getBlockchainHeight());
        http.post("/getBlock/:id", getBlock());
        http.post("/getUTXO/:address", getUTXO());
        http.post("/getTx/:hash", getTx());
        http.post("/deploy", deploy());
        http.post("/getTxs/:address", getTxs());
        http.post("/getLastTx/:address", getLastTxHash());
    }

    private Route getBlockchainHeight() {
        return (req, res) -> {
            res.type("application/json");
            res.status(200);

            int height = super.database.getBlockchainLength();

            return """
                        {
                            "status": "Ok",
                            "height": %
                        }
                    """.replace("%", Integer.toString(height));
        };
    }

    private Route getBlock() {
        return (req, res) -> {
            res.type("application/json");
            res.status(200);

            var param = req.params("id");
            int id = -1;

            try {
                id = Integer.parseInt(param);
            } catch (NumberFormatException e) {
                return status("Invalid ID");
            }

            var block = super.database.getBlock(id);

            if (block == null) {
                return status("Not Found");
            }

            return """
                    {
                        "status": "Ok",
                        %block
                    }
                    """
                        .replace("%block", serialize(block));
        };
    }

    private Route getUTXO() {
        return (req, res) -> {
            res.type("application/json");
            res.status(200);

            var base64 = req.params("address").replaceAll("%2F", "/");
            byte[] address;

            try {
                address = Protocol.CRYPTO.fromBase64(base64);
            } catch (IllegalArgumentException e) {
                return status("Invalid Address");
            }

            var utxo = super.database.getUTXO(address);

            if (utxo == -1) {
                return status("Not Found");
            }

            return """
                {
                    "status": "Ok",
                    "utxo": %utxo
                }        
            """.replace("%utxo", Long.toString(utxo));
        };
    }

    private Route getTx() {
        return (req, res) -> {
            res.type("application/json");
            res.status(200);

            var base64 = req.params("hash").replaceAll("%2F", "/");
            byte[] hash;

            try {
                hash = Protocol.CRYPTO.fromBase64(base64);
            } catch (IllegalArgumentException e) {
                return status("Invalid Hash");
            }

            var tx = super.database.getTransaction(hash);

            if (tx == null) {
                return status("Not Found");
            }

            return """
                    {
                        "status": "Ok",
                        %tx
                    }

                    """
                        .replace("%tx", serialize(tx));
        };
    }

    private Route getTxs() {
        return (req, res) -> {
            res.type("application/json");
            res.status(200);

            var base64 = req.params("address").replaceAll("%2F", "/");
            byte[] address;

            try {
                address = Protocol.CRYPTO.fromBase64(base64);
            } catch (IllegalArgumentException e) {
                return status("Invalid Address");
            }

            List<Transaction> txs = super.database.getTransactions(address);

            if (txs == null) {
                return status("Not Found");
            }

            var json = new StringBuilder("\"txs\":[");

            int i = 0;
            for (var tx : txs) {
                json.append("{" + serialize(tx) + "}");
                if (++i != txs.size()) {
                    json.append(",");
                }
            }

            json.append("]");

            return """
                    {
                        "status": "Ok",
                        %json
                    }
                    """
                        .replace("%json", json.toString());
        };
    }

    private Route deploy() {
        return (req, res) -> {
            res.type("application/json");
            res.status(200);

            MultipartConfigElement multipartConfigElement = new MultipartConfigElement("");
 
            req.raw().setAttribute("org.eclipse.jetty.multipartConfig", multipartConfigElement);

            Part uploadedFile = req.raw().getPart("file");

            byte[] file = uploadedFile.getInputStream().readAllBytes();

            var result = true;
            try {
                result = deployTx(file);
            } catch (Exception e) {
                result = false;
            }

            return status(result ? "Ok" : "Invalid Transaction");
        };
    }

    private Route getLastTxHash() {
        return (req, res) -> {
            res.type("application/json");
            res.status(200);

            var base64 = req.params("address").replaceAll("%2F", "/");
            byte[] address;

            try {
                address = Protocol.CRYPTO.fromBase64(base64);
            } catch (IllegalArgumentException e) {
                return status("Invalid Address");
            }

            var tx = super.database.getLastTransaction(address);

            if (tx == null) {
                return status("Not Found");
            }

            return """
                    {
                        "status": "Ok",
                        %tx
                    }
                    """
                        .replace("%tx", serialize(tx));
        };
    }

    private static String serialize(Block block) {
        return """
            "nonce": "%nonce",
            "difficulty": %difficulty,
            "miner": "%miner",
            "timestamp": %timestamp,
            "last_hash": "%last_hash",
            "hash": "%hash",
            "nTx": %nTx
        """
            .replace("%nonce", Protocol.CRYPTO.toBase64(block.nonce()))
            .replace("%difficulty", Long.toString(block.difficulty()))
            .replace("%miner", Protocol.CRYPTO.toBase64(block.miner()))
            .replace("%timestamp", Long.toString(block.timestamp()))
            .replace("%last_hash", Protocol.CRYPTO.toBase64(block.lastHash()))
            .replace("%hash", Protocol.CRYPTO.toBase64(block.hash()))
            .replace("%nTx", Integer.toString(block.nTx()));
    }

    private static String serialize(Transaction tx) {
        return """
            "blockId": %blockId,
            "sender": "%sender",
            "recipient": "%recipient",
            "amount": %amount,
            "timestamp": %timestamp,
            "lastTxHash": "%lastTxHash",
            "signature": "%signature",
            "hash": "%hash"
        """
            .replace("%blockId", Integer.toString(tx.blockId()))
            .replace("%sender", Protocol.CRYPTO.toBase64(Protocol.CRYPTO.sha256(tx.senderPub())))
            .replace("%recipient", Protocol.CRYPTO.toBase64(tx.recipient()))
            .replace("%amount", Long.toString(tx.amount()))
            .replace("%timestamp", Long.toString(tx.timestamp()))
            .replace("%lastTxHash", Protocol.CRYPTO.toBase64(tx.lastTxHash()))
            .replace("%signature", Protocol.CRYPTO.toBase64(tx.signature()))
            .replace("%hash", Protocol.CRYPTO.toBase64(tx.hash()));
    }

    private static String status(String status) {
        return "{\"status\":\"" + status + "\"}";
    }

    @Override
    public void terminate() {
        http.stop();
    }

}