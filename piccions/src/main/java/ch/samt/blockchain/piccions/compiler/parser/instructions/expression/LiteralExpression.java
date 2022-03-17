package ch.samt.blockchain.piccions.compiler.parser.instructions.expression;

import ch.samt.blockchain.piccions.bytecode.ByteCode;
import ch.samt.blockchain.piccions.compiler.assembler.Assembler;
import ch.samt.blockchain.piccions.compiler.assembler.Opcode;

public class LiteralExpression extends Expression {

    private int value;

    public LiteralExpression(int value) {
        this.value = value;
    }

    @Override
    public Opcode[] getOpcodes(Assembler assembler) {
        return Assembler.buildInstructions(
            ByteCode.PUSH_I8,
            (byte) value
        );
    }
    
}
