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
public class Instruction {

    private byte instruction;
    
    private Map<MetaDataType, MetaData> options;

    public Instruction() {}

    public Instruction(byte instruction) {
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
        INCREMENT,
        MASTER_CHECKPOINT,
        SLAVE_CHECKPOINT,
        MASTER_STACK_OFFSET,
        SLAVE_STACK_OFFSET,
        ALTER_STACK_OFFSET,
        INCREASE_STACK_BY_PARAM_SIZE,
        DECREASE_STACK_BY_PARAM_SIZE;

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

        public static class INCREASE_STACK_BY_PARAM_SIZE extends GenericMetaData<String> {

            public INCREASE_STACK_BY_PARAM_SIZE(String funcName) {
                super(funcName);
            }

            MetaDataType getType() {
                return INCREASE_STACK_BY_PARAM_SIZE;
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
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(instruction + " ");

        if (options != null) {
            for (var type : options.keySet()) {
                var value = options.get(type);
                builder.append("[" + type + ": " + value + "]");
            }
        }

        return builder.toString();
    }

}