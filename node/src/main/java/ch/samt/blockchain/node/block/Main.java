package ch.samt.blockchain.node.block;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Block> blockchain = new ArrayList<>();
        int prefix = 2;
        String prefixString = new String(new char[prefix]).replace('\0', '0');

        Block newBlock = new Block(
                new byte[]{0,0,0,0,0,0,0},
                blockchain.get(blockchain.size() - 1).getHash(),
                new Date().getTime());
        newBlock.mineBlock(prefix);

        if(newBlock.getHash().substring(0, prefix).equals((prefixString))){
            blockchain.add(newBlock);
        }
    }
}
