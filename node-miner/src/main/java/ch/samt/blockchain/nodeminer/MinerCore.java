package ch.samt.blockchain.nodeminer;

import java.util.Random;

import ch.samt.blockchain.common.protocol.PoWSolvedPacket;
import ch.samt.blockchain.nodefull.Miner;

public class MinerCore extends Thread {
    
    private Miner miner;
    private MinerNode node;
    private byte[] wallet;

    public MinerCore(Miner miner, MinerNode node, byte[] wallet) {
        this.miner = miner;
        this.node = node;
        this.wallet = wallet;
    }

    @Override
    public void run() {
        byte[] nonce = new byte[32];
        var rand = new Random(System.currentTimeMillis());

        while (!isInterrupted()) {
            rand.nextBytes(nonce);

            if (miner.isMined(nonce)) {
                node.broadcastPoW(PoWSolvedPacket.create(nonce, wallet, System.currentTimeMillis()));
            }
        }
    }

}
