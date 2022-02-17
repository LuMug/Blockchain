package ch.samt.blockchain.piccions.vm;

class Stack {

    private byte[] stack;
    private int stackSize;

    public Stack(int size) {
        this.stack = new byte[size];
        this.stackSize = 0;
    }

    public void allocFrame(int size) {
        stackSize += size;
    }

    public void deallocFrame(int size) {
        stackSize -= size;
    }

    // read from stack (stackframe)
    // offset should be negative
    public byte read(int offset) {
        return stack[stackSize + offset];
    }

    // read to stack (stackframe)
    // offset should be negative
    public void write(byte v, int offset) {
        stack[stackSize + offset] = v;
    }

    public void pushI8(byte v) {
        stack[stackSize++] = v;
    }
    
    public byte popI8() {
        return stack[--stackSize];
    }

    public void print() {
        System.out.println("------");
        for (int i = stackSize - 1; i >= 0; i--) {
            System.out.println("| " + stack[i] + " |");
        }
        System.out.println("------\n");
    }

}