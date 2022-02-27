package ch.samt.blockchain.piccions.compiler.parser.instructions.pushable;

import ch.samt.blockchain.piccions.bytecode.ByteCode;
import ch.samt.blockchain.piccions.compiler.assembler.Assembler;

public class SumExpression implements NumericExpression {

    private NumericExpression left;
    private NumericExpression right;

    public SumExpression(NumericExpression left, NumericExpression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public void compile(Assembler builder) {
        left.compile(builder);
        right.compile(builder);
        builder.add(
            ByteCode.ADD_I8
        );
    }
    
}
