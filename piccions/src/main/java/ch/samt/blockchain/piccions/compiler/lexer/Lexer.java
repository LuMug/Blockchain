package ch.samt.blockchain.piccions.compiler.lexer;

import java.util.LinkedList;
import java.util.List;

import ch.samt.blockchain.piccions.compiler.SyntaxException;
import ch.samt.blockchain.piccions.compiler.lexer.tokens.Char;
import ch.samt.blockchain.piccions.compiler.lexer.tokens.Identifier;
import ch.samt.blockchain.piccions.compiler.lexer.tokens.NumericLiteral;
import ch.samt.blockchain.piccions.compiler.lexer.tokens.StringLiteral;
import ch.samt.blockchain.piccions.compiler.lexer.tokens.Token;

public class Lexer {
    
    public static List<Token> tokenize(String code) throws SyntaxException {
        
        class LexerProcessor {
            
            private String code;
            private char currentChar;
            private int currentPos;
            
            public LexerProcessor(String code) {
                this.code = code;
                this.currentPos = -1;
            }

            private boolean hasEnded() {
                return currentPos >= code.length();
            }

            private void nextChar() {
                ++currentPos;

                if (hasEnded()) {
                    return;
                }

                currentChar = code.charAt(currentPos);
            }

            private void skip(char c) {
                while (!hasEnded() && currentChar == c) {
                    nextChar();
                }
            }

            /**
             * currentChat == '
             * @return
             * @throws SyntaxException
             */
            private String parseStringLiteral() throws SyntaxException {
                var builder = new StringBuilder();

                boolean escape = false;

                nextChar();

                while (currentChar != '\'' || currentChar == '\'' && escape) {
                    if (hasEnded()) {
                        throw new SyntaxException("String literal not closed");
                    }

                    if (currentChar == '\\') {
                        if (!escape) {
                            escape = true;
                            nextChar();
                            continue;
                        }
                    }

                    escape = false;

                    builder.append(currentChar);
                    nextChar();
                }

                nextChar();

                return builder.toString();
            }

            private String parseIdentifier() throws SyntaxException {
                var builder = new StringBuilder();

                while (!hasEnded() && Identifier.isValidCharacter(currentChar)) {
                    builder.append(currentChar);
                    nextChar();
                }

                return builder.toString();
            }

            private String parseNumericLiteral() throws SyntaxException {
                var builder = new StringBuilder();

                while (!hasEnded() && NumericLiteral.isValidCharacter(currentChar)) {
                    builder.append(currentChar);
                    nextChar();
                }

                return builder.toString();
            }
            
            public List<Token> tokenize() throws SyntaxException {
                List<Token> result = new LinkedList<>();

                this.code = code.replaceAll("\n", "");
                this.code = code.replaceAll("\r", "");
                
                nextChar();
                while (!hasEnded()) {
                    switch (currentChar) {
                        case ' ' -> skip(' ');
                        case '\'' -> result.add(new StringLiteral(parseStringLiteral()));
                        default -> {
                            if (NumericLiteral.isValidCharacter(currentChar)) { // Priority over identifier. Identifiers cannot start with number.
                                result.add(new NumericLiteral(parseNumericLiteral()));
                            } else if (Identifier.isValidCharacter(currentChar)) {
                                result.add(new Identifier(parseIdentifier()));
                            } else {
                                result.add(new Char(currentChar));
                                nextChar();
                            }
                        }
                    }

                }
                
                return result;
            }
            
        }
        
        return new LexerProcessor(code).tokenize();
    }

}
