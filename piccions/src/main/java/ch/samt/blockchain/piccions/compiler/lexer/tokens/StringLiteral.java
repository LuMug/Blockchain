package ch.samt.blockchain.piccions.compiler.lexer.tokens;

import ch.samt.blockchain.piccions.compiler.SyntaxException;

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

    public static void assertType(Token t, String errorMessage)
            throws SyntaxException {
        if (!(t instanceof StringLiteral)) {
            throw new SyntaxException(errorMessage);
        }
    }
    
}
