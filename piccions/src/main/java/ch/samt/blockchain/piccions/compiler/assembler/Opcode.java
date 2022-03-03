package ch.samt.blockchain.piccions.compiler.assembler;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a bytecode instruction with metadata.
 * 
 * <pre>{@code 
 * // Create instruction
 * var instruction = new Instruction2((byte) SOME_VALUE);
 *
 * // Add metadata
 * instruction.addMetaData(new MetaDataType.INCREMENT(SOME_VALUE));
 *
 * // Check if metadata is present
 * instruction.hasMetaData(MetaDataType.INCREMENT);
 *
 * // Get metadata value
 * ((MetaDataType.INCREMENT) instruction.getMetaData(MetaDataType.INCREMENT)).getValue();
 *
 * // Change metadata value
 * ((MetaDataType.INCREMENT) instruction.getMetaData(MetaDataType.INCREMENT)).setValue(SOME_VALUE); 
 * }</pre>
 */
public class Opcode {

    private byte instruction; // TODO: opcode
    
    private Map<MetaDataType, MetaData> options;

    public Opcode() {}

    public Opcode(byte instruction) {
        this.instruction = instruction;
    }

    public byte getInstruction() {
        return instruction;
    }

    public void setInstruction(byte instruction) {
        this.instruction = instruction;
    }

    public void addMetaData(MetaData option) {
        if (options == null) {
            options = new HashMap<>();
        }

        options.put(option.getType(), option);
    }

    public boolean hasMetaData(MetaDataType type) {
        return options != null && options.containsKey(type);
    }

    public MetaData getMetaData(MetaDataType type) {
        return options.get(type);
    }

    public abstract static class MetaData {

        abstract MetaDataType getType();

    }

    public abstract static class GenericMetaData<E> extends MetaData {

        private E value;

        public GenericMetaData(E value) {
            this.value = value;
        }

        public E getValue() {
            return value;
        }

        public void setValue(E value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value.toString();
        }

    }

    public static enum MetaDataType {
        /* Higher priority -> execute first
        
                                 PRIORITY: */
        INCREMENT,                    // 1
        MASTER_CHECKPOINT,            // 2
        SLAVE_CHECKPOINT,             // 2
        MASTER_STACK_OFFSET,          // 2
        SLAVE_STACK_OFFSET,           // 2
        ALTER_STACK_OFFSET,           // 2
        DECREASE_STACK_BY_PARAM_SIZE, // 3
        PARAMETER,                    // 3
        DEBUG;                        // -

        // TODO: multiple metadata for MASTER_*

        public static class INCREMENT extends GenericMetaData<Integer> {

            public INCREMENT(int value) {
                super(value);
            }

            MetaDataType getType() {
                return INCREMENT;
            }

        }

        public static class MASTER_CHECKPOINT extends GenericMetaData<Integer> {

            public MASTER_CHECKPOINT(int value) {
                super(value);
            }

            MetaDataType getType() {
                return MASTER_CHECKPOINT;
            }

        }

        public static class SLAVE_CHECKPOINT extends GenericMetaData<Integer> {

            public SLAVE_CHECKPOINT(int value) {
                super(value);
            }

            MetaDataType getType() {
                return SLAVE_CHECKPOINT;
            }

        }

        public static class MASTER_STACK_OFFSET extends GenericMetaData<Integer> {

            public MASTER_STACK_OFFSET(int value) {
                super(value);
            }

            MetaDataType getType() {
                return MASTER_STACK_OFFSET;
            }

        }

        public static class SLAVE_STACK_OFFSET extends GenericMetaData<Integer> {

            public SLAVE_STACK_OFFSET(int value) {
                super(value);
            }

            MetaDataType getType() {
                return SLAVE_STACK_OFFSET;
            }

        }

        public static class ALTER_STACK_OFFSET extends GenericMetaData<Integer> {

            public ALTER_STACK_OFFSET(int value) {
                super(value);
            }

            MetaDataType getType() {
                return ALTER_STACK_OFFSET;
            }

        }

        public static class DECREASE_STACK_BY_PARAM_SIZE extends GenericMetaData<String> {

            public DECREASE_STACK_BY_PARAM_SIZE(String funcName) {
                super(funcName);
            }

            MetaDataType getType() {
                return DECREASE_STACK_BY_PARAM_SIZE;
            }

        }

        public static class PARAMETER extends GenericMetaData<String> {

            public PARAMETER(String funcNameAndIndex) {
                super(funcNameAndIndex);
            }

            MetaDataType getType() {
                return PARAMETER;
            }

        }

        public static class DEBUG extends GenericMetaData<String> {

            public DEBUG(String funcNameAndIndex) {
                super(funcNameAndIndex);
            }

            MetaDataType getType() {
                return DEBUG;
            }

        }
    }

    @Override
    public String toString() {
        // format metadata
        StringBuilder builder = new StringBuilder();

        if (options != null) {
            for (var type : options.keySet()) {
                var value = options.get(type);
                builder.append("[" + type + ": " + value + "]");
            }
        }

        return builder.toString();
    }

}