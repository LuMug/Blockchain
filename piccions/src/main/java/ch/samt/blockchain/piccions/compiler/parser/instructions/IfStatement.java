package ch.samt.blockchain.piccions.compiler.parser.instructions;

import ch.samt.blockchain.piccions.compiler.assembler.Assembler;
import ch.samt.blockchain.piccions.compiler.parser.Compilable;
import ch.samt.blockchain.piccions.compiler.parser.instructions.pushable.BoolExpression;

public class IfStatement implements Compilable {

    private BoolExpression condition;
    private Compilable body;

    @Override
    public void compile(Assembler assembler) {
        
    }
    
}
