package ch.samt.blockchain.node;

import javax.xml.crypto.Data;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection db = new DatabaseConnection("node.db");
        db.execute("""
                            CREATE TABLE contacts (
                                contact_id INTEGER PRIMARY KEY,
                                first_name TEXT NOT NULL,
                                last_name TEXT NOT NULL,
                                email TEXT NOT NULL UNIQUE,
                                phone TEXT NOT NULL UNIQUE
                           );
                """);
    }
}
