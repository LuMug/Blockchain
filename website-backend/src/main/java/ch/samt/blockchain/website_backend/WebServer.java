package ch.samt.blockchain.website_backend;

import spark.Service;

public class WebServer implements HttpServer {

    public static final int MAX_THREADS = 20;

    private Service http;
    private int port;

    public WebServer(int port) {
        this.port = port;
    }

    public WebServer(Service http) {
        this.http = http;
    }

    @Override
    public void init() {
        if (http == null) {
            this.http = Service.ignite()
                .port(port)
                .threadPool(MAX_THREADS);
        }

        http.staticFiles.externalLocation("/Users/paul/Desktop/blockchain/website-frontend");
        
        /*http.get("/", (req, res) -> {
            return "ciaoooo";
        });*/

        // Allow CORS
        http.after((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
            res.header("Content-Security-Policy", "default-src 'none'");
        });
    }

    @Override
    public void stop() {
        http.stop();
    }

}
