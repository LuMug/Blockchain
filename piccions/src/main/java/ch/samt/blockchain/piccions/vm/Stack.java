package ch.samt.blockchain.piccions.vm;

class Stack {

    private byte[] stack;
    private int stackSize;

    public Stack(int size) {
        this.stack = new byte[size];
        this.stackSize = 0;
    }

    public byte peek(int offset) {
        return stack[stackSize - offset];
    }

    public void pushI8(byte v) {
        stack[stackSize++] = v;
    }
    
    public byte popI8() {
        return stack[--stackSize];
    }

    public void dealloc(int off) {
        stackSize -= off;
    }

    public void write(byte value, int offset) {
        stack[stackSize - offset] = value;
    }

    public int size() {
        return stackSize;
    }

    public void print() {
        System.out.println("------");
        for (int i = stackSize - 1; i >= 0; i--) {
            System.out.println("| " + stack[i] + " |");
        }
        System.out.println("------\n");
    }

}