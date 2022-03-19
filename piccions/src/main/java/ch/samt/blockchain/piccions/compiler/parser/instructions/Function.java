package ch.samt.blockchain.piccions.compiler.parser.instructions;

public interface Function extends Compilable {
    
    public void setBody(InstructionSet body);

    public InstructionSet getBody();

}
