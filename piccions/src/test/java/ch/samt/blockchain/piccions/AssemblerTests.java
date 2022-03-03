package ch.samt.blockchain.piccions;

import org.junit.jupiter.api.Test;

import ch.samt.blockchain.piccions.compiler.assembler.Assembler;

import static ch.samt.blockchain.piccions.bytecode.ByteCode.*;
import static ch.samt.blockchain.piccions.compiler.assembler.Assembler.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.DisplayName;


public class AssemblerTests {
    
    @Test
    @DisplayName("""
        function main() {
            variable = 10
        
            while (variable / 10 == 1) {
                print(variable)
                variable = variable + 1
            }
        }
    """)
    void whileLoop() {
        byte[] expected = new byte[]{0,10,6,1,0,10,4,0,1,5,21,26,6,1,30,6,1,0,1,1,7,2,8,0,20,2,32};

        var assembler = new Assembler();

        assembler.add(
            assembler.mainFunc(
                buildInstructions(
                    buildInstructions(
                        buildInstruction(PUSH_I8),
                        assembler.variable("variable", (byte) 10)
                    ),
                    assembler.whileLoop(
                        buildInstructions(
                            buildInstruction(LOAD), // push var
                            assembler.variable("variable"),
                            buildInstruction(PUSH_I8),   // push 10
                            buildInstruction((byte) 10),
                            buildInstruction(DIV_I8),    // div
                            buildInstruction(PUSH_I8),   // push 1
                            buildInstruction((byte) 1),
                            buildInstruction(EQUALS_I8) // compare
                        ),
                        buildInstructions(
                            buildInstruction(LOAD), // push var
                            assembler.variable("variable"),
                            buildInstruction(PRINT_I8),
                            buildInstruction(LOAD), // push var
                            assembler.variable("variable"),
                            buildInstruction(PUSH_I8),
                            buildInstruction((byte) 1),
                            buildInstruction(ADD_I8),
                            buildInstruction(STORE), // write var
                            assembler.variable("variable")
                        )
                    )
                )
            )
        );

        byte[] actual = assembler.assemble();

        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("""
        func func1() {
            print(42)
        }
        
        func func2() {
            print(24)
        }
        
        func main() {
            if (true) {
                func1()
            } else {
                func2()
            }
        }
    """)
    void ifElseStatement() {
        byte[] expected = new byte[]{0,0,21,12,0,8,20,19,8,1,20,18,0,16,20,25,8,1,32,0,42,30,8,0,22,0,24,30,8,0,22};

        var assembler = new Assembler();

        assembler.add(
            assembler.mainFunc(
                assembler.ifElseStatement(
                    buildInstructions(
                        PUSH_BOOL,
                        FALSE
                    ),
                    assembler.invokeFunc("func1"),
                    assembler.invokeFunc("func2")
                )
            )
        );
        
        assembler.add(
            assembler.declareFunc(
                "func1",
                PUSH_I8,
                (byte) 42,
                PRINT_I8
            )
        );
        
        assembler.add(
            assembler.declareFunc(
                "func2",
                PUSH_I8,
                (byte) 24,
                PRINT_I8
            )
        );

        byte[] actual = assembler.assemble();

        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("""
        func main() {
            let var1 = 66;
            let var2 = 67;
            let var3 = 68;
            print(var3);
            print(var2);
            print(var1);
        }
    """)
    void variables() {
        byte[] expected = new byte[]{0,66,0,67,0,68,6,1,30,6,2,30,6,3,30,32};

        var assembler = new Assembler();

        assembler.add(
            assembler.mainFunc(
                buildInstruction(PUSH_I8),
                assembler.variable("var1", (byte) 66),
                buildInstruction(PUSH_I8),
                assembler.variable("var2", (byte) 67),
                buildInstruction(PUSH_I8),
                assembler.variable("var3", (byte) 68),
                
                buildInstruction(LOAD),
                assembler.variable("var3"),
                buildInstruction(PRINT_I8),
                buildInstruction(LOAD),
                assembler.variable("var2"),
                buildInstruction(PRINT_I8),
                buildInstruction(LOAD),
                assembler.variable("var1"),
                buildInstruction(PRINT_I8)
            )
        );

        byte[] actual = assembler.assemble();

        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("""
        func main() {
            variable = 51
            print_sum(variable, variable)
        }
        
        func print_sum(var1, var2) {
            print(var1, var2);
        }
    """)
    void parameterizedFunction() {
        byte[] expected = new byte[]{0,51,0,10,6,2,6,3,20,11,32,6,2,6,3,1,30,8,2,22};

        var assembler = new Assembler();

        assembler.add(
            assembler.mainFunc(
                buildInstructions(
                    buildInstructions(
                        buildInstruction(PUSH_I8),
                        assembler.variable("variable", (byte) 51)
                    ),
                    assembler.invokeFuncWithParams("print_sum", buildInstructions(
                        buildInstruction(LOAD),
                        assembler.variable("variable"),
                        buildInstruction(LOAD),
                        assembler.variable("variable")
                    ))
                )
            )
        );
        
        assembler.add(
            assembler.declareFuncWithParams("print_sum", new int[]{1,1}, buildInstructions(
                buildInstruction(LOAD),
                assembler.param("print_sum", 0),
                buildInstruction(LOAD),
                assembler.param("print_sum", 1),
                buildInstruction(ADD_I8),
                buildInstruction(PRINT_I8)
            ))
        );

        byte[] actual = assembler.assemble();

        assertArrayEquals(expected, actual);
    }

}