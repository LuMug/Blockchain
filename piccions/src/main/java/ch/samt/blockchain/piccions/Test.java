package ch.samt.blockchain.piccions;

import ch.samt.blockchain.piccions.vm.VirtualMachine;

import ch.samt.blockchain.piccions.compiler.CompileException;
import ch.samt.blockchain.piccions.compiler.Compiler;
import ch.samt.blockchain.piccions.compiler.SyntaxException;


public class Test {
    
    public static void main(String[] args) throws SyntaxException, CompileException {

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

        /*var assembler = new Assembler();
        assembler.add(
            assembler.mainFunc(
                Assembler.buildInstructions(
                    Assembler.buildInstruction(ByteCode.PUSH_I8),
                    assembler.variable("a", (byte) 5),

                    Assembler.buildInstruction(ByteCode.LOAD),
                    assembler.variable("a"),
                    Assembler.buildInstruction(ByteCode.PUSH_I8),
                    Assembler.buildInstruction((byte) 1),
                    assembler.variable("b", Assembler.buildInstruction(ByteCode.ADD_I8)),

                    Assembler.buildInstruction(ByteCode.LOAD),
                    assembler.variable("b"),
                    Assembler.buildInstruction(ByteCode.PRINT_I8)
                )
            )
        );*/

        var bytecode = assembler.assemble();
        System.out.println(assembler);

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
