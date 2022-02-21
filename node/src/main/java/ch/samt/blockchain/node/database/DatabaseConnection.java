package ch.samt.blockchain.node.database;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public interface DatabaseConnection {

    boolean connect();

    void execute(String sql);

    ResultSet query(String query);

    PreparedStatement prepareStatement(String sql);

    Blob createBlob(byte[] data);

}
