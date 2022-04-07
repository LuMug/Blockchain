package ch.samt.blockchain.forger;

import ch.samt.blockchain.forger.paramhandler.ParamHandler;

public class Main {
    
    public static void main(String[] args) {
        var params = new ParamHandler();
        params
            .addArg("priv", false, "path", ParamHandler.propertyOf("Descritpion", "Private key file"))
            .addFlag("gen", ParamHandler.propertyOf("Descritpion", "Generate private key"))
            .addArg("out", false, "path", ParamHandler.propertyOf("Descritpion", "Output file"))
            .addArg("priv", false, "path", ParamHandler.propertyOf("Descritpion", "Load private key"))
            .addArg("amount", false, "value", ParamHandler.propertyOf("Descritpion", "Amount to transfer"))
            .addArg("to", false, "address", ParamHandler.propertyOf("Descritpion", "Tx receiver"))
            .addFlag("help", ParamHandler.propertyOf("Descritpion", "Displays this message"))
            .addFlag("examples", ParamHandler.propertyOf("Descritpion", "Displays some examples"))
            .addArg("dump", false, "path", ParamHandler.propertyOf("Descritpion", "Displays this message"));

        try {
            params.parse(args);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return;
        }

        if (params.getFlag("help")) {
            System.out.println(params.help("java -jar forger.jar", "Work with transactions"));
            return;
        }

        if (params.getFlag("examples")) {
            printExamples();
            return;
        }

        if (params.getFlag("gen")) {
            assertAll(params, new String[]{"out"});
            Forger.gen(params.getArg("out"));
            return;
        }

        if (!params.isNull("dump")) {
            Forger.dump(params.getArg("dump"));
            return;
        }

        if (any(params, new String[]{"to", "amount"})) {
            assertAll(params, new String[]{"to", "amount", "out", "priv"});
            Forger.tx(
                params.getArg("priv"),
                params.getArg("to"),
                params.getArg("amount"),
                params.getArg("out")
            );
            return;
        }
    }

    private static boolean any(ParamHandler handler, String... args) {
        for (var arg : args) {
            if (!handler.isNull(arg)) {
                return true;
            }
        }

        return false;
    }

    private static void assertAll(ParamHandler handler, String... args) {
        var builder = new StringBuilder();
        
        for (var arg : args) {
            if (handler.isNull(arg)) {
                builder.append(arg + ", ");
            }
        }

        if (builder.isEmpty()) {
            return;
        }

        var str = builder.toString();
        System.err.println("Missing arguments: {" + str.substring(0, str.length() - 2) + "}");

        System.exit(0);
    }

    private static void printExamples() {
        System.out.println();
        System.out.println("Generate wallet");
        System.out.println("\tjava -jar forger.jar -gen -out ./key.priv");
        System.out.println();
        System.out.println("Create transaction");
        System.out.println("\tjava -jar forger.jar -priv ./key.priv -amount 10000 -out transaction.tx -to J2VY3W0oEZCBxBKAu4ufsOc4/Qjl5Kiu2ottLxQgrK4=");
        System.out.println();
        System.out.println("Dump transaction file content");
        System.out.println("\tjava -jar forger.jar -dump ./transaction.tx");
        System.out.println();
    }

}