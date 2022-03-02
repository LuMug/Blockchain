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
                assembler.ifElseStatement(
                    buildInstructions(
                        PUSH_BOOL,
                        FALSE
                    ),
                    assembler.invokeFunc("func2"),
                    buildInstructions(
                        assembler.invokeFunc("func1"),
                        assembler.invokeFunc("func1"),
                        assembler.invokeFunc("func1"),
                        assembler.invokeFunc("func1"),
                        assembler.invokeFunc("func1"),
                        assembler.invokeFunc("func1")
                    )
                )
            )
        );
        
        assembler.add(
            assembler.declareFunc(
                "func1",
                buildInstructions(
                    buildInstruction(PUSH_I8),
                    buildInstruction((byte) 42),
                    buildInstruction(PRINT_I8),
                    buildInstruction(PUSH_I8),
                    buildInstruction((byte) 33)
                )
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
        
        // PUSH %POS% dovrebbe avere un -1 STACK
        
        // TODO il push dei parametri dovrebbe avere -(PARAM SIZE) STACK

        // TODO l'inizio del body della funzione dovrebbe avere un +(PARAM SIZE) STACK
        
        // TODO l'inizio della funzione dovrebbe essere il stackMaster di tutti i parametri

        // TODO I parametri vanno chiamati con .param(index) -> uguale a .variable
        // ma hanno un incrementOption

        var bytecode = assembler.compile(); // assemble

        System.out.println(assembler);
        //print(bytecode);

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
