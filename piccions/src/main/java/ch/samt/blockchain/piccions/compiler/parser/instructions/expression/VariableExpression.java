package ch.samt.blockchain.piccions.compiler.parser.instructions.expression;

import ch.samt.blockchain.piccions.bytecode.ByteCode;
import ch.samt.blockchain.piccions.compiler.assembler.Assembler;
import ch.samt.blockchain.piccions.compiler.assembler.Opcode;

public class VariableExpression extends Expression {

    private String name;

    public VariableExpression(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Opcode[] getOpcodes(Assembler assembler) {
        return new Opcode[] {
            Assembler.buildInstruction(ByteCode.LOAD),
            assembler.variable(name)
        };
    }
    
}
