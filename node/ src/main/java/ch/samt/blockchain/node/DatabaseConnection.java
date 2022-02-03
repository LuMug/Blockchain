import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    /**
     * Creates a Connection object, it is used to connect to the database.
     */
    private Connection connection = null;

    /**
     * Creates a Statement object, it is used to send statements and queries.
     */
    private Statement statement = null;

    /**
     * Constructor of the class, it assigns the values of connection and statement.
     * 
     * @param database database to connect to
     * @param user     the user to use for the connection
     * @param password the user's password
     */
    public DatabaseConnection(String database) {
        System.out.println("[database] :: connecting");

        try {
            Class.forName("jdbc:sqlite:" + database);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + database);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            statement = connection.createStatement(
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("[database] :: connected");
    }

    public void execute(String sql) {
        try {
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet query(String query) {
        try {
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public PreparedStatement prepareStatement(String sql) {
        try {
            return connection.prepareStatement(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

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