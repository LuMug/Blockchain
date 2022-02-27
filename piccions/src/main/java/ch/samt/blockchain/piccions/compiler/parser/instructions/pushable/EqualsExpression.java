package ch.samt.blockchain.piccions.compiler.parser.instructions.pushable;

import ch.samt.blockchain.piccions.bytecode.ByteCode;
import ch.samt.blockchain.piccions.compiler.assembler.Assembler;

public class EqualsExpression implements BoolExpression {
    
    private Pushable left;
    private Pushable right;

    public EqualsExpression(Pushable left, Pushable right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void compile(Assembler builder) {
        left.compile(builder);
        right.compile(builder);
        builder.add(
            ByteCode.MUL_I8
        );
    }

}
