package ch.samt.blockchain.node.utils;

import java.util.Iterator;

/**
 * Iterator used to iterate an index over a circular path.
 
 * @param length the length of the iteration
 * @param start the starting point
 */
public record CircleIterator(int length, int start) implements Iterable<Integer> {

    @Override
    public Iterator<Integer> iterator() {
        return new Iterator<Integer>() {

            private int index = start;
            private boolean first = true;

            @Override
            public boolean hasNext() {
                return first || index % length != start;
            }

            @Override
            public Integer next() {
                first = false;
                return index++ % length;
            }
            
        };
    }

}