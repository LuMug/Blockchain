package ch.samt.blockchain.website_backend;

import static spark.Spark.*;

public class Backend {

    public static void init(int port) {
        port(port);
        get("/get", (req, res) -> "Hello GET");
        post("/post", (req, res) -> "Hello POST");
    }
    
}