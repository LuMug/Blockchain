package ch.samt.blockchain.piccions.compiler.parser.instructions;

import java.util.LinkedList;
import java.util.List;

import ch.samt.blockchain.piccions.compiler.assembler.Assembler;
import ch.samt.blockchain.piccions.compiler.assembler.Opcode;

public class Function implements Compilable {

    private List<Parameter> parameters = new LinkedList<>();
    private InstructionSet body;
    private String name;

    public Function(String name) {
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

    public void setBody(InstructionSet body) {
        this.body = body;
    }

    public InstructionSet getBody() {
        return body;
    }

    @Override
    public Opcode[] getOpcodes(Assembler assembler) {
        // TODO Auto-generated method stub
        return null;
    }
    
}