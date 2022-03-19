package ch.samt.blockchain.piccions.compiler.parser.instructions;

import java.util.LinkedList;
import java.util.List;

import ch.samt.blockchain.piccions.compiler.assembler.Assembler;
import ch.samt.blockchain.piccions.compiler.assembler.Opcode;

public class FunctionDeclaration implements Function {

    private List<Parameter> parameters = new LinkedList<>();
    private InstructionSet body;
    private String name;

    public FunctionDeclaration(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void addParameter(Parameter parameter) {
        parameters.add(parameter);
    }

    @Override
    public void setBody(InstructionSet body) {
        this.body = body;
    }

    @Override
    public InstructionSet getBody() {
        return body;
    }

    @Override
    public Opcode[] getOpcodes(Assembler assembler) {
        if (parameters.size() == 0) { // no parameters
            return assembler.declareFunc(name, body.getOpcodes(assembler));
        }

        var paramSizes = new int[parameters.size()];

        int i = 0;
        for (var parameter : parameters) {
            paramSizes[i++] = parameter.size();
        }

        return assembler.declareFuncWithParams(name, paramSizes, body.getOpcodes(assembler));
    }
    
}
