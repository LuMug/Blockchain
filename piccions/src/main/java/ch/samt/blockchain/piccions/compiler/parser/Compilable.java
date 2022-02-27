package ch.samt.blockchain.piccions.compiler.parser;

import ch.samt.blockchain.piccions.compiler.assembler.Assembler;

public interface Compilable {
    
    void compile(Assembler assembler);

}
