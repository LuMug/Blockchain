package ch.samt.blockchain.forger;

import ch.samt.blockchain.common.utils.paramhandler.ParamHandler;

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
            .addFlag("first", ParamHandler.propertyOf("Descritpion", "First transaction"))
            .addArg("dump", false, "path", ParamHandler.propertyOf("Descritpion", "Displays this message"))
            .addArg("last", false, "path", ParamHandler.propertyOf("Descritpion", "Last transaction"));

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
            params.assertAll(new String[]{"out"});
            Forger.gen(params.getArg("out"));
            return;
        }

        if (!params.isNull("dump")) {
            Forger.dump(params.getArg("dump"));
            return;
        }

        if (params.any(new String[]{"to", "amount"})) {
            params.assertAll(new String[]{"to", "amount", "out", "priv"});

            var priv = params.getArg("priv");
            var to = params.getArg("to");
            var amount = params.getArg("amount");
            var out = params.getArg("out");
            var last = params.getArg("last");

            if (params.getFlag("first")) {
                Forger.tx(priv, to, amount, out, true);
            } else if (params.isNull("last")) {
                Forger.tx(priv, to, amount, out);
            } else {
                Forger.tx(priv, to, amount, out, last);
            }

            return;
        }
    }

    private static void printExamples() {
        System.out.println();
        System.out.println("Generate wallet");
        System.out.println("\tjava -jar forger.jar -gen -out ./key.priv");
        System.out.println();
        System.out.println("Create transaction");
        System.out.println("\tjava -jar forger.jar -priv ./key.priv -amount 10000 -out transaction.tx -to J2VY3W0oEZCBxBKAu4ufsOc4/Qjl5Kiu2ottLxQgrK4=");
        System.out.println();
        System.out.println("Create transaction (no HTTP request for lastHash)");
        System.out.println("\tjava -jar forger.jar -priv ./key.priv -last ./last.tx -amount 10000 -out transaction.tx -to J2VY3W0oEZCBxBKAu4ufsOc4/Qjl5Kiu2ottLxQgrK4=");
        System.out.println("\tjava -jar forger.jar -priv ./key.priv -first -amount 10000 -out transaction.tx -to J2VY3W0oEZCBxBKAu4ufsOc4/Qjl5Kiu2ottLxQgrK4=");
        System.out.println();
        System.out.println("Dump transaction file content");
        System.out.println("\tjava -jar forger.jar -dump ./transaction.tx");
        System.out.println();
    }

}