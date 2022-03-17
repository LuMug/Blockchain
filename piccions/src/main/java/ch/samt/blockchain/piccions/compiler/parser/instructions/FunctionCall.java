package ch.samt.blockchain.piccions.compiler.parser.instructions;

import java.util.LinkedList;
import java.util.List;

import ch.samt.blockchain.piccions.compiler.assembler.Assembler;
import ch.samt.blockchain.piccions.compiler.assembler.Opcode;

public class FunctionCall implements Compilable {

    private String name;

    private List<Pushable> parameters;

    public FunctionCall(String name) {
        this(name, new LinkedList<>());
    }

    public FunctionCall(String name, List<Pushable> parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    @Override
    public Opcode[] getOpcodes(Assembler assembler) {
        return null;
    }
    
}
