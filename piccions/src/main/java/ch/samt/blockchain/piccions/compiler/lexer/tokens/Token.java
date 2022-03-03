package ch.samt.blockchain.piccions.compiler.lexer.tokens;

import ch.samt.blockchain.piccions.compiler.SyntaxException;

public interface Token {

    public String getValue();

    default void assertValue(String value, String errorMessage) throws SyntaxException {
        if (!value.equals(getValue())) {
            throw new SyntaxException(errorMessage);
        }
    }
    
}