package edu.cs.budgetbuddy.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class DatabaseUtil {

    private static final String DB_HOST = "127.0.0.1";
    private static final String DB_PORT = "3306";
    private static final String DB_NAME = "budget_buddy";
    private static final String DB_USER = "db_user_test";
    private static final String DB_PASSWORD = "user123!";
    
    private static final String JDBC_URL = 
        "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME 
        + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    static {
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("MySQL JDBC Driver loaded successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: MySQL JDBC Driver not found!");
            e.printStackTrace();
            throw new RuntimeException("Failed to load MySQL driver", e);
        }
    }
    
    public static Connection getConnection() throws SQLException {
        System.out.println("DatabaseUtil: Attempting to connect to " + DB_NAME + "...");
        Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
        System.out.println("DatabaseUtil: Connection established successfully.");
        return conn;
    }
    
    public static void close(Connection conn, PreparedStatement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
                System.out.println("DatabaseUtil: ResultSet closed.");
            } catch (SQLException e) {
                System.err.println("Error closing ResultSet: " + e.getMessage());
            }
        }


        if (stmt != null) {
            try {
                stmt.close();
                System.out.println("DatabaseUtil: PreparedStatement closed.");
            } catch (SQLException e) {
                System.err.println("Error closing PreparedStatement: " + e.getMessage());
            }
        }


        if (conn != null) {
            try {
                conn.close();
                System.out.println("DatabaseUtil: Connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing Connection: " + e.getMessage());
            }
        }
    }
    
    public static void close(Connection conn, PreparedStatement stmt) {
        close(conn, stmt, null);
    }

    public static void close(Connection conn) {
        close(conn, null, null);
    }
    
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
    
    public static boolean verifyPassword(String password, String storedHash) {
        String hashedInput = hashPassword(password);
        return hashedInput.equals(storedHash);
    }
}
