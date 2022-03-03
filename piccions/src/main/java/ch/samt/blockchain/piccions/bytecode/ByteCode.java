package ch.samt.blockchain.piccions.bytecode;

public class ByteCode {

     /**
     * Represents a <code>true</code> value.
     */
    public final static byte FALSE = 0;

    /**
     * Represents a <code>false</code> value.
     */
    public final static byte TRUE = 1;
    
    public final static byte PUSH_I8 = 0;
    public final static byte PUSH_BOOL = 0;
    public final static byte EQUALS_I8 = 5;
    public final static byte EQUALS_BOOL = 5;
    public final static byte OR_I8 = 40;
    public final static byte OR_BOOL = 40;
    public final static byte AND_I8 = 41;
    public final static byte AND_BOOL = 41;
    public final static byte XOR_I8 = 42;
    public final static byte XOR_BOOL = 42;

    public final static byte ADD_I8 = 1;
    public final static byte SUB_I8 = 2;
    public final static byte MUL_I8 = 3;
    public final static byte DIV_I8 = 4;

    public final static byte LOAD = 6; // PUSH stack[-next()]
    public final static byte STORE = 7; // WRITE pop() at next()
    public final static byte DEALLOC = 8; // dealloc until next()

    public final static byte PRINT_I8 = 30;
    public final static byte PRINT_BOOL = 31;

    public final static byte EXIT = 32;
    
    /**
     * GOTO next()
     */
    public final static byte GOTO_A = 20;
    
    /**
     * if (!pop())
     *  GOTO next()
     */
    public final static byte GOTO_B = 21;

    /**
     * GOTO pop()
     */
    public final static byte GOTO_C = 22;

    /**
     * Returns the offset that the given operation makes to the stack size.
     * For example:
     *      PUSH   -> +1    (push a value to the stack)
     *      GOTO_A -> 0     (nothing)
     *      ADD    -> -1    (pop two elements, add result)
     * 
     * The purpose of this method is to track how deep variables in the stackframe are.
     * Hence, instructions such as ALLOC and DEALLOC shall not e considered since they are
     * used respectively before and after a local scope is created.
     * 
     * @param code the instruction
     * @return the size offset
     */
    public static int getStackOffset(byte instruction) {
        return switch (instruction) {
            case PUSH_I8,
                 LOAD           -> +1;
            case EQUALS_I8,
                 OR_I8,
                 AND_I8,
                 XOR_I8,
                 ADD_I8,
                 SUB_I8,
                 MUL_I8,
                 DIV_I8,
                 PRINT_I8,
                 PRINT_BOOL,
                 STORE,
                 GOTO_B,
                 GOTO_C         -> -1;
            case GOTO_A -> 0;
                 default -> 0;
        };
    }

    /**
     * This method returns the offset for the next instruction in the bytecode
     * For example:
     *      PUSH -> 2   (next byte is value to push)
     *      ADD  -> 1   (no arguments in bytecode)
     * 
     * @param code the instruction
     * @return the offset to the next bytecode instruction
     */
    public static int getNextInstructionOffset(byte instruction) {
        return switch (instruction) {
            case PUSH_I8,
                 GOTO_A,
                 GOTO_B,
                 DEALLOC,
                 STORE,
                 LOAD          -> 2;
            case EQUALS_I8,
                 OR_I8,
                 AND_I8,
                 XOR_I8,
                 ADD_I8,
                 SUB_I8,
                 MUL_I8,
                 DIV_I8,
                 PRINT_I8,
                 PRINT_BOOL,
                 GOTO_C         -> 1;
            default             -> 1;
        };
    }

     public static String format(byte instruction) {
          return switch (instruction) {
               case PUSH_I8 -> "PUSH_I8";
               case EQUALS_I8 -> "EQUALS_I8";
               case OR_I8 -> "OR_I8";
               case AND_I8 -> "AND_I8";
               case XOR_I8 -> "XOR_I8";
               case ADD_I8 -> "ADD_I8";
               case SUB_I8 -> "SUB_I8";
               case MUL_I8 -> "MUL_I8";
               case DIV_I8 -> "DIV_I8";
               case LOAD -> "LOAD";
               case STORE -> "STORE";
               case DEALLOC -> "DEALLOC";
               case PRINT_I8 -> "PRINT_I8";
               case PRINT_BOOL -> "PRINT_BOOL";
               case EXIT -> "EXIT";
               case GOTO_A -> "GOTO_A";
               case GOTO_B -> "GOTO_B";
               case GOTO_C -> "GOTO_C";
               default -> "";
          };
     }

}
