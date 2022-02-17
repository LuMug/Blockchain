package ch.samt.blockchain.seeder;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * Represents a node
 */
public record Node(InetSocketAddress address, UUID uuid) {
    
}
