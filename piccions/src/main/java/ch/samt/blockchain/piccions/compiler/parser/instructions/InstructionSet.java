package ch.samt.blockchain.piccions.compiler.parser.instructions;

import java.util.LinkedList;
import java.util.List;

import ch.samt.blockchain.piccions.compiler.assembler.Assembler;
import ch.samt.blockchain.piccions.compiler.assembler.Opcode;


public class InstructionSet implements Compilable {

    private List<Compilable> instructions = new LinkedList<>();

    public void addInstruction(Compilable instruction) {
        instructions.add(instruction);
    }

    @Override
    public Opcode[] getOpcodes(Assembler assembler) {
        return null;
    }
    
}
