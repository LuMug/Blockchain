package ch.samt.blockchain.piccions.compiler.parser.instructions;

import ch.samt.blockchain.piccions.bytecode.ByteCode;
import ch.samt.blockchain.piccions.compiler.assembler.Assembler;
import ch.samt.blockchain.piccions.compiler.assembler.Opcode;

public record Assignment(String variable, Pushable value) implements Compilable {

    @Override
    public Opcode[] getOpcodes(Assembler assembler) {
        return Assembler.buildInstructions(
            value.getOpcodes(assembler),
            Assembler.buildInstructions(
                Assembler.buildInstruction(ByteCode.STORE),
                assembler.variable(variable)
            )
        );
    }
    
}