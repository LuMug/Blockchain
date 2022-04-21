package ch.samt.blockchain.nodefull;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ch.samt.blockchain.common.protocol.SendTransactionPacket;

public class Mempool {
    
    private Set<byte[]> txSigHashTable = new HashSet<>();

    private List<SendTransactionPacket> transactions = new LinkedList<>();

    public boolean contains(byte[] sig) {
        return txSigHashTable.contains(sig);
    }

    public void add(SendTransactionPacket packet) {
        transactions.add(packet);
        //transactions.subList(arg0, arg1)
    }

    public void clear() {
        txSigHashTable.clear();
        transactions.clear();
    }

}
