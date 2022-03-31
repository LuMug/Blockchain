package ch.samt.blockchain.website_backend;

import static spark.Spark.*;

import spark.Route;
import spark.Service;

public class BlockchainApi implements HttpServer {

    public static final int MAX_THREADS = 20;

    private Service http;
    private int port;

    public BlockchainApi(int port) {
        this.port = port;
    }

    public BlockchainApi(Service http) {
        this.http = http;
    }

    @Override
    public void init() {
        if (http == null) {
            this.http = Service.ignite()
                    .port(port)
                    .threadPool(MAX_THREADS);
        }

        http.post("/getLatestBlocks/:from/:to", getLatestBlocks());
        http.post("/getLatestTransactions/:from/:to", getLatestTransactions());
        http.post("/getBlockchainSize", getBlockchainSize());
    }

    private Route getLatestBlocks() {
        return (req, res) -> {
            // Get the latest 10 blocks
            // /getLatestBlocks/0/10

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
                            "blocks": [
                                { "id": 0, "timestamp": %TIMESTAMP%, "hash": "aGFzaGRlbGJsb2Njb2hhc2hkZWxibG9jY28K", "nTx": 59 },
                                { "id": 1, "timestamp": %TIMESTAMP%, "hash": "ZWFzdGVyZWdnZWFzdGVyZWdnZWFzdGVyQUFB", "nTx": 32 },
                                { "id": 2, "timestamp": %TIMESTAMP%, "hash": "ZWRzdGVycmdnZWFmdGVyZWdzZWFzdmVyUkFB", "nTx": 3 }
                            ]
                        }
                    """
                    .replaceAll("%TIMESTAMP%", Long.toString(timestamp));
        };
    }

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
    }

    private Route getBlockchainSize() {
        return (req, res) -> {
            res.type("application/json");
            res.status(200);

            return """
                        {
                            "size": "20 Mib"
                        }
                    """;
        };
    }

    @Override
    public void stop() {
        http.stop();
    }

    /*
     * POST in JS
     * 
     * async function postData(url = '', data = {}) {
     * const response = await fetch(url, {
     * method: 'POST',
     * cache: 'no-cache',
     * headers: {
     * 'Content-Type': 'application/json'
     * },
     * referrerPolicy: 'no-referrer',
     * body: JSON.stringify(data)
     * });
     * return response.json();
     * }
     * 
     * postData('/post/path', { })
     * .then(json => {
     * 
     * });
     */

}