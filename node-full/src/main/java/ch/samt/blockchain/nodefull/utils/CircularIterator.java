package ch.samt.blockchain.nodefull.utils;

import java.util.Iterator;

/**
 * Iterator used to iterate an index over a circular path.
 * 
 * Example: length=5, start=3
 * 3,4,0,1,2
 
 * @param length the length of the iteration
 * @param start the starting point
 */
public record CircularIterator(int length, int start) implements Iterable<Integer> {

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