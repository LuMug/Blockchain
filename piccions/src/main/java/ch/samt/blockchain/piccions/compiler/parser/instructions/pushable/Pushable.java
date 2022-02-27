package ch.samt.blockchain.piccions.compiler.parser.instructions.pushable;

import ch.samt.blockchain.piccions.compiler.parser.Compilable;

public interface Pushable extends Compilable {
    
}

/*
import compiler.assembler.Assembler;

public interface Expression {
    
    void compile(Assembler builder);

    // https://stackoverflow.com/questions/3422673/how-to-evaluate-a-math-expression-given-in-string-form
    public static Expression eval(final String str) {
        return new Object() {
            int pos = -1, ch;
    
            void nextChar() {
                ch = ++pos < str.length() ? str.charAt(pos) : -1;
            }
    
            boolean eat(int charToEat) {
                while (ch == ' ') {
                    nextChar();
                }

                if (ch == charToEat) {
                    nextChar();
                    return true;
                }

                return false;
            }
    
            Expression parse() {
                nextChar();
                Expression x = parseExpression();
                
                if (pos < str.length()) {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                return x;
            }
    
            Expression parseExpression() {
                Expression x = parseTerm();

                while (true) {
                    if (eat('+')) {
                        x = new SumExpression(x, parseTerm());
                    } else if (eat('-')) {
                        x = new SubExpression(x, parseTerm());
                    } else {
                        return x;
                    }
                }
            }
    
            Expression parseTerm() {
                Expression x = parseFactor();
                
                while (true) {
                    if (eat('*')) {
                        x = new ProdExpression(x, parseFactor());
                    } else if (eat('/')) {
                        x = new DivExpression(x, parseFactor());
                    } else {
                        return x;
                    }
                }
            }
    
            Expression parseFactor() {
                if (eat('+')) {
                    return parseFactor();
                }

                if (eat('-')) {
                    return new ProdExpression(parseFactor(), new NumericExpression((byte) -1)); // NegateExpression
                }
    
                Expression x;
                int startPos = this.pos;

                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') {
                        nextChar();
                    }

                    x = new NumericExpression(Byte.parseByte(str.substring(startPos, this.pos)));
                } else if (ch >= 'a' && ch <= 'z') {
                    while (ch >= 'a' && ch <= 'z') {
                        nextChar();
                    }

                    String val = str.substring(startPos, this.pos);
                    x = parseFactor(); // prende (...)
                    if (!eat('(')) {
                        System.out.println("variabile:\t " + val);
                    } else {
                        System.out.println("funzione:\t " + val);
                    }

                    // str  ()  <- func
                    // str      <- var
                    //throw new IllegalArgumentException("Function not found: " + val);
                } else {
                    throw new IllegalArgumentException("Unexpected: " + (char) ch);
                }
    
                return x;
            }
        }.parse();
    }

}*/
