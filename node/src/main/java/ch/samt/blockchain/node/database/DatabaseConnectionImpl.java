package ch.samt.blockchain.node.database;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnectionImpl implements DatabaseConnection {
    
    /**
     * Creates a Connection object, it is used to connect to the database.
     */
    private Connection connection = null;

    /**
     * Creates a Statement object, it is used to send statements and queries.
     */
    private Statement statement = null;

    /**
     * The database name
     */
    private String database;

    /**
     * Constructor of the class, it assigns the values of connection and statement.
     * 
     * @param database database to connect to
     */
    public DatabaseConnectionImpl(String database) {
        this.database = database;
    }

    @Override
    public boolean connect() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + database);
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        
        return true;
    }

    @Override
    public void execute(String sql) {
        try {
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ResultSet query(String query) {
        try {
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql) {
        try {
            return connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Blob createBlob(byte[] data) {
        try {
            Blob blob = connection.createBlob();
            blob.setBytes(1, data);
            return blob;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}