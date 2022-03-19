package ch.samt.blockchain.piccions.compiler.parser.instructions.std;

import ch.samt.blockchain.piccions.bytecode.ByteCode;
import ch.samt.blockchain.piccions.compiler.assembler.Assembler;
import ch.samt.blockchain.piccions.compiler.assembler.Opcode;
import ch.samt.blockchain.piccions.compiler.parser.instructions.FunctionDeclaration;
import ch.samt.blockchain.piccions.compiler.parser.instructions.InstructionSet;
import ch.samt.blockchain.piccions.compiler.parser.instructions.Parameter;

public class Print {

    public final static FunctionDeclaration instance = new FunctionDeclaration("print") {
        {
            addParameter(new Parameter("input", "[ch]", 1));
        }

        @Override
        public InstructionSet getBody() {
            return new InstructionSet(); // empty instruction set
        }

        @Override
        public Opcode[] getOpcodes(Assembler assembler) {
            return assembler.declareFuncWithParams("print", new int[]{1}, Assembler.buildInstructions(
                Assembler.buildInstruction(ByteCode.LOAD),
                assembler.param("print", 0),
                Assembler.buildInstruction(ByteCode.PRINT_I8)
            ));
        }
    };
    
}
