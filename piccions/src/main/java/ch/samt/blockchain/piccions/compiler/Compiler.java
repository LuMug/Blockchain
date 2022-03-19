package ch.samt.blockchain.piccions.compiler;

import java.util.LinkedList;
import java.util.List;

import ch.samt.blockchain.piccions.bytecode.ByteCode;
import ch.samt.blockchain.piccions.compiler.assembler.Assembler;
import ch.samt.blockchain.piccions.compiler.parser.Parser;
import ch.samt.blockchain.piccions.compiler.parser.instructions.Declaration;
import ch.samt.blockchain.piccions.compiler.parser.instructions.Function;
import ch.samt.blockchain.piccions.compiler.parser.instructions.FunctionCall;
import ch.samt.blockchain.piccions.compiler.parser.instructions.FunctionDeclaration;
import ch.samt.blockchain.piccions.compiler.parser.instructions.InstructionSet;
import ch.samt.blockchain.piccions.compiler.parser.instructions.MainFunction;
import ch.samt.blockchain.piccions.compiler.parser.instructions.std.StdLibrary;
import ch.samt.blockchain.piccions.vm.VirtualMachine;

public class Compiler {
    
    public static Assembler compile(String code) throws SyntaxException, CompileException {

        class CompilerProcessor {

            private InstructionSet root;

            private List<FunctionDeclaration> functions = new LinkedList<>();
            private MainFunction mainFunction;

            public CompilerProcessor(InstructionSet root) {
                this.root = root;
            }

            private void processFunction(Function function) throws CompileException {
                var instructions = function.getBody().getInstructions();

                List<String> params = new LinkedList<>();

                for (var instruction : instructions) {
                    if (instruction instanceof FunctionCall call) {
                        var name = call.getName();

                        // Check if function exists
                        if (!contains(functions, f -> f.getName().equals(name))) {
                            if (name.equals("main")) {
                                throw new CompileException("Cannot call main function: " + name);
                            } else {
                                throw new CompileException("No function defined: " + name);
                            }
                        }

                        // TODO: check params

                        continue;
                    }

                    if (instruction instanceof Declaration declaration) {
                        // TODO
                    }
                }
            }

            // Processes and validates the abstract syntax tree.
            private void processAST() throws CompileException {
                var instructions = root.getInstructions();
                
                // Process function definitions
                for (var instruction : instructions) {
                    if (instruction instanceof FunctionDeclaration function) {
                        
                        // Check for duplicate name
                        if (contains(functions, f -> f.getName().equals(function.getName()))) {
                            throw new CompileException("Duplicate function: " + function.getName());
                        }

                        functions.add(function);
                        continue;
                    }
                    
                    // Check if it is main function
                    if (instruction instanceof MainFunction main) {
                        // Check for duplicate main function
                        if (mainFunction != null) {
                            throw new CompileException("Duplicate main function");    
                        }

                        this.mainFunction = main;
                    } else {
                        throw new CompileException("Invalid instruction");
                    }
                }

                // Process functions
                processFunction(mainFunction);
                for (var function : functions) {
                    processFunction(function);
                }

                // Check if no main function has been defined
                if (mainFunction == null) {
                    throw new CompileException("No main function defined");    
                }

                // Put main function on top as request by the assembler
                instructions.remove(mainFunction);
                instructions.add(0, mainFunction);
            }

            private void injectStdLibrary() {
                for (var function : StdLibrary.getStdFunctions()) {
                    root.addInstruction(function);
                }
            }

            public Assembler compile() throws CompileException {
                injectStdLibrary();
                processAST();
                
                var assembler = new Assembler();
                assembler.add(root.getOpcodes(assembler));
                return assembler;
            }

            private static <T> boolean contains(List<T> list, java.util.function.Function<T, Boolean> matcher) {
                for (var el : list) {
                    if (matcher.apply(el)) {
                        return true;
                    }
                }
                return false;
            }

        }

        return new CompilerProcessor(Parser.getAbtractSyntaxTree(code)).compile();
    }

    public static void main(String[] args) {
        try {
            String code = """
                func main() {
                    let variable = 10;
                
                    while variable / 10 {
                        print(variable);
                        variable = variable + 1;
                    }
                }
            """;

            var assembler = Compiler.compile(code);
            var bytecode = assembler.assemble();
            System.out.println(assembler);
            new VirtualMachine(bytecode).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // SEMICOLON CHECK
        // TODO: support empty functions
        // check variables
        // check param size/count
        // check(instructionSet) -> recursive
    }

}
