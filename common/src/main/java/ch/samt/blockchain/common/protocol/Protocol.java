package ch.samt.blockchain.common.protocol;

import ch.samt.blockchain.common.utils.crypto.CryptoUtils;

public class Protocol {
    
    public final static byte REQUEST_NODES                      = 0x0;
    public final static byte SERVE_NODES                        = 0x1;
    public final static byte REGISTER_NODE                      = 0x2;
    public final static byte SEND_TRANSACTION                   = 0x3;
    public final static byte REQUEST_BLOCKCHAIN_LENGTH          = 0x4;
    public final static byte SERVE_BLOCKCHAIN_LENGTH            = 0x5;
    public final static byte REQUEST_IF_HASH_EXISTS             = 0x6;
    public final static byte SERVE_IF_HASH_EXISTS               = 0x7;
    public final static byte POW_SOLVED                         = 0x8;
    public final static byte REQUEST_DOWNLOAD                   = 0x9;
    public final static byte SERVE_OLD_TX                       = 0xA;
    public final static byte SERVE_OLD_POW                      = 0xB;
    public final static byte DOWNLOAD_DONE                      = 0xC;
    
    public static final CryptoUtils CRYPTO = new CryptoUtils();

    public static class Forger {

        public static final String DEFAULT_API_IP = "127.0.0.1";

        public static final int DEFAULT_API_PORT = 6767;
    
    }

    public static class Database {

        /**
         * The maximum number of cached nodes in the database.
         */
        public static final int MAX_CACHED_NODES = 50;

    }

    public static class Blockchain {

        /**
         * The reward for mining a block.
         */
        public static final int BLOCK_REWARD = 1000;

        /**
         * Every how much time a block should approxaimtely
         * be mined (ms).
         */
        public static final int BLOCK_RATE = 1000; // 120000

        /**
         * Every how many blocks to adjust the difficulty.
         */
        public static final int DIFFICULTY_ADJUSTMENT_RATE = 5; // 100

        /**
         * How many last blocks to consider when adjusting the difficulty.
         */
        public static final int DIFFICULTY_ADJUSTMENT_DEPTH = 6; // 100

        /**
         * The inizia difficulty.
         */
        public static final long INITIAL_DIFFICULTY = 1L; // 100

    }

    public static class Seeder {

        public static final int POOL_CAPACITY = 100;

        public static final int MAX_REQUEST = 10;

    }

    public static class Node {

        /**
         * Maximum number of connections to other nodes
         */
        public final static int MAX_CONNECTIONS = 120;

        public static final int MAX_TX_POOL_SIZE = 50;

        /**
         * Minimum numbers of connections to other nodes.
         * If this number is not reached the node will
         * periodically try to connect to more nodes.
         */
        public final static int MIN_CONNECTIONS = 100;

        /**
         * Interval in ms for the node to register to a random seeder.
         */
        public final static int REGISTER_INTERVAL = 30000;//180000;

        /**
         * Interval in ms for the node to try to reach MIN_CONNECTIONS
         * of connections if needed.
         */
        public final static int UPDATE_INTERVAL = 30000;//180000;

        /**
         * Maximum number of queries to the seeder a time.
         */
        public final static int MAX_TRIES_SEEDER = 5;

        /**
         * Maximum number of queries to the neighbours at a time.
         */
        public final static int MAX_TRIES_NEIGHBOUR = 5;

        public final static int DEFAULT_PORT = 5555;

    }

}
