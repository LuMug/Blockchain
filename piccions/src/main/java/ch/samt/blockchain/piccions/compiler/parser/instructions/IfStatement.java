package ch.samt.blockchain.piccions.compiler.parser.instructions;

import ch.samt.blockchain.piccions.compiler.assembler.Assembler;
import ch.samt.blockchain.piccions.compiler.assembler.Opcode;

public record IfStatement(Pushable condition, InstructionSet body) implements Compilable {

    @Override
    public Opcode[] getOpcodes(Assembler assembler) {
        return assembler.ifStatement(condition.getOpcodes(assembler), body.getOpcodes(assembler));
    }
    
}
