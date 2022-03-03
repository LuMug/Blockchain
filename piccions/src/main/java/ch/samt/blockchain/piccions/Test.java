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

        var bytecode = assembler.assemble(); // assemble

        System.out.println(assembler);
        print(bytecode);

        var vm = new VirtualMachine(bytecode);
        vm.execute();
    }

    private static void print(byte[] arr) {
        StringBuilder builder = new StringBuilder();

        builder.append("new byte[]{");
        for (int i = 0; i < arr.length; i++) {
            builder.append(arr[i] + (i == arr.length - 1 ? "};" : ","));
        }
        System.out.println(builder.toString());
    }

}
