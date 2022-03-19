package ch.samt.blockchain.piccions.compiler.parser;

import java.util.LinkedList;
import java.util.List;

import ch.samt.blockchain.piccions.compiler.SyntaxException;
import ch.samt.blockchain.piccions.compiler.lexer.Lexer;
import ch.samt.blockchain.piccions.compiler.lexer.tokens.Identifier;
import ch.samt.blockchain.piccions.compiler.lexer.tokens.NumericLiteral;
import ch.samt.blockchain.piccions.compiler.lexer.tokens.Token;
import ch.samt.blockchain.piccions.compiler.parser.instructions.Assignment;
import ch.samt.blockchain.piccions.compiler.parser.instructions.Compilable;
import ch.samt.blockchain.piccions.compiler.parser.instructions.Declaration;
import ch.samt.blockchain.piccions.compiler.parser.instructions.FunctionDeclaration;
import ch.samt.blockchain.piccions.compiler.parser.instructions.FunctionCall;
import ch.samt.blockchain.piccions.compiler.parser.instructions.InstructionSet;
import ch.samt.blockchain.piccions.compiler.parser.instructions.MainFunction;
import ch.samt.blockchain.piccions.compiler.parser.instructions.Parameter;
import ch.samt.blockchain.piccions.compiler.parser.instructions.Pushable;
import ch.samt.blockchain.piccions.compiler.parser.instructions.expression.AddExpression;
import ch.samt.blockchain.piccions.compiler.parser.instructions.expression.DivExpression;
import ch.samt.blockchain.piccions.compiler.parser.instructions.expression.Expression;
import ch.samt.blockchain.piccions.compiler.parser.instructions.expression.LiteralExpression;
import ch.samt.blockchain.piccions.compiler.parser.instructions.expression.MulExpression;
import ch.samt.blockchain.piccions.compiler.parser.instructions.expression.SubExpression;
import ch.samt.blockchain.piccions.compiler.parser.instructions.expression.VariableExpression;

public class Parser {
    
    public static final String FUNCTION_DEFINITION = "func";
    public static final String FUNCTION_PARAM_OPENER = "(";
    public static final String FUNCTION_PARAM_CLOSER = ")";
    public static final String FUNCTION_BODY_OPENER = "{";
    public static final String FUNCTION_BODY_CLOSER = "}";
    public static final String PARAMETER_SEPARATOR = ",";
    public static final String PRECEDENCE_OPENER = "(";
    public static final String PRECEDENCE_CLOSER = ")";
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
    public static final String ASSIGN_OPERATOR = "=";
    public static final String EQUALS_OPERATOR = "==";
    public static final String MAIN_FUNCTION = "main";
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

                nextToken();
                while (!currentToken.getValue().equals(FUNCTION_PARAM_CLOSER)) {
                    String name = currentToken.getValue();
                    nextToken();
                    String type = currentToken.getValue();
                    nextToken();

                    // Size of 1
                    parameters.add(new Parameter(name, type, 1));

                    if (!currentToken.getValue().equals(FUNCTION_PARAM_CLOSER)) {
                        currentToken.assertValue(PARAMETER_SEPARATOR, "Expected '" + PARAMETER_SEPARATOR + "'");
                        nextToken();
                    }
                }

                return parameters;
            }

            private Compilable parseDeclaration() throws SyntaxException {
                String name = currentToken.getValue();
                nextToken();
                currentToken.assertValue(ASSIGN_OPERATOR, "Expected '" + ASSIGN_OPERATOR + "'");
                nextToken();
                Pushable value = parseExpression();
                nextToken();
                return new Declaration(name, value);
            }

            private Compilable parseIfStatement() throws SyntaxException {
                throw new SyntaxException("Not implemented yet");
            }

            private Compilable parseWhileStatement() throws SyntaxException {
                throw new SyntaxException("Not implemented yet");
            }

            private Expression parseExpression3() throws SyntaxException {
                if (currentToken.getValue().equals(ADD_OPERATOR)) {
                    nextToken();
                    return parseExpression3();
                }

                if (currentToken.getValue().equals(SUB_OPERATOR)) {
                    nextToken();
                    return new MulExpression(new LiteralExpression(-1), parseExpression3());
                }
                
                Expression x = null;

                if (currentToken.getValue().equals(PRECEDENCE_OPENER)) {
                    nextToken();
                    x = parseExpression1();
                } else if (currentToken instanceof NumericLiteral) {
                    x = new LiteralExpression(Integer.parseInt(currentToken.getValue()));
                } else {
                    x = new VariableExpression(currentToken.getValue());
                    // or function
                }
                
                //nextToken();
                //currentToken.assertValue(PRECEDENCE_CLOSER, "Expected: '" + PRECEDENCE_CLOSER + "'");

                return x;
            }

            private Expression parseExpression2() throws SyntaxException {
                Expression x = parseExpression3();
                
                while (true) {
                    nextToken();
                    if (currentToken.getValue().equals(MUL_OPERATOR)) {
                        nextToken();
                        x = new MulExpression(x, parseExpression3());
                    } else if (currentToken.getValue().equals(DIV_OPERATOR)) {
                        nextToken();
                        x = new DivExpression(x, parseExpression3());
                    } else {
                        return x;
                    }
                }
            }

            private Expression parseExpression1() throws SyntaxException {
                Expression x = parseExpression2();
                
                while (true) {
                    if (currentToken.getValue().equals(ADD_OPERATOR)) {
                        nextToken();
                        x = new AddExpression(x, parseExpression2());
                        // ? x = new AddExpression(x, parseExpression1());
                    } else if (currentToken.getValue().equals(SUB_OPERATOR)) {
                        nextToken();
                        x = new SubExpression(x, parseExpression2());
                        // ? x = new AddExpression(x, parseExpression1());
                    } else {
                        if (!(currentToken.getValue().equals(PRECEDENCE_CLOSER) || currentToken.getValue().equals(PARAMETER_SEPARATOR))) {
                            currentToken.assertValue(INSTRUCTION_CLOSER, "Expected: '" + INSTRUCTION_CLOSER + "'");
                        }
                        return x;
                    }
                }
            }

            private Pushable parseExpression() throws SyntaxException {
                return parseExpression1();
            }

            private Compilable parseFuncCallOrAssignment() throws SyntaxException {
                String identifier = currentToken.getValue();
                nextToken();

                if (currentToken.getValue().equals(FUNCTION_PARAM_OPENER)) {
                    // is function call
                    
                    List<Pushable> parameters = new LinkedList<>();
                    
                    nextToken();
                    while (!currentToken.getValue().equals(FUNCTION_PARAM_CLOSER)) {
                        // FunctionCallWithParam instead od FunctionCall
                        parameters.add(parseExpression());

                        if (!currentToken.getValue().equals(FUNCTION_PARAM_CLOSER)) {
                            currentToken.assertValue(PARAMETER_SEPARATOR, "Expected: " + PARAMETER_SEPARATOR);
                            nextToken();
                        }
                    }

                    nextToken();
                    currentToken.assertValue(INSTRUCTION_CLOSER, "Expected: '" + INSTRUCTION_CLOSER + "'");
                    nextToken();

                    return new FunctionCall(identifier, parameters);
                }
                
                if (currentToken.getValue().equals(ASSIGN_OPERATOR)) {
                    // is assignment
                    nextToken();
                    var expr = parseExpression();
                    
                    nextToken(); // ?
                    currentToken.assertValue(INSTRUCTION_CLOSER, "Expected: '" + INSTRUCTION_CLOSER + "'");
                    nextToken();
                 
                    
                    return new Assignment(identifier, expr);
                }

                throw new SyntaxException("Unexpected token: " + currentToken.getValue());
            }

            private Compilable parseInstruction() throws SyntaxException {
                return switch (currentToken.getValue()) {
                    case DECLARE_VARIABLE -> {
                        nextToken();
                        yield parseDeclaration();
                    }
                    case IF_STATEMENT -> {
                        nextToken();
                        yield parseIfStatement();
                    }
                    case WHILE_STATEMENT -> {
                        nextToken();
                        yield parseWhileStatement();
                    }
                    default -> {
                        yield parseFuncCallOrAssignment();
                    }
                };
            }

            private Compilable parseFunction() throws SyntaxException {
                nextToken();
                Identifier.assertType(currentToken, "Expected function name");
                String functionName = currentToken.getValue();
                nextToken();
                currentToken.assertValue(FUNCTION_PARAM_OPENER, "Expected '" + FUNCTION_PARAM_OPENER + "'");
                
                var function = new FunctionDeclaration(functionName);

                List<Parameter> params = parseParameters();
                for (var param : params) {
                    function.addParameter(param);
                }

                nextToken();
                currentToken.assertValue(FUNCTION_BODY_OPENER, "Expected '" + FUNCTION_BODY_OPENER + "'");
                nextToken();

                InstructionSet body = new InstructionSet();

                while (!currentToken.getValue().equals(FUNCTION_BODY_CLOSER)) {
                    body.addInstruction(parseInstruction());
                    //nextToken();
                }

                if (functionName.equals(MAIN_FUNCTION)) {
                    if (params.size() != 0) {
                        throw new SyntaxException("Main function must not have parameters");
                    }

                    return new MainFunction(body);
                }

                function.setBody(body);

                return function;
            }

            public InstructionSet generateTree() throws SyntaxException {
                InstructionSet root = new InstructionSet();

                nextToken();
                while (!hasEnded()) {
                    if (!currentToken.getValue().equals(FUNCTION_DEFINITION)) {
                        throw new SyntaxException("Unexpected token: " + currentToken.getValue());
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