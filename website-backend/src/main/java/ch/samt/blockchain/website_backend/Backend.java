package ch.samt.blockchain.website_backend;

import static spark.Spark.*;

public class Backend {

    public static void init(int port) {
        port(port);
        
        post("/getLatestBlocks/:from/:to", (req, res) -> {
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

            return """
                {
                    "blocks": [
                        { "id": 0, "hash": "aGFzaGRlbGJsb2Njb2hhc2hkZWxibG9jY28K", "nTx": 59 },
                        { "id": 1, "hash": "ZWFzdGVyZWdnZWFzdGVyZWdnZWFzdGVyQUFB", "nTx": 32 },
                        { "id": 2, "hash": "ZWRzdGVycmdnZWFmdGVyZWdzZWFzdmVyUkFB", "nTx": 3 }
                    ]
                }
            """;
        });
    }

    public static void stop() {
        spark.Spark.stop();
    }

    /*
        POST in JS

        async function postData(url = '', data = {}) {
            const response = await fetch(url, {
            method: 'POST',
            cache: 'no-cache',
            headers: {
                'Content-Type': 'application/json'
            },
            referrerPolicy: 'no-referrer',
                body: JSON.stringify(data)
            });
            return response.json();
        }

        postData('/post/path', { })
        .then(data => {
            
        });
    */

}