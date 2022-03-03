package ch.samt.blockchain.piccions.compiler.lexer.tokens;

import ch.samt.blockchain.piccions.compiler.SyntaxException;

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

    public static void assertType(Token t, String errorMessage)
            throws SyntaxException {
        if (!(t instanceof Char)) {
            throw new SyntaxException(errorMessage);
        }
    }

}
