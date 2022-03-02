package ch.samt.blockchain.piccions;

import ch.samt.blockchain.piccions.compiler.assembler.Assembler;
import ch.samt.blockchain.piccions.vm.VirtualMachine;
import static ch.samt.blockchain.piccions.bytecode.ByteCode.*;
import static ch.samt.blockchain.piccions.compiler.assembler.Assembler.*;


public class Test {
    
    public static void main(String[] args) {
        var assembler = new Assembler();

        assembler.add(
            assembler.mainFunc(
                buildInstructions(
                    buildInstructions(
                        buildInstruction(PUSH_I8),
                        assembler.variable("variable", (byte) 10)
                    ),
                    assembler.whileLoop(
                        buildInstructions( // CONDITION
                            buildInstruction(LOAD), // push var
                            assembler.variable("variable"),
                            buildInstruction(PUSH_I8),   // push 10
                            buildInstruction((byte) 10),
                            buildInstruction(DIV_I8),    // div
                            buildInstruction(PUSH_I8),   // push 1
                            buildInstruction((byte) 1),
                            buildInstruction(EQUALS_I8) // compare
                        ),
                        buildInstructions( // BODY
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
                            ),
                            buildInstructions(
                                assembler.invokeFunc("print42") // print42
                            )
                        )
                    )
                )
            )
        );

        assembler.add(
                assembler.declareFunc("print42", buildInstructions(
                buildInstruction(PUSH_I8),
                buildInstruction((byte) 42),
                buildInstruction(PRINT_I8)
            ))
        );
        
        // PUSH %POS% dovrebbe avere un -1 STACK
        
        // TODO il push dei parametri dovrebbe avere -(PARAM SIZE) STACK

        // TODO l'inizio della funzione dovrebbe avere un +(PARAM SIZE) STACK
        
        // TODO l'inizio della funzione dovrebbe essere il stackMaster di tutti i parametri

        // TODO I parametri vanno chiamati con .param(index) -> uguale a .variable
        // ma hanno un incrementOption

        var bytecode = assembler.compile(); // assemble

        System.out.println(assembler);

        var vm = new VirtualMachine(bytecode);
        vm.execute();
    }

}
