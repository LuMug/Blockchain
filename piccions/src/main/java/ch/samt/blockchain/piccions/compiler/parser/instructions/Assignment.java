package ch.samt.blockchain.piccions.compiler.parser.instructions;

import ch.samt.blockchain.piccions.compiler.assembler.Assembler;
import ch.samt.blockchain.piccions.compiler.assembler.Opcode;

public record Assignment(String variable, Pushable value) implements Compilable {

    @Override
    public Opcode[] getOpcodes(Assembler assembler) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
