package ch.samt.blockchain.piccions.compiler.assembler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ch.samt.blockchain.piccions.bytecode.ByteCode;

public class Assembler {
    
    private List<Instruction> bytecode;

    private Map<String, Integer> functionCheckpointIDs;
    private Map<String, Integer> variableStackOffsetIDs;

    /**
     * IDs used to mark function checkpoints
     * or variable stack offsets.
     */
    private int ids;

    public Assembler() {
        this.bytecode = new LinkedList<>();
        this.functionCheckpointIDs = new HashMap<>();
        this.variableStackOffsetIDs = new HashMap<>();
    }

    public void add(byte code) {
        add(new Instruction(code));
    }

    public void add(byte... codes) {
        for (var code : codes) {
            add(code);
        }
    }

    public void add(Instruction code) {
        bytecode.add(code);
    }

    public void add(Instruction... codes) {
        for (var code : codes) {
            add(code);
        }
    }

    public int nextID() {
        return ids++;
    }

    public int getMasterCheckpointIndex(int checkpoint) {
        for (int i = 0; i < bytecode.size(); i++) {
            Instruction instruction = bytecode.get(i);

            if (instruction.hasMasterCheckpoint() && instruction.getMasterCheckpoint() == checkpoint) {
                return i;
            }
        }

        throw new IllegalArgumentException("Invalid checkpoint");
    }

    public byte[] compile() {
        byte[] result = new byte[bytecode.size() + 2];
        int pos = 0;

        for (var instruction : bytecode) {
            if (instruction.hasSlaveCheckpoint()) {
                int checkpoint = instruction.getSlaveCheckpoint();
                int index = getMasterCheckpointIndex(checkpoint);

                if (instruction.hasIncrementOption()) {
                    index += instruction.getIncrementOption();
                }

                instruction.setInstruction((byte) index);
            }

            // remove annotations
            result[pos++] = instruction.getInstruction();
        }

        return result;
    }

    public Instruction[] whileLoop(byte[] conditionCode, byte[] bodyCode) {
        return whileLoop(buildInstructions(conditionCode), buildInstructions(bodyCode));
    }

    public Instruction[] whileLoop(byte[] conditionCode, Instruction[] bodyCode) {
        return whileLoop(buildInstructions(conditionCode), bodyCode);
    }

    public Instruction[] whileLoop(Instruction[] conditionCode, byte[] bodyCode) {
        return whileLoop(conditionCode, buildInstructions(bodyCode));
    }

    public Instruction[] whileLoop(Instruction[] conditionCode, Instruction[] bodyCode) {
        bodyCode = scope(bodyCode);

        var result = new Instruction[conditionCode.length + bodyCode.length + 4];
        int pos = 0;

        for (var code : conditionCode) {
            result[pos++] = code;
        }

        int startCheckpoint = nextID();
        int endCheckpoint = nextID();

        result[0].setMasterCheckpoint(startCheckpoint);
        result[0].setIncrementOption();


        result[pos++] = new Instruction(ByteCode.GOTO_B);
        
        Instruction temp = new Instruction();
        temp.setSlaveCheckpoint(endCheckpoint);
        result[pos++] = temp;

        for (var code : bodyCode) {
            result[pos++] = code;
        }

        result[pos++] = new Instruction(ByteCode.GOTO_A);
        
        temp = new Instruction();
        temp.setMasterCheckpoint(endCheckpoint);
        temp.setSlaveCheckpoint(startCheckpoint);
        result[pos++] = temp;

        return result;
    }

    public Instruction[] ifStatement(byte[] conditionCode, byte[] bodyCode) {
        return ifStatement(buildInstructions(conditionCode), buildInstructions(bodyCode));
    }

    public Instruction[] ifStatement(byte[] conditionCode, Instruction[] bodyCode) {
        return ifStatement(buildInstructions(conditionCode), bodyCode);
    }

    public Instruction[] ifStatement(Instruction[] conditionCode, byte[] bodyCode) {
        return ifStatement(conditionCode, buildInstructions(bodyCode));
    }

    public Instruction[] ifStatement(Instruction[] conditionCode, Instruction[] bodyCode) {
        bodyCode = scope(bodyCode);

        var result = new Instruction[conditionCode.length + bodyCode.length + 2];
        int pos = 0;

        for (var code : conditionCode) {
            result[pos++] = code;
        }

        int endCheckpoint = nextID();

        result[pos++] = new Instruction(ByteCode.GOTO_B);
        
        Instruction temp = new Instruction();
        temp.setSlaveCheckpoint(endCheckpoint);
        temp.setIncrementOption();
        result[pos++] = temp;

        for (var code : bodyCode) {
            result[pos++] = code;
        }

        result[result.length - 1].setMasterCheckpoint(endCheckpoint);

        return result;
    }

    public Instruction[] ifElseStatement(byte[] conditionCode, byte[] ifCode, byte[] elseCode) {
        return ifElseStatement(buildInstructions(conditionCode), buildInstructions(ifCode), buildInstructions(elseCode));
    }

    public Instruction[] ifElseStatement(byte[] conditionCode, byte[] ifCode, Instruction[] elseCode) {
        return ifElseStatement(buildInstructions(conditionCode), buildInstructions(ifCode), elseCode);
    }

    public Instruction[] ifElseStatement(byte[] conditionCode, Instruction[] ifCode, byte[] elseCode) {
        return ifElseStatement(buildInstructions(conditionCode), ifCode, buildInstructions(elseCode));
    }

    public Instruction[] ifElseStatement(byte[] conditionCode, Instruction[] ifCode, Instruction[] elseCode) {
        return ifElseStatement(buildInstructions(conditionCode), ifCode, elseCode);
    }

    public Instruction[] ifElseStatement(Instruction[] conditionCode, byte[] ifCode, byte[] elseCode) {
        return ifElseStatement(conditionCode, buildInstructions(ifCode), buildInstructions(elseCode));
    }

    public Instruction[] ifElseStatement(Instruction[] conditionCode, byte[] ifCode, Instruction[] elseCode) {
        return ifElseStatement(conditionCode, buildInstructions(ifCode), elseCode);
    }

    public Instruction[] ifElseStatement(Instruction[] conditionCode, Instruction[] ifCode, byte[] elseCode) {
        return ifElseStatement(conditionCode, ifCode, buildInstructions(elseCode));
    }

    public Instruction[] ifElseStatement(Instruction[] conditionCode, Instruction[] ifCode, Instruction[] elseCode) {
        ifCode = scope(ifCode);
        elseCode = scope(elseCode);
        
        var result = new Instruction[ifCode.length + elseCode.length + conditionCode.length + 4];
        int pos = 0;

        for (var code : conditionCode) {
            result[pos++] = code;
        }

        int ifEndCheckpoint = nextID();
        int elseEndCheckpoint = nextID();

        result[pos++] = new Instruction(ByteCode.GOTO_B);

        Instruction temp = new Instruction();
        temp.setSlaveCheckpoint(ifEndCheckpoint);
        temp.setIncrementOption();
        result[pos++] = temp;

        for (var code : ifCode) {
            result[pos++] = code;
        }

        result[pos++] = new Instruction(ByteCode.GOTO_A);

        temp = new Instruction();
        temp.setMasterCheckpoint(ifEndCheckpoint);
        temp.setSlaveCheckpoint(elseEndCheckpoint);
        temp.setIncrementOption();
        result[pos++] = temp;

        for (var code : elseCode) {
            result[pos++] = code;
        }

        result[result.length - 1].setMasterCheckpoint(elseEndCheckpoint);

        return result;
    }

    public Instruction[] declareFunc(String name, byte... code) {
        return declareFunc(name, buildInstructions(code));
    }

    public Instruction[] declareFunc(String name, Instruction[]... code) {
        var bytecode = buildInstructions(code);

        var result = new Instruction[bytecode.length + 1];
        int pos = 0;

        // Function code
        for (var instruction : bytecode) {
            result[pos++] = instruction;
        }

        // Set master checkpoint for this function
        int funcCheckpoint = 0;
        if (functionCheckpointIDs.containsKey(name)) {
            funcCheckpoint = functionCheckpointIDs.get(name);
        } else {
            functionCheckpointIDs.put(name, funcCheckpoint = nextID());
        }
        result[0].setMasterCheckpoint(funcCheckpoint);

        // Process offset stack IDs
        processStackOffsetIDs(result);

        // Go back to whatever the code was executing
        result[pos++] = new Instruction(ByteCode.GOTO_C);

        return result;
    }

    private void processStackOffsetIDs(Instruction... result) {
        int stackOffset = 0;
        Map<Integer, Integer> offsets = new HashMap<>();
        int instructionIndex = 0;

        Instruction lastIntruction = null;
        for (int i = 0; i < result.length; i++) {
            Instruction instruction = result[i];
            byte opcode = instruction.getInstruction();
            
            if (instruction.hasMasterStackOffset()) {
                offsets.put(instruction.getMasterStackOffset(), stackOffset);
            }
            
            if (instruction.hasSlaveStackOffset()) {
                instruction.setInstruction(
                    (byte) (stackOffset -
                    offsets.get(instruction.getSlaveStackOffset())));
            }

            // if opcode is instruction
            if (instructionIndex == i) {
                stackOffset += ByteCode.getStackOffset(opcode);
                instructionIndex += ByteCode.getNextInstructionOffset(opcode);

                if (lastIntruction != null && lastIntruction.getInstruction() == ByteCode.DEALLOC) {
                    stackOffset -= offsets.get(lastIntruction.getSlaveStackOffset());
                }
            }

            lastIntruction = instruction;
        }
    }

    public Instruction[] invokeFunc(String name) {
        var result = new Instruction[4];

        int funcCheckpoint = 0;
        
        if (functionCheckpointIDs.containsKey(name)) {
            funcCheckpoint = functionCheckpointIDs.get(name);
        } else {
            // create checkpoint if doesn't exist
            functionCheckpointIDs.put(name, funcCheckpoint = nextID());
        }
        
        // Push index to GOTO after func is done
        result[0] = new Instruction(ByteCode.PUSH_I8);

        Instruction temp = new Instruction();
        int indexCheckpoint = nextID();
        temp.setSlaveCheckpoint(indexCheckpoint);
        temp.setMasterCheckpoint(indexCheckpoint);
        temp.setIncrementOption(3); // skip this and the next two instructions
        result[1] = temp;

        // GOTO func
        result[2] = new Instruction(ByteCode.GOTO_A);

        temp = new Instruction();
        temp.setSlaveCheckpoint(funcCheckpoint);
        result[3] = temp;

        return result;
    }

    public Instruction variable(String name) {
        return variable(name, new Instruction());
    }

    public Instruction variable(String name, byte instruction) {
        return variable(name, new Instruction(instruction));
    }

    public Instruction variable(String name, Instruction instruction) {
        if (variableStackOffsetIDs.containsKey(name)) {
            instruction.setSlaveStackOffset(variableStackOffsetIDs.get(name));
        } else {
            // create if doesn't exist
            int id = nextID();
            variableStackOffsetIDs.put(name, id);
            instruction.setMasterStackOffset(id);
        }

        return instruction;
    }

    public Instruction[] scope(Instruction... instructions) {
        int id = nextID();
        instructions[0].setMasterStackOffset(id);

        var result = new Instruction[instructions.length + 2];
        int i = 0;
        for (; i < instructions.length; i++) {
            result[i] = instructions[i];
        }

        result[i++] = new Instruction(ByteCode.DEALLOC);
        var temp = new Instruction();
        temp.setSlaveStackOffset(id);
        result[i++] = temp;

        return result;
    }

    // should be called first
    public Instruction[] mainFunc(byte... code) {
        return mainFunc(buildInstructions(code));
    }

    // should be called first
    public Instruction[] mainFunc(Instruction... code) {
        var result = buildInstructions(code,
            new Instruction[]{new Instruction(ByteCode.EXIT)});

        processStackOffsetIDs(result);

        return result;
    }

    public static Instruction[] buildInstructions(byte... bytecode) {
        var result = new Instruction[bytecode.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = new Instruction(bytecode[i]);
        }

        return result;
    }

    public static Instruction buildInstruction(byte instruction) {
        return new Instruction(instruction);
    }

    public static Instruction[] buildInstructions(Instruction[]... chunks) {
        if (chunks.length == 1) {
            return chunks[0];
        }

        int size = 0;

        for (var chunk : chunks) {
            size += chunk.length;
        }

        var result = new Instruction[size];

        int pos = 0;
        for (var chunk : chunks) {
            for (var v : chunk) {
                result[pos++] = v;
            }
        }

        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (var instruction : bytecode) {
            builder.append(instruction.toString());
            builder.append("\n");
        }

        return builder.toString();
    }
    
    /*
    @SuppressWarnings("unchecked")
    private static <T> T[] merge(T[]... chunks) {
        int size = 0;

        for (T[] chunk : chunks) {
            size += chunk.length;
        }

        T[] result = (T[]) new Object[size];

        int pos = 0;
        for (T[] chunk : chunks) {
            for (T v : chunk) {
                result[pos++] = v;
            }
        }

        return result;
    }*/

    // nextCheckpoint() -> nextUniqueValue() / counter somthing

    // Problem: call to a fuction doesn't allocate on stack
    // for the stack offset ids.
    // Implement scope for functions
    // Functions with parameters and retruns

}
