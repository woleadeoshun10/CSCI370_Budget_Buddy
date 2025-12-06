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
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load MySQL driver", e);
        }
    }
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASSWORD);
    }
    
    public static void close(Connection conn, PreparedStatement stmt, ResultSet rs) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException e) { /* ignore */ }
        }
        if (stmt != null) {
            try { stmt.close(); } catch (SQLException e) { /* ignore */ }
        }
        if (conn != null) {
            try { conn.close(); } catch (SQLException e) { /* ignore */ }
        }
    }
    
    public static void close(Connection conn, PreparedStatement stmt) {
        close(conn, stmt, null);
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
