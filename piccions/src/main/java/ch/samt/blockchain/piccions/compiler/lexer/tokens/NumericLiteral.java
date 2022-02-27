package ch.samt.blockchain.piccions.compiler.lexer.tokens;

/**
 * Represents a numeric literal.
 */
public class NumericLiteral implements Token {

    private String value;

    public NumericLiteral(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static boolean isValidCharacter(char c) {
        return c >= '0' && c <= '9';
    }
    
}
