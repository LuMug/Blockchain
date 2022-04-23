package ch.samt.blockchain.nodeapi;

import ch.samt.blockchain.common.protocol.Protocol;
import ch.samt.blockchain.nodefull.Block;
import ch.samt.blockchain.nodefull.HighLevelNode;
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

        //http.post("/getLatestTransactions/:from/:to", getLatestTransactions());
        http.post("/getBlockchainHeight", getBlockchainHeight());
        http.post("/getBlock/:id", getBlock());
        http.post("/getUTXO/:address", getUTXO());
    }

    /*
    private Route getLatestTransactions() {
        return (req, res) -> {
            int from = 0;
            int to = 0;

            try {
                from = Integer.parseInt(req.params(":from"));
                to = Integer.parseInt(req.params(":to"));
            } catch (NumberFormatException e) {
                return "{}";
            }

            res.type("application/json");
            res.status(200);

            // Result example
            long timestamp = System.currentTimeMillis();
            return """
                        {
                            "transactions": [
                                { "from": "ZWFzdGVyZWdnZWFzdGVyZWdnZWFzdGVyQUFB", "to": "ZWRzdGVycmdnZWFmdGVyZWdzZWFzdmVyUkFB", "amount": 15000, "timestamp": %TIMESTAMP% },
                                { "from": "ZWFzdGVyZWdnZWFzdGVyZWdnZWFzdGVyQUFB", "to": "ZWRzdGVycmdnZWFmdGVyZWdzZWFzdmVyUkFB", "amount": 15000, "timestamp": %TIMESTAMP% },
                                { "from": "ZWFzdGVyZWdnZWFzdGVyZWdnZWFzdGVyQUFB", "to": "ZWRzdGVycmdnZWFmdGVyZWdzZWFzdmVyUkFB", "amount": 15000, "timestamp": %TIMESTAMP% }
                            ]
                        }
                    """
                    .replaceAll("%TIMESTAMP%", Long.toString(timestamp));
        };
    }*/

    private Route getBlockchainHeight() {
        return (req, res) -> {
            res.type("application/json");
            res.status(200);

            int height = super.database.getBlockchainLength();

            return """
                        {
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
                return error("Invalid ID");
            }

            var block = super.database.getBlock(id);

            if (block == null) {
                return error("Not Found");
            }

            return serialize(block);
        };
    }

    private Route getUTXO() {
        return (req, res) -> {
            res.type("application/json");
            res.status(200);

            var base64 = req.params("address");
            byte[] address;

            try {
                address = Protocol.CRYPTO.fromBase64(base64);
            } catch (IllegalArgumentException e) {
                return error("Invalid Address");
            }

            var utxo = super.database.getUTXO(address);

            if (utxo == -1) {
                return error("Not Found");
            }

            return """
                {
                    "status": "Ok",
                    "utxo": %utxo
                }        
            """.replace("%utxo", Long.toString(utxo));
        };
    }

    private static String serialize(Block block) {
        return """
            {
                "status": "Ok",
                "nonce": "%nonce",
                "miner": "%miner",
                "timestamp": %timestamp,
                "last_hash": "%last_hash",
                "hash": "%hash",
                "nTx": %nTx
            }
        """
            .replace("%nonce", Protocol.CRYPTO.toBase64(block.nonce()))
            .replace("%miner", Protocol.CRYPTO.toBase64(block.miner()))
            .replace("%timestamp", Long.toString(block.timestamp()))
            .replace("%last_hash", Protocol.CRYPTO.toBase64(block.lastHash()))
            .replace("%hash", Protocol.CRYPTO.toBase64(block.hash()))
            .replace("%nTx", Integer.toString(block.nTx()));
    }

    private static String error(String status) {
        return "{\"status\":\"" + status + "\"}";
    }

    @Override
    public void terminate() {
        http.stop();
    }

}