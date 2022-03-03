package ch.samt.blockchain.piccions.compiler.parser;

import java.util.LinkedList;
import java.util.List;

import ch.samt.blockchain.piccions.compiler.SyntaxException;
import ch.samt.blockchain.piccions.compiler.lexer.Lexer;
import ch.samt.blockchain.piccions.compiler.lexer.tokens.Identifier;
import ch.samt.blockchain.piccions.compiler.lexer.tokens.Token;
import ch.samt.blockchain.piccions.compiler.parser.instructions.Compilable;
import ch.samt.blockchain.piccions.compiler.parser.instructions.Function;
import ch.samt.blockchain.piccions.compiler.parser.instructions.InstructionSet;
import ch.samt.blockchain.piccions.compiler.parser.instructions.Parameter;

public class Parser {
    
    public static final String FUNCTION_DEFINITION = "func";
    public static final String FUNCTION_PARAM_OPENER = "(";
    public static final String FUNCTION_PARAM_CLOSER = ")";
    public static final String FUNCTION_BODY_OPENER = "{";
    public static final String FUNCTION_BODY_CLOSER = "}";
    public static final String PARAMETER_SEPARATOR = ",";
    public static final String WHILE_BODY_OPENER = "{";
    public static final String WHILE_BODY_CLOSER = "}";
    public static final String ELSE_BODY_OPENER = "{";
    public static final String ELSE_BODY_CLOSER = "}";
    public static final String IF_BODY_OPENER = "{";
    public static final String LESS_OPERATOR = "<";
    public static final String ADD_OPERATOR = "+";
    public static final String SUB_OPERATOR = "-";
    public static final String MUL_OPERATOR = "*";
    public static final String DIV_OPERATOR = "/";
    public static final String IF_STATEMENT = "if";
    public static final String IF_BODY_CLOSER = "}";
    public static final String EQUALS_OPERATOR = "==";
    public static final String ASSIGN_OPERATOR = "=";
    public static final String GREATER_OPERATOR = ">";
    public static final String ELSE_STATEMENT = "else";
    public static final String DECLARE_VARIABLE = "let";
    public static final String INSTRUCTION_CLOSER = ";";
    public static final String WHILE_STATEMENT = "while";

    public static InstructionSet getAbtractSyntaxTree(String code) throws SyntaxException {
        
        class ParserProcessor {
            
            private List<Token> tokens;
            private Token currentToken;
            private int currentPos;
            
            public ParserProcessor(List<Token> tokens) {
                this.tokens = tokens;
                this.currentPos = -1;
            }

            private boolean hasEnded() {
                return currentPos >= tokens.size();
            }

            private void nextToken() {
                ++currentPos;

                if (hasEnded()) {
                    return;
                }

                currentToken = tokens.get(currentPos);
            }

            private List<Parameter> parseParameters() throws SyntaxException {
                List<Parameter> parameters = new LinkedList<>();

                while (!currentToken.getValue().equals(FUNCTION_PARAM_OPENER)) {
                    nextToken();
                    String name = currentToken.getValue();
                    nextToken();
                    String type = currentToken.getValue();
                    nextToken();
                    currentToken.assertValue(PARAMETER_SEPARATOR, "Expected '" + PARAMETER_SEPARATOR + "'");
                    nextToken();

                    parameters.add(new Parameter(name, type));
                }

                return parameters;
            }

            private Compilable parseInstruction() {
                return switch (currentToken.getValue()) {
                    case DECLARE_VARIABLE -> null;
                    case IF_STATEMENT -> null;
                    case WHILE_STATEMENT -> null;
                    default -> null;
                };
            }

            private Compilable parseFunction() throws SyntaxException {
                nextToken();
                Identifier.assertType(currentToken, "Expected function name");
                String functionName = currentToken.getValue();
                nextToken();
                currentToken.assertValue(FUNCTION_PARAM_OPENER, "Expected '" + FUNCTION_PARAM_OPENER + "'");
                
                Function function = new Function(functionName);

                List<Parameter> params = parseParameters();
                nextToken();

                for (var param : params) {
                    function.addParameter(param);
                }

                currentToken.assertValue(FUNCTION_BODY_OPENER, "Expected '" + FUNCTION_BODY_OPENER + "'");
                nextToken();

                InstructionSet body = new InstructionSet();

                while (!currentToken.getValue().equals(FUNCTION_BODY_CLOSER)) {
                    body.addInstruction(parseInstruction());
                    nextToken();
                }

                function.setBody(body);

                return function;
            }

            public InstructionSet generateTree() throws SyntaxException {
                InstructionSet root = new InstructionSet();

                nextToken();
                while (!hasEnded()) {
                    if (!currentToken.getValue().equals(FUNCTION_DEFINITION)) {
                        throw new SyntaxException("Unexpected identifier: " + currentToken.getValue());
                    }

                    root.addInstruction(parseFunction());

                    nextToken();
                }

                return root;
            }

        }

        return new ParserProcessor(Lexer.tokenize(code)).generateTree();
    }

}