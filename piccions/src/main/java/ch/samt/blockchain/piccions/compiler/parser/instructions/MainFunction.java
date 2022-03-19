package ch.samt.blockchain.piccions.compiler.parser.instructions;

import ch.samt.blockchain.piccions.compiler.assembler.Assembler;
import ch.samt.blockchain.piccions.compiler.assembler.Opcode;

public class MainFunction implements Function {

    private InstructionSet body;

    public MainFunction(InstructionSet body) {
        this.body = body;
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
        return assembler.mainFunc(body.getOpcodes(assembler));
    }
    
}