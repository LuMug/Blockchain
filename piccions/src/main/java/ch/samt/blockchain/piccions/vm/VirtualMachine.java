package ch.samt.blockchain.piccions.vm;

import ch.samt.blockchain.piccions.bytecode.ByteCode;

public class VirtualMachine {

    private final static int MAX_STACK_SIZE = 4096;

    private byte[] bytecode;
    private Stack stack;

    public VirtualMachine(byte[] bytecode) {
        this.bytecode = bytecode;
        this.stack = new Stack(MAX_STACK_SIZE);
    }

    public void execute() {
        execute(bytecode, 0, bytecode.length);
    }

    private void execute(byte[] bytecode, int offset, int length) {
        var cursor = new Cursor(offset, length);
        
        loop:
        while (!cursor.isEnded()) {
            byte instruction = cursor.next();

            switch (instruction) {
                case ByteCode.PUSH_I8 -> {
                    stack.pushI8(cursor.next());
                }
                case ByteCode.PRINT_I8 -> {
                    byte v = stack.popI8();
                    System.out.println(v);
                }
                case ByteCode.ADD_I8 -> {
                    byte v1 = stack.popI8();
                    byte v2 = stack.popI8();
                    byte result = (byte) (v1 + v2);
                    stack.pushI8(result);
                }
                case ByteCode.SUB_I8 -> {
                    byte v1 = stack.popI8();
                    byte v2 = stack.popI8();
                    byte result = (byte) (v2 - v1);
                    stack.pushI8(result);
                }
                case ByteCode.MUL_I8 -> {
                    byte v1 = stack.popI8();
                    byte v2 = stack.popI8();
                    byte result = (byte) (v1 * v2);
                    stack.pushI8(result);
                }
                case ByteCode.DIV_I8 -> {
                    byte v1 = stack.popI8();
                    byte v2 = stack.popI8();
                    byte result = (byte) (v2 / v1);
                    stack.pushI8(result);
                }
                case ByteCode.EQUALS_I8 -> {
                    byte v1 = stack.popI8();
                    byte v2 = stack.popI8();
                    boolean result = v1 == v2;
                    stack.pushI8(result ? ByteCode.TRUE : ByteCode.FALSE);
                }
                case ByteCode.PRINT_BOOL -> {
                    boolean bool = stack.popI8() == ByteCode.TRUE;
                    System.out.println(bool);
                }
                case ByteCode.GOTO_A -> {
                    byte pos = cursor.next();
                    cursor.jump(pos);
                }
                case ByteCode.GOTO_B -> {
                    byte pos = cursor.next();
                    boolean bool = stack.popI8() == ByteCode.TRUE;
                    if (!bool) {
                        cursor.jump(pos);
                    }
                }
                case ByteCode.GOTO_C -> {
                    byte off = stack.popI8();
                    cursor.jump(off);
                }
                case ByteCode.LOAD -> {
                    byte off = cursor.next();
                    byte value = stack.peek(off);
                    stack.pushI8(value);
                }
                case ByteCode.STORE -> {
                    byte value = stack.popI8();
                    byte off = cursor.next();
                    stack.write(value, off);
                }
                case ByteCode.DEALLOC -> {
                    byte off = cursor.next();
                    stack.dealloc(off);
                }
                case ByteCode.EXIT -> {
                    break loop;
                }
            }
            
            //stack.print();
        }
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