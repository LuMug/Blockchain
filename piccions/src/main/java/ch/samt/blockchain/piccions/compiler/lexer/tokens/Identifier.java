package ch.samt.blockchain.piccions.compiler.lexer.tokens;

/**
 * Represents a keyword, function name or variable name.
 */
public class Identifier implements Token {

    private String value;

    public Identifier(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static boolean isValidCharacter(char c) {
        return c == '_' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9';
    }
    
}
