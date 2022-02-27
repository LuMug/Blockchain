package ch.samt.blockchain.piccions.compiler.lexer.tokens;

/**
 * Represents a string literal.
 */
public class StringLiteral implements Token {

    private String value;

    public StringLiteral(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
    
}
