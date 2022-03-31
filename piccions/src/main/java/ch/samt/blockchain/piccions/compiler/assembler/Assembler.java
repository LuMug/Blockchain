package ch.samt.blockchain.piccions.compiler.assembler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ch.samt.blockchain.piccions.bytecode.ByteCode;

import static ch.samt.blockchain.piccions.compiler.assembler.Opcode.MetaDataType.*;

public class Assembler {
    
    private List<Opcode> bytecode;

    private Map<String, Integer> variableStackOffsetIDs;
    private Map<String, Integer> functionCheckpointIDs;
    private Map<String, int[]> functionParamSizes;

    /**
     * IDs used to mark function checkpoints
     * or variable stack offsets.
     */
    private int ids;

    public Assembler() {
        this.bytecode = new LinkedList<>();
        this.functionCheckpointIDs = new HashMap<>();
        this.variableStackOffsetIDs = new HashMap<>();
        this.functionParamSizes = new HashMap<>();
    }

    public void add(byte code) {
        add(new Opcode(code));
    }

    public void add(byte... codes) {
        for (var code : codes) {
            add(code);
        }
    }

    public void add(Opcode code) {
        bytecode.add(code);
    }

    public void add(Opcode... codes) {
        for (var code : codes) {
            add(code);
        }
    }

    public int nextID() {
        return ids++;
    }

    public int getMasterCheckpointIndex(int checkpoint) {
        for (int i = 0; i < bytecode.size(); i++) {
            Opcode instruction = bytecode.get(i);

            if (instruction.hasMetaData(MASTER_CHECKPOINT)
                    && ((MASTER_CHECKPOINT) instruction.getMetaData(MASTER_CHECKPOINT)).getValue() == checkpoint) {
                return i;
            }
        }

        throw new IllegalArgumentException("Invalid checkpoint");
    }

    public Opcode[] whileLoop(byte[] conditionCode, byte[] bodyCode) {
        return whileLoop(buildInstructions(conditionCode), buildInstructions(bodyCode));
    }

    public Opcode[] whileLoop(byte[] conditionCode, Opcode[] bodyCode) {
        return whileLoop(buildInstructions(conditionCode), bodyCode);
    }

    public Opcode[] whileLoop(Opcode[] conditionCode, byte[] bodyCode) {
        return whileLoop(conditionCode, buildInstructions(bodyCode));
    }

    public Opcode[] whileLoop(Opcode[] conditionCode, Opcode[] bodyCode) {
        bodyCode = scope(bodyCode);

        var result = new Opcode[conditionCode.length + bodyCode.length + 4];
        int pos = 0;

        for (var code : conditionCode) {
            result[pos++] = code;
        }

        int startCheckpoint = nextID();
        int endCheckpoint = nextID();

        result[0].addMetaData(new MASTER_CHECKPOINT(startCheckpoint));
        result[0].addMetaData(new INCREMENT(1));


        result[pos++] = new Opcode(ByteCode.GOTO_B);
        
        Opcode temp = new Opcode();

        temp.addMetaData(new SLAVE_CHECKPOINT(endCheckpoint));
        temp.addMetaData(new INCREMENT(1));
        result[pos++] = temp;

        for (var code : bodyCode) {
            result[pos++] = code;
        }

        result[pos++] = new Opcode(ByteCode.GOTO_A);
        
        temp = new Opcode();

        temp.addMetaData(new MASTER_CHECKPOINT(endCheckpoint));
        temp.addMetaData(new SLAVE_CHECKPOINT(startCheckpoint));
        result[pos++] = temp;

        return result;
    }

    public Opcode[] ifStatement(byte[] conditionCode, byte[] bodyCode) {
        return ifStatement(buildInstructions(conditionCode), buildInstructions(bodyCode));
    }

    public Opcode[] ifStatement(byte[] conditionCode, Opcode[] bodyCode) {
        return ifStatement(buildInstructions(conditionCode), bodyCode);
    }

    public Opcode[] ifStatement(Opcode[] conditionCode, byte[] bodyCode) {
        return ifStatement(conditionCode, buildInstructions(bodyCode));
    }

    public Opcode[] ifStatement(Opcode[] conditionCode, Opcode[] bodyCode) {
        bodyCode = scope(bodyCode);

        var result = new Opcode[conditionCode.length + bodyCode.length + 2];
        int pos = 0;

        for (var code : conditionCode) {
            result[pos++] = code;
        }

        int endCheckpoint = nextID();

        result[pos++] = new Opcode(ByteCode.GOTO_B);
        
        Opcode temp = new Opcode();
        

        temp.addMetaData(new SLAVE_CHECKPOINT(endCheckpoint));
        temp.addMetaData(new INCREMENT(1));
        result[pos++] = temp;

        for (var code : bodyCode) {
            result[pos++] = code;
        }


        result[result.length - 1].addMetaData(new MASTER_CHECKPOINT(endCheckpoint));

        return result;
    }

    public Opcode[] ifElseStatement(byte[] conditionCode, byte[] ifCode, byte[] elseCode) {
        return ifElseStatement(buildInstructions(conditionCode), buildInstructions(ifCode), buildInstructions(elseCode));
    }

    public Opcode[] ifElseStatement(byte[] conditionCode, byte[] ifCode, Opcode[] elseCode) {
        return ifElseStatement(buildInstructions(conditionCode), buildInstructions(ifCode), elseCode);
    }

    public Opcode[] ifElseStatement(byte[] conditionCode, Opcode[] ifCode, byte[] elseCode) {
        return ifElseStatement(buildInstructions(conditionCode), ifCode, buildInstructions(elseCode));
    }

    public Opcode[] ifElseStatement(byte[] conditionCode, Opcode[] ifCode, Opcode[] elseCode) {
        return ifElseStatement(buildInstructions(conditionCode), ifCode, elseCode);
    }

    public Opcode[] ifElseStatement(Opcode[] conditionCode, byte[] ifCode, byte[] elseCode) {
        return ifElseStatement(conditionCode, buildInstructions(ifCode), buildInstructions(elseCode));
    }

    public Opcode[] ifElseStatement(Opcode[] conditionCode, byte[] ifCode, Opcode[] elseCode) {
        return ifElseStatement(conditionCode, buildInstructions(ifCode), elseCode);
    }

    public Opcode[] ifElseStatement(Opcode[] conditionCode, Opcode[] ifCode, byte[] elseCode) {
        return ifElseStatement(conditionCode, ifCode, buildInstructions(elseCode));
    }

    public Opcode[] ifElseStatement(Opcode[] conditionCode, Opcode[] ifCode, Opcode[] elseCode) {
        ifCode = scope(ifCode);
        elseCode = scope(elseCode);
        
        var result = new Opcode[ifCode.length + elseCode.length + conditionCode.length + 4];
        int pos = 0;

        for (var code : conditionCode) {
            result[pos++] = code;
        }

        int ifEndCheckpoint = nextID();
        int elseEndCheckpoint = nextID();

        result[pos++] = new Opcode(ByteCode.GOTO_B);

        Opcode temp = new Opcode();
        temp.addMetaData(new SLAVE_CHECKPOINT(ifEndCheckpoint));
        temp.addMetaData(new INCREMENT(1));
        result[pos++] = temp;

        for (var code : ifCode) {
            result[pos++] = code;
        }

        result[pos++] = new Opcode(ByteCode.GOTO_A);

        temp = new Opcode();
        temp.addMetaData(new MASTER_CHECKPOINT(ifEndCheckpoint));
        temp.addMetaData(new SLAVE_CHECKPOINT(elseEndCheckpoint));
        temp.addMetaData(new INCREMENT(1));
        result[pos++] = temp;

        for (var code : elseCode) {
            result[pos++] = code;
        }

        result[result.length - 1].addMetaData(new MASTER_CHECKPOINT(elseEndCheckpoint));

        return result;
    }

    public Opcode[] declareFunc(String name, byte... code) {
        return declareFunc(name, buildInstructions(code));
    }

    public Opcode[] declareFunc(String name, Opcode[] code) {
        return declareFuncWithParams(name, new int[]{0}, code);
    }

    public Opcode[] declareFuncWithParams(String name, int[] paramSizes, Opcode[] body) {
        body = scope(body);

        var result = new Opcode[body.length + 1];
        int pos = 0;

        // body
        for (var instruction : body) {
            result[pos++] = instruction;
        }

        // Go back to whatever the code was executing
        result[pos++] = new Opcode(ByteCode.GOTO_C);
        
        // Set master checkpoint for this function
        int funcCheckpoint = 0;
        if (functionCheckpointIDs.containsKey(name)) {
            funcCheckpoint = functionCheckpointIDs.get(name);
        } else {
            functionCheckpointIDs.put(name, funcCheckpoint = nextID());
        }

        result[0].addMetaData(new MASTER_CHECKPOINT(funcCheckpoint));

        // Total parameter size
        int paramTotalSize = sum(paramSizes);

        // Increase stack offset such that scope erases params
        body[0].addMetaData(new ALTER_STACK_OFFSET(+paramTotalSize));

        
        functionParamSizes.put(name, paramSizes);

        // body[0] of the function should be masterStack of every parameter
        // it already has a MASTER_STACK_OFFSET id
        // The stack offset <name>_params represents the ending point
        // for all the parameters.
        variableStackOffsetIDs.put(name + "_params",
            ((MASTER_STACK_OFFSET) body[0].getMetaData(MASTER_STACK_OFFSET)).getValue());
        // TODO add another MASTER_STACK_OFFSET

        return result;
    }

    public Opcode[] invokeFunc(String name) {
        return invokeFuncWithParams(name, null);
    }

    public Opcode param(String funcName, int index) {
        Opcode temp = new Opcode();
        temp.addMetaData(new PARAMETER(funcName + "_" + index));
        return temp;
    }

    public Opcode[] invokeFuncWithParams(String name, Opcode[] pushParams) {
        var result = new Opcode[4 + (pushParams == null ? 0 : pushParams.length)];

        int funcCheckpoint = 0;
        
        if (functionCheckpointIDs.containsKey(name)) {
            funcCheckpoint = functionCheckpointIDs.get(name);
        } else {
            // create checkpoint if doesn't exist
            functionCheckpointIDs.put(name, funcCheckpoint = nextID());
        }
        
        int pos = 0;

        // Push index to GOTO after func is done
        Opcode temp = new Opcode(ByteCode.PUSH_I8);

        // XXXX temp.addMetaData(new ALTER_STACK_OFFSET(-1));
        result[pos++] = temp;

        temp = new Opcode();
        int indexCheckpoint = nextID();

        temp.addMetaData(new SLAVE_CHECKPOINT(indexCheckpoint));
        temp.addMetaData(new MASTER_CHECKPOINT(indexCheckpoint));
         // skip this, the next two instructions (and pushParams if any)
        temp.addMetaData(new INCREMENT(3 + (pushParams == null ? 0 : pushParams.length)));
        result[pos++] = temp;

        if (pushParams != null) {
            // Push params
            for (var instruction : pushParams) {
                result[pos++] = instruction;
            }
        }
        
        // GOTO func
        temp = new Opcode(ByteCode.GOTO_A);
        if (pushParams != null) {
            temp.addMetaData(new DECREASE_STACK_BY_PARAM_SIZE(name));
            temp.addMetaData(new ALTER_STACK_OFFSET(-1)); // - PUSH %POS%
        }
        result[pos++] = temp;

        temp = new Opcode();
        temp.addMetaData(new SLAVE_CHECKPOINT(funcCheckpoint));
        result[pos++] = temp;

        return result;
    }

    public Opcode variable(String name) {
        return variable(name, new Opcode());
    }

    public Opcode variable(String name, byte instruction) {
        return variable(name, new Opcode(instruction));
    }

    public Opcode variable(String name, Opcode instruction) {
        if (variableStackOffsetIDs.containsKey(name)) {

            instruction.addMetaData(new SLAVE_STACK_OFFSET(variableStackOffsetIDs.get(name)));
        } else {
            // create if doesn't exist
            int id = nextID();
            variableStackOffsetIDs.put(name, id);
            instruction.addMetaData(new MASTER_STACK_OFFSET(id));
        }

        return instruction;
    }

    public Opcode[] scope(Opcode... instructions) {
        if (instructions.length == 0) { // empty scope
            return instructions;
        }
        
        int id = nextID();

        instructions[0].addMetaData(new MASTER_STACK_OFFSET(id));

        var result = new Opcode[instructions.length + 2];
        int i = 0;
        for (; i < instructions.length; i++) {
            result[i] = instructions[i];
        }

        result[i++] = new Opcode(ByteCode.DEALLOC);
        var temp = new Opcode();
        temp.addMetaData(new SLAVE_STACK_OFFSET(id));
        result[i++] = temp;

        return result;
    }

    // should be added first
    public Opcode[] mainFunc(byte... code) {
        return mainFunc(buildInstructions(code));
    }

    // should be added first
    public Opcode[] mainFunc(Opcode... code) {
        var result = buildInstructions(code,
            new Opcode[]{new Opcode(ByteCode.EXIT)});

        return result;
    }

    public byte[] assemble() {
        byte[] result = new byte[bytecode.size()];
        int pos = 0;

        for (var instruction : bytecode) {

            // process DECREASE_STACK_BY_PARAM_SIZE
            if (instruction.hasMetaData(DECREASE_STACK_BY_PARAM_SIZE)) {
                String funcName = ((DECREASE_STACK_BY_PARAM_SIZE) instruction.getMetaData(DECREASE_STACK_BY_PARAM_SIZE)).getValue();
                int totalParamSize = sum(functionParamSizes.get(funcName));
                
                // Modify ALTER_STACK_OFFSET
                if (instruction.hasMetaData(ALTER_STACK_OFFSET)) {
                    // Sum if there's already
                    int v = ((ALTER_STACK_OFFSET) instruction.getMetaData(ALTER_STACK_OFFSET)).getValue();
                    ((ALTER_STACK_OFFSET) instruction.getMetaData(ALTER_STACK_OFFSET)).setValue(v - totalParamSize);
                } else {
                    // Add metadata there isn't
                    instruction.addMetaData(new ALTER_STACK_OFFSET(-totalParamSize));
                }
            }

            // process PARAMETER
            if (instruction.hasMetaData(PARAMETER)) {
                String funcNameAndParameterIndex = ((PARAMETER) instruction.getMetaData(PARAMETER)).getValue();
                
                String funcName = "";
                int offset = 0;
                {
                    int separatorIndex = funcNameAndParameterIndex.lastIndexOf("_");
                    funcName = funcNameAndParameterIndex.substring(0, separatorIndex);
                    int paramIndex = Integer.parseInt(funcNameAndParameterIndex
                    .substring(separatorIndex + 1, funcNameAndParameterIndex.length()));
                    int[] paramSizes = functionParamSizes.get(funcName);
                    for (; paramIndex < paramSizes.length; ++paramIndex) {
                        offset += paramSizes[paramIndex];
                    }
                }
                
                int masterStackOffsetID = variableStackOffsetIDs.get(funcName + "_params");
                
                // Add SLAVE_STACK_OFFSET
                instruction.addMetaData(new SLAVE_STACK_OFFSET(masterStackOffsetID));

                // Add INCREMENT
                instruction.addMetaData(new INCREMENT(offset));

            }

            // Process position checkpoints
            if (instruction.hasMetaData(SLAVE_CHECKPOINT)) {
                int checkpoint = ((SLAVE_CHECKPOINT) instruction.getMetaData(SLAVE_CHECKPOINT)).getValue();
                int index = getMasterCheckpointIndex(checkpoint);

                if (instruction.hasMetaData(INCREMENT)) {
                    index += ((INCREMENT) instruction.getMetaData(INCREMENT)).getValue();
                }

                instruction.setInstruction((byte) index);
            }
        }

        // Process offset stack IDs
        processStackOffsets(bytecode);


        for (var instruction : bytecode) {
            // remove annotations
            result[pos++] = instruction.getInstruction();
        }

        return result;
    }

    private void processStackOffsets(List<Opcode> result) {
        int stackOffset = 0;
        Map<Integer, Integer> offsets = new HashMap<>();
        int instructionIndex = 0;

        // weird spaghetti logic

        Opcode lastInstruction = null;
        for (int i = 0; i < result.size(); i++) {
            Opcode instruction = result.get(i);
            byte opcode = instruction.getInstruction();
    
            if (instruction.hasMetaData(MASTER_STACK_OFFSET)) {
                offsets.put(((MASTER_STACK_OFFSET) instruction.getMetaData(MASTER_STACK_OFFSET)).getValue(), stackOffset);
            }

            if (instruction.hasMetaData(SLAVE_STACK_OFFSET)) {
                instruction.setInstruction(
                    (byte) (stackOffset -
                    offsets.get(((SLAVE_STACK_OFFSET) instruction.getMetaData(SLAVE_STACK_OFFSET)).getValue())));
            }

            if (instruction.hasMetaData(ALTER_STACK_OFFSET)) {
                stackOffset += ((ALTER_STACK_OFFSET) instruction.getMetaData(ALTER_STACK_OFFSET)).getValue();
            }

            if (instructionIndex == i) {
                int next = ByteCode.getNextInstructionOffset(opcode);
                if (next == 1) {
                    stackOffset += ByteCode.getStackOffset(opcode);    
                }
                instructionIndex += next;
            } else if (lastInstruction != null) {
                stackOffset += ByteCode.getStackOffset(lastInstruction.getInstruction());
                if (lastInstruction.getInstruction() == ByteCode.DEALLOC) {
                    stackOffset -= offsets.get(((SLAVE_STACK_OFFSET) instruction.getMetaData(SLAVE_STACK_OFFSET)).getValue());
                }
            }

            lastInstruction = instruction;
            // TODO support opcodes with more than two next()
            // modify stacksize only once per instruction
        }
    }

    public static Opcode[] buildInstructions(byte... bytecode) {
        var result = new Opcode[bytecode.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = new Opcode(bytecode[i]);
        }

        return result;
    }

    public static Opcode[] buildInstructions(Opcode... bytecode) {
        return bytecode;
    }

    public static Opcode buildInstruction(byte instruction) {
        return new Opcode(instruction);
    }

    public static Opcode[] buildInstructions(Opcode[]... chunks) {
        if (chunks.length == 1) {
            return chunks[0];
        }

        int size = 0;

        for (var chunk : chunks) {
            size += chunk.length;
        }

        var result = new Opcode[size];

        int pos = 0;
        for (var chunk : chunks) {
            for (var v : chunk) {
                result[pos++] = v;
            }
        }

        return result;
    }

    private static int sum(int[] arr) {
        int sum = 0;

        for (int i : arr) {
            sum += i;
        }

        return sum;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        int digits = (int) Math.log10(bytecode.size()) + 1;

        // TODO use iterator
        int nextInstructionIndex = 0;
        for (int i = 0; i < bytecode.size(); i++) {
            builder.append(pad(i, digits) + ".\t");

            byte opcode = bytecode.get(i).getInstruction();
            if (i == nextInstructionIndex) {
                nextInstructionIndex += ByteCode.getNextInstructionOffset(opcode);
                builder.append(ByteCode.format(opcode) + " ");
                builder.append(bytecode.get(i).toString());
            } else {
                builder.append(opcode);
            }

            builder.append("\n\r");
        }

        return builder.toString();
    }

    private static String pad(int v, int digits) {
        StringBuilder builder = new StringBuilder();
        builder.append(Integer.toString(v));
        while (builder.length() < digits) {
            builder.insert(0,"0");
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

}
