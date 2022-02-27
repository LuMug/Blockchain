package ch.samt.blockchain.piccions.compiler.lexer.tokens;

/**
 * Represents a single char token.
 */
public class Char implements Token {
    
    private char value;

    public Char(char value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return Character.toString(value);
    }

}
