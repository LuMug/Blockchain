package ch.samt.blockchain.piccions.compiler.parser.instructions;

import ch.samt.blockchain.piccions.compiler.assembler.Assembler;
import ch.samt.blockchain.piccions.compiler.assembler.Opcode;

public record IfElseStatement(Pushable condition, InstructionSet ifBody, InstructionSet elseBody) implements Compilable {

    @Override
    public Opcode[] getOpcodes(Assembler assembler) {
        return assembler.ifElseStatement(
            condition.getOpcodes(assembler),
            ifBody.getOpcodes(assembler),
            elseBody.getOpcodes(assembler)
        );
    }
    
}