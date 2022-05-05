package ch.samt.blockchain.nodeminer;

import java.util.LinkedList;
import java.util.List;

import ch.samt.blockchain.common.protocol.Protocol;
import ch.samt.blockchain.nodefull.DownloadListener;
import ch.samt.blockchain.nodefull.HighLevelNode;

public class MinerNode extends HighLevelNode implements DownloadListener {
    
    private byte[] wallet;
    private int cores = -1;
    private List<MinerCore> miners = new LinkedList<>();

    public MinerNode(int port, byte[] privKey, String db) {
        super(port, db);

        super.registerDownloadListener(this);

        wallet = Protocol.CRYPTO.sha256(
            Protocol.CRYPTO.publicKeyFromPrivateKey(
                Protocol.CRYPTO.privateKeyFromEncoded(privKey))
                .getEncoded());
    }

    public MinerNode(int port, byte[] privKey) {
        this(port, privKey, "blockchain_" + port + ".db");
    }

    public void startMining(int cores) {
        this.cores = cores;
        // minig starts at onDownloadEnd()
    }

    @Override
    public void onDownloadStart() {
        for (var miner : miners) {
            miner.interrupt();
        }
        miners.clear();
    }

    public synchronized boolean broadcastPoW(byte[] packet) {
        return super.broadcastPoW(packet, null, true);
    }

    @Override
    public void onDownloadEnd() {
        if (cores == -1) {
            return;
        }
                
        for (int i = 0; i < cores; i++) {
            try {
                Thread.sleep(5); // for random seed
                var miner = new MinerCore(super.miner, this, wallet);
                miner.start();
                miners.add(miner);
            } catch (InterruptedException e) {}
        }
    }

}
