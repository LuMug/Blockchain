package ch.samt.blockchain.website_backend;

import spark.Service;

public class WebServer implements HttpServer {

    public static final int MAX_THREADS = 20;

    private Service http;
    private int port;
    private String path;

    public WebServer(String path, int port) {
        this.path = path;
        this.port = port;
    }

    public WebServer(String path, Service http) {
        this.path = path;
        this.http = http;
    }

    @Override
    public void init() {
        if (http == null) {
            this.http = Service.ignite()
                    .port(port)
                    .threadPool(MAX_THREADS);
        }

        http.staticFiles.externalLocation(path);

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
