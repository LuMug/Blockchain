package ch.samt.blockchain.common.protocol;

public class Protocol {
    
    public final static byte REQUEST_NODES      = 0;
    public final static byte SERVE_NODES        = 1;
    public final static byte REGISTER_NODE      = 2;
    public final static byte SEND_TRANSACTION   = 3;

    public static class Node {

        /**
         * Maximum number of connections to other nodes
         */
        public final static int MAX_CONNECTIONS = 100;

        /**
         * Minimum numbers of connections to other nodes.
         * If this number is not reached the node will
         * periodically try to connect to more nodes.
         */
        public final static int MIN_CONNECTIONS = 500;

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
         * Maximum size of the persistent node cache.
         */
        public final static int MAX_NODE_CACHE_SIZE = 150;

        /**
         * Maximum number of queries to the seeder a time.
         */
        public final static int MAX_TRIES_SEEDER = 5;

        /**
         * Maximum number of queries to the neighbours at a time.
         */
        public final static int MAX_TRIES_NEIGHBOUR = 5;

 
    }

}
