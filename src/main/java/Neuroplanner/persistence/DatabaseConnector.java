package Neuroplanner.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private static final String connectionUrl =
            "jdbc:sqlserver://neuroplanner-server.database.windows.net:1433;" +
                    "database=neuroplannerdb;" +
                    "user=seval@neuroplanner-server;" +
                    "password=Neuroplanner123!;" +
                    "encrypt=true;" +
                    "trustServerCertificate=false;" +
                    "hostNameInCertificate=*.database.windows.net;" +
                    "loginTimeout=30;";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQL Server JDBC Driver niet gevonden!", e);
        }
        return DriverManager.getConnection(connectionUrl);
    }



}
