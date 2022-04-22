package ch.samt.blockchain.webserver;

import spark.Service;

public class Webserver implements HttpServer {
    
    public static final int MAX_THREADS = 20;

    private Service http;
    private int port;
    private String wwwPath;

    public Webserver(int port, String wwwPath) {
        this.port = port;
        this.wwwPath = wwwPath;
    }

    public Webserver(Service http, String wwwPath) {
        this.http = http;
        this.wwwPath = wwwPath;
    }

    @Override
    public void init() {
        if (http == null) {
            this.http = Service.ignite()
                    .port(port)
                    .threadPool(MAX_THREADS);
        }

        http.staticFiles.externalLocation(wwwPath);

        // Allow CORS
        http.after((req, res) -> {
            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
            res.header("Content-Security-Policy", "default-src 'none'");
        });
    }

    @Override
    public void terminate() {
        http.stop();
    }

}
