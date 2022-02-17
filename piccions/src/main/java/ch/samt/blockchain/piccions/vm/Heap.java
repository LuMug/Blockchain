package ch.samt.blockchain.piccions.vm;

class Heap {

    private byte[] heap;

    private Chunk firstChunk;

    public Heap(int size) {
        this.heap = new byte[size];
        this.firstChunk = new Chunk(0, false);
    }

    public int allocate(int size) {
        if (size == 0) {
            throw new RuntimeException("memory allocation of length 0 is not supported");
        }

        Chunk current = firstChunk;
        Chunk next;

        while ((next = current.nextChunk()) != null) {
            if (next.getPointer() - current.getPointer() >= size && !current.isAllocated()) {
                current.setAllocated(next.getPointer() - current.getPointer());

                current.setAllocated(size);

                return current.getPointer();
            }

            current = next;
        }

        if (heap.length - current.getPointer() >= size && !current.isAllocated()) {
            current.setAllocated(size);
            return current.getPointer();
        }
        
        return -1;
    }

    public void free(int pointer) {
        Chunk currentChunk = firstChunk;
        Chunk lastChunk = null;

        do {
            if (currentChunk.getPointer() == pointer) {
                currentChunk.setAllocated(false);

                // Remove (after) free segmentation
                var nextChunk = currentChunk.nextChunk();
                if (nextChunk != null && !nextChunk.isAllocated()) {
                    currentChunk.setNext(nextChunk.nextChunk());
                }

                // After (after) free segmentation
                if (lastChunk != null && !lastChunk.isAllocated()) {
                    lastChunk.setNext(currentChunk.nextChunk());
                }

                return;
            }

            lastChunk = currentChunk;
        } while ((currentChunk = currentChunk.nextChunk()) != null);
    }

    private class Chunk {
        
        private int pointer;
        private boolean allocated;
        private Chunk nextPointer;

        public Chunk(int pointer, boolean allocated) {
            this.pointer = pointer;
            this.allocated = allocated;
        }

        public int getPointer() {
            return pointer;
        }

        public Chunk nextChunk() {
            return nextPointer;
        }

        public boolean isAllocated() {
            return allocated;
        }

        public void setAllocated(boolean allocated) {
            this.allocated = allocated;
        }

        public void setAllocated(int size) {
            setAllocated(true);
            
            int newNextPointer = nextPointer == null ? heap.length : nextPointer.getPointer();
            int avail = newNextPointer - size;

            if (avail != 0) {
                Chunk next = new Chunk(pointer + size, false);
                next.setNext(nextPointer);
                setNext(next);
            }
        }

        private void setNext(Chunk chunk) {
            this.nextPointer = chunk;
        }

    }

    public void print() {
        Chunk current = firstChunk;
        Chunk next;

        while ((next = current.nextChunk()) != null) {
            System.out.println(pad(Integer.toString(current.getPointer()), 3) + " " + (
                current.isAllocated() ? "+" : "-"
            ));

            current = next;
        }

        System.out.println(pad(Integer.toString(current.getPointer()), 3) + " " + (
            current.isAllocated() ? "+" : "-"
        ));

        System.out.println("---------");
    }

    private static String pad(String a, int n) {
        StringBuilder s = new StringBuilder();

        for (int i = 0; i < n - a.length(); i++) {
            s.append("0");
        }

        s.append(a);

        return s.toString();
    }

}