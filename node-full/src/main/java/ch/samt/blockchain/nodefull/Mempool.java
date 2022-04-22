package ch.samt.blockchain.nodefull;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ch.samt.blockchain.common.protocol.SendTransactionPacket;

public class Mempool {
    
    // fast lookup
    private Set<byte[]> txSigHashTable = new HashSet<>();

    private List<SendTransactionPacket> transactions = new LinkedList<>();

    public boolean contains(byte[] sig) {
        return txSigHashTable.contains(sig);
    }

    public SendTransactionPacket drawOne() {
        var tx = transactions.get(0);
        transactions.remove(0);
        txSigHashTable.remove(tx.getSignature());
        return tx;
    }

    // TOOD using .sublist()

    public void add(SendTransactionPacket packet) {
        transactions.add(packet);
        txSigHashTable.add(packet.getSignature());
    }

    public void clear() {
        txSigHashTable.clear();
        transactions.clear();
    }

    public boolean isEmpty() {
        return transactions.size() == 0;
    }

}
