package ch.samt.blockchain.node;

import ch.samt.blockchain.node.database.*;

public class Main {
    public static void main(String[] args) throws Exception {
        DatabaseConnection db = new DatabaseConnection("node.db");
        db.execute("""
            CREATE TABLE IF NOT EXISTS test (
                id INT PRIMARY KEY,
                nome VARCHAR(25)
            );
        """);

        db.execute("INSERT INTO test (nome) VALUES ('ciao');");

        var result = db.query("SELECT * FROM test;");

        while (result.next()) {
            System.out.print(result.getInt(1));
            System.out.println(result.getString(2));
        }
    }
}
