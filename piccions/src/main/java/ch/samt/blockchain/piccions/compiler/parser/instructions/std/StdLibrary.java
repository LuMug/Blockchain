package ch.samt.blockchain.piccions.compiler.parser.instructions.std;

import ch.samt.blockchain.piccions.compiler.parser.instructions.FunctionDeclaration;

public class StdLibrary {

    public static FunctionDeclaration[] getStdFunctions() {
        return new FunctionDeclaration[] {
            Print.instance
        };
    }

}
