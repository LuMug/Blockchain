package ch.samt.blockchain.piccions.compiler.parser.instructions.expression;

import ch.samt.blockchain.piccions.compiler.assembler.Assembler;
import ch.samt.blockchain.piccions.compiler.assembler.Opcode;

public class AddExpression extends Expression {

    private Expression left;
    private Expression right;

    public AddExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Opcode[] getOpcodes(Assembler assembler) {
        System.out.print("(");
        left.getOpcodes(assembler);
        System.out.print(")+(");
        right.getOpcodes(assembler);
        System.out.print(")");
        
        return null;
    }
    
}
