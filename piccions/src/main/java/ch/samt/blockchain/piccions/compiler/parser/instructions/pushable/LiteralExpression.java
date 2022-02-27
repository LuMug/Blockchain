package ch.samt.blockchain.piccions.compiler.parser.instructions.pushable;

import ch.samt.blockchain.piccions.bytecode.ByteCode;
import ch.samt.blockchain.piccions.compiler.assembler.Assembler;

public class LiteralExpression implements NumericExpression {

    private byte value;

    public LiteralExpression(byte value) {
        this.value = value;
    }

    @Override
    public void compile(Assembler builder) {
        builder.add(
            ByteCode.PUSH_I8,
            value
        );
    }

}
