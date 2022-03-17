package ch.samt.blockchain.piccions.compiler.parser.instructions.expression;

import ch.samt.blockchain.piccions.bytecode.ByteCode;
import ch.samt.blockchain.piccions.compiler.assembler.Assembler;
import ch.samt.blockchain.piccions.compiler.assembler.Opcode;

public class MulExpression extends Expression {

    private Expression left;
    private Expression right;

    public MulExpression(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public Opcode[] getOpcodes(Assembler assembler) {
        //System.out.print("(");
        //left.getOpcodes(assembler);
        //System.out.print(")*(");
        //right.getOpcodes(assembler);
        //System.out.print(")");
        
        var pushLeft = left.getOpcodes(assembler);
        var pushRight = right.getOpcodes(assembler);
        var result = new Opcode[pushLeft.length + pushRight.length + 1];
        int i = 0;
        for (int j = 0; j < pushLeft.length; j++) {
            result[i++] = pushLeft[j];
        }
        for (int j = 0; j < pushRight.length; j++) {
            result[i++] = pushRight[j];
        }
        result[i] = Assembler.buildInstruction(ByteCode.MUL_I8);

        return result;
    }
    
}