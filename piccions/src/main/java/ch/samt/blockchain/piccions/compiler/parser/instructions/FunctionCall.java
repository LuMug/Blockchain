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

    public String getName() {
        return name;
    }

    @Override
    public Opcode[] getOpcodes(Assembler assembler) {
        if (parameters.size() == 0) { // no parameters
            return assembler.invokeFunc(name);
        }

        // push parameters
        List<Opcode> push = new LinkedList<>();
        for (var instr : parameters) {
            var chunk = instr.getOpcodes(assembler);
            for (var opcode : chunk) {
                push.add(opcode);
            }
        }

        var pushParams = push.toArray(new Opcode[push.size()]);

        return assembler.invokeFuncWithParams(name, pushParams);
    }
    
}
