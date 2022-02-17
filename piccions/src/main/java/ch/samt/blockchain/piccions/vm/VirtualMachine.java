package ch.samt.blockchain.piccions.vm;

public class VirtualMachine {

    private final static int MAX_STACK_SIZE = 4096;

    private byte[] bytecode;
    private Stack stack;

    public VirtualMachine(byte[] bytecode) {
        this.bytecode = bytecode;
        this.stack = new Stack(MAX_STACK_SIZE);
    }

    public void execute() {
        execute(0, bytecode.length);
    }

    private void execute(int offset, int length) {
        var cursor = new Cursor(offset, length);
        
        
    }

    private class Cursor { // bytecode slider

        private int position;
        private int end;
        private boolean isEnded;

        public Cursor(int offset, int length) {
            this.position = offset;
            this.end = offset + length;
        }
        
        public byte next() {
            if (position >= end - 1) {
                isEnded = true;
            }
    
            return bytecode[position++];
        }

        public void jump(int pos) {
            this.position = pos;

            isEnded = position >= end - 1;
        }

        public boolean isEnded() {
            return isEnded;
        }

    }

}