package ch.samt.blockchain.piccions.compiler.assembler;

public class Instruction {

    private byte instruction;

    private MetaData options; // store in separate object to avoid useless allocation

    public Instruction(byte instruction) {
        this.instruction = instruction;
    }

    public Instruction() {}

    public byte getInstruction() {
        return instruction;
    }

    public void setInstruction(byte instruction) {
        this.instruction = instruction;
    }

    private void initIfNull() {
        if (options == null) {
            options = new MetaData();
        }
    }

    public void setIncrementOption(int increment) {
        initIfNull();
        options.setIncrementOption(increment);
    }

    public void setIncrementOption() {
        setIncrementOption(1);
    }

    public boolean hasIncrementOption() {
        return options != null && options.hasIncrementOption();
    }

    public int getIncrementOption() {
        return options.getIncrementOption();
    }

    public void setMasterCheckpoint(int checkpoint) {
        initIfNull();
        options.setMasterCheckpoint(checkpoint);
    }

    public boolean hasMasterCheckpoint() {
        return options != null && options.hasMasterCheckpoint();
    }

    public int getMasterCheckpoint() {
        return options.getMasterCheckpoint();
    }

    public void setSlaveCheckpoint(int checkpoint) {
        initIfNull();
        options.setSlaveCheckpoint(checkpoint);
    }

    public boolean hasSlaveCheckpoint() {
        return options != null && options.hasSlaveCheckpoint();
    }

    public int getSlaveCheckpoint() {
        return options.getSlaveCheckpoint();
    }

    public void setMasterStackOffset(int offset) {
        initIfNull();
        options.setMasterStackOffset(offset);
    }

    public boolean hasMasterStackOffset() {
        return options != null && options.hasMasterStackOffset();
    }

    public int getMasterStackOffset() {
        return options.getMasterStackOffset();
    }

    public void setSlaveStackOffset(int offset) {
        initIfNull();
        options.setSlaveStackOffset(offset);
    }

    public boolean hasSlaveStackOffset() {
        return options != null && options.hasSlaveStackOffset();
    }

    public int getSlaveStackOffset() {
        return options.getSlaveStackOffset();
    }

    public void setAlterStackOffset(int offset) {
        initIfNull();
        options.setAlterStackOffset(offset);
    }

    public boolean hasAlterStackOffset() {
        return options != null && options.hasAlterStackOffset();
    }

    public int getAlterStackOffset() {
        return options.getAlterStackOffset();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append(instruction);
        builder.append(" ");

        if (hasIncrementOption()) {
            builder.append("IncrementOptiont: ");
            builder.append(getIncrementOption());
            builder.append(" ");
        }

        if (hasMasterCheckpoint()) {
            builder.append("MasterCheckpoint: ");
            builder.append(getMasterCheckpoint());
            builder.append(" ");
        }

        if (hasSlaveCheckpoint()) {
            builder.append("SlaveCheckpoint: ");
            builder.append(getSlaveCheckpoint());
            builder.append(" ");
        }

        if (hasMasterStackOffset()) {
            builder.append("MasterStackOffset: ");
            builder.append(getMasterStackOffset());
            builder.append(" ");
        }

        if (hasSlaveStackOffset()) {
            builder.append("SlaveStackOffset: ");
            builder.append(getSlaveStackOffset());
            builder.append(" ");
        }

        if (hasAlterStackOffset()) {
            builder.append("AlterStackOffset: ");
            builder.append(getAlterStackOffset());
            builder.append(" ");
        }

        return builder.toString();
    }
    /**
     * A separate class is used so that if the instruction doesn't have metadata
     * only the <code>null</code> is stored.
     */
    private class MetaData {

        /**
         * The value of this opcode will be
         * incremented by the specified amount.
         */
        private int increment = 0;

        /**
         * The position of this opcode can be requested by other opcodes.
         */
        private int masterCheckpoint = -1;

        /**
         * The value of this opcode will be replaced
         * by the position of the specified master checkpoint.
         */
        private int slaveCheckpoint = -1;

        /**
         * The stack offset at this opcode can be requested by other opcodes.
         */
        private int masterStackOffset = -1;
        
        /**
         * The value of this opcode will be replaced
         * by the stack offset of the specified variable.
         */
        private int slaveStackOffset = -1;

        /**
         * This opcode alters the current stack offset
         * by its value.
         */
        private int alterStackOffset = 0;

        public void setIncrementOption(int increment) {
            this.increment = increment;
        }

        public boolean hasIncrementOption() {
            return increment != 0;
        }

        public int getIncrementOption() {
            return increment;
        }

        public void setMasterCheckpoint(int checkpoint) {
            this.masterCheckpoint = checkpoint;
        }

        public boolean hasMasterCheckpoint() {
            return masterCheckpoint != -1;
        }

        public int getMasterCheckpoint() {
            return masterCheckpoint;
        }

        public void setSlaveCheckpoint(int checkpoint) {
            this.slaveCheckpoint = checkpoint;
        }

        public boolean hasSlaveCheckpoint() {
            return slaveCheckpoint != -1;
        }

        public int getSlaveCheckpoint() {
            return slaveCheckpoint;
        }

        public void setMasterStackOffset(int offset) {
            this.masterStackOffset = offset;
        }

        public boolean hasMasterStackOffset() {
            return masterStackOffset != -1;
        }

        public int getMasterStackOffset() {
            return masterStackOffset;
        }

        public void setSlaveStackOffset(int offset) {
            this.slaveStackOffset = offset;
        }

        public boolean hasSlaveStackOffset() {
            return slaveStackOffset != -1;
        }

        public int getSlaveStackOffset() {
            return slaveStackOffset;
        }

        public void setAlterStackOffset(int offset) {
            this.alterStackOffset = offset;
        }

        public boolean hasAlterStackOffset() {
            return alterStackOffset != 0;
        }

        public int getAlterStackOffset() {
            return alterStackOffset;
        }

    }

}