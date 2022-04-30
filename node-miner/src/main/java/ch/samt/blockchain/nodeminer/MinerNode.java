package ch.samt.blockchain.nodeminer;

import java.util.Random;

import ch.samt.blockchain.common.protocol.PoWSolvedPacket;
import ch.samt.blockchain.common.protocol.Protocol;
import ch.samt.blockchain.nodefull.HighLevelNode;

public class MinerNode extends HighLevelNode {
    
    private byte[] wallet;

    public MinerNode(int port, byte[] privKey, String db) {
        super(port, db);

        wallet = Protocol.CRYPTO.sha256(
            Protocol.CRYPTO.publicKeyFromPrivateKey(
                Protocol.CRYPTO.privateKeyFromEncoded(privKey))
                .getEncoded());

        int core = Runtime.getRuntime().availableProcessors();
        System.out.println("Cores: " + core);
    }

    public MinerNode(int port, byte[] privKey) {
        this(port, privKey, "blockchain_" + port + ".db");
    }

    public void solvePoW() {
        byte[] nonce = new byte[32];

        var rand = new Random(System.currentTimeMillis());

        rand.nextBytes(nonce);

        broadcastPoW(
            PoWSolvedPacket.create(nonce, wallet, System.currentTimeMillis()),
            null,
            true
        );        
    }

}
