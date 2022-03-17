package ch.samt.blockchain.piccions.compiler;

import ch.samt.blockchain.piccions.compiler.assembler.Assembler;
import ch.samt.blockchain.piccions.compiler.parser.Parser;
import ch.samt.blockchain.piccions.compiler.parser.instructions.InstructionSet;
import ch.samt.blockchain.piccions.vm.VirtualMachine;

public class Compiler {
    
    public static Assembler compile(String code) throws SyntaxException {

        class CompilerProcessor {

            private InstructionSet root;

            public CompilerProcessor(InstructionSet root) {
                this.root = root;
            }

            public Assembler compile() {
                var assembler = new Assembler();
                assembler.add(root.getOpcodes(assembler));
                return assembler;
            }

        }

        return new CompilerProcessor(Parser.getAbtractSyntaxTree(code)).compile();
    }

    public static void main(String[] args) {
        try {
            String code = """
                func main() {
                    ciao();
                }
                func ciao() {
                    let a = 2;
                }

            """;

            String _code = """
                func ciao(type v, type2 v2) {
                    let v1 = 2+1-3;
                    let v2 = (1-2)*2;
                    let v3 = 2*2*4+2;
                    let v4 = 2+2/66;
                    let v5 = (3);
                    let v6 = (((((8*3)))));
                }
            """;

            var assembler = Compiler.compile(code);

            System.out.println(assembler);

            var bytecode = assembler.assemble();
            new VirtualMachine(bytecode).execute();
        } catch (SyntaxException e) {
            e.printStackTrace();
        }
    }

}
