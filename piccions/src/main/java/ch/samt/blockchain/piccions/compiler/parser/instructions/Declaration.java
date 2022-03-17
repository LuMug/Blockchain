package ch.samt.blockchain.piccions.compiler.parser.instructions;

import ch.samt.blockchain.piccions.compiler.assembler.Assembler;
import ch.samt.blockchain.piccions.compiler.assembler.Opcode;

public record Declaration(String variable, Pushable value) implements Compilable {

    @Override
    public Opcode[] getOpcodes(Assembler assembler) {
        System.out.print("LET " + variable + " ");
        value.getOpcodes(assembler);
        System.out.println();
        return null;
    }

}
