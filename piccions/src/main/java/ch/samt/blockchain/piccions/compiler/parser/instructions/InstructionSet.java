package ch.samt.blockchain.piccions.compiler.parser.instructions;

import java.util.LinkedList;
import java.util.List;

import ch.samt.blockchain.piccions.compiler.assembler.Assembler;
import ch.samt.blockchain.piccions.compiler.parser.Compilable;


public class InstructionSet implements Compilable {

    private List<Compilable> instructions = new LinkedList<>();

    @Override
    public void compile(Assembler assembler) {
        for (var instruction : instructions) {
            instruction.compile(assembler);
        }
    }
    
}
