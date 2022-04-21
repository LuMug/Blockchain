package ch.samt.blockchain.nodefull;

import java.net.InetSocketAddress;

public class Seeders {

    public static final InetSocketAddress[] SEEDERS = {
        node("127.0.0.1", 4670),
        node("127.0.0.1", 4671),
        node("127.0.0.1", 4672)
    };

    private static InetSocketAddress node(String addr, int port) {
        return new InetSocketAddress(addr, port);
    }

}
