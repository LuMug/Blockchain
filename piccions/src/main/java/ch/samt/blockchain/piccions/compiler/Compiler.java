package ch.samt.blockchain.piccions.compiler;

import ch.samt.blockchain.piccions.compiler.assembler.Assembler;
import ch.samt.blockchain.piccions.compiler.parser.Parser;
import ch.samt.blockchain.piccions.compiler.parser.instructions.InstructionSet;

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
            Compiler.compile("""
                func ciao(type v, type2 v2) {
                    let v1 = 2+1-3;
                    let v2 = (1-2)*2;
                    let v3 = 2*2*4+2;
                    let v4 = 2+2/66;
                    let v5 = (3);
                    let v5 = (((((8*3)))));
                    let v6 = v4+1;
                    let v7 = v4*(2+v3);
                }

                func altraFunzione() {

                }
            """);
        } catch (SyntaxException e) {
            e.printStackTrace();
        }
    }

}
