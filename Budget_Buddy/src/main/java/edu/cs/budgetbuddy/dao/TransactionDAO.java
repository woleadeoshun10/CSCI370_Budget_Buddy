package edu.cs.budgetbuddy.dao;

import edu.cs.budgetbuddy.model.Transaction;
import edu.cs.budgetbuddy.model.Transaction.Category;
import edu.cs.budgetbuddy.util.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TransactionDAO {

    public static Transaction create(Transaction transaction) {
        String sql = "INSERT INTO transactions (user_id, amount, category, description, " +
                     "was_impulse, transaction_date) VALUES (?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, transaction.getUserId());
            stmt.setBigDecimal(2, transaction.getAmount());
            stmt.setString(3, transaction.getCategory().getDbValue());
            stmt.setString(4, transaction.getDescription());
            stmt.setBoolean(5, transaction.isWasImpulse());
            stmt.setDate(6, transaction.getTransactionDate());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    transaction.setTransactionId(rs.getInt(1));
                    System.out.println("TransactionDAO: Created transaction ID " + 
                                       transaction.getTransactionId());
                    return transaction;
                }
            }
            
        } catch (SQLException e) {
            System.err.println("TransactionDAO.create() error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
        
        return null;
    }
    

    public static Transaction findById(int transactionId) {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, transactionId);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToTransaction(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("TransactionDAO.findById() error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
        
        return null;
    }
    

    public static List<Transaction> findByUserId(int userId) {
        String sql = "SELECT * FROM transactions WHERE user_id = ? " +
                     "ORDER BY transaction_date DESC, created_at DESC";
        
        return executeTransactionListQuery(sql, userId);
    }
    

    public static List<Transaction> findByDateRange(int userId, Date startDate, Date endDate) {
        String sql = "SELECT * FROM transactions WHERE user_id = ? " +
                     "AND transaction_date BETWEEN ? AND ? " +
                     "ORDER BY transaction_date DESC";
        
        List<Transaction> transactions = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setDate(2, startDate);
            stmt.setDate(3, endDate);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("TransactionDAO.findByDateRange() error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
        
        return transactions;
    }
    

    public static List<Transaction> findCurrentMonth(int userId) {
        String sql = "SELECT * FROM transactions WHERE user_id = ? " +
                     "AND MONTH(transaction_date) = MONTH(CURDATE()) " +
                     "AND YEAR(transaction_date) = YEAR(CURDATE()) " +
                     "ORDER BY transaction_date DESC";
        
        return executeTransactionListQuery(sql, userId);
    }
    
    

    public static List<Transaction> findByCategory(int userId, Category category) {
        String sql = "SELECT * FROM transactions WHERE user_id = ? AND category = ? " +
                     "ORDER BY transaction_date DESC";
        
        List<Transaction> transactions = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setString(2, category.getDbValue());
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("TransactionDAO.findByCategory() error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
        
        return transactions;
    }
    

    public static List<Transaction> findRecent(int userId, int limit) {
        String sql = "SELECT * FROM transactions WHERE user_id = ? " +
                     "ORDER BY transaction_date DESC, created_at DESC LIMIT ?";
        
        List<Transaction> transactions = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("TransactionDAO.findRecent() error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
        
        return transactions;
    }
    

    public static BigDecimal getMonthlyTotal(int userId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) as total FROM transactions " +
                     "WHERE user_id = ? " +
                     "AND MONTH(transaction_date) = MONTH(CURDATE()) " +
                     "AND YEAR(transaction_date) = YEAR(CURDATE())";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBigDecimal("total");
            }
            
        } catch (SQLException e) {
            System.err.println("TransactionDAO.getMonthlyTotal() error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
        
        return BigDecimal.ZERO;
    }
    
    
    
    public static Map<Category, BigDecimal> getMonthlySpendingByCategory(int userId) {
        String sql = "SELECT category, SUM(amount) as total FROM transactions " +
                     "WHERE user_id = ? " +
                     "AND MONTH(transaction_date) = MONTH(CURDATE()) " +
                     "AND YEAR(transaction_date) = YEAR(CURDATE()) " +
                     "GROUP BY category";
        
        Map<Category, BigDecimal> spending = new HashMap<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Category cat = Category.fromDbValue(rs.getString("category"));
                BigDecimal total = rs.getBigDecimal("total");
                spending.put(cat, total);
            }
            
        } catch (SQLException e) {
            System.err.println("TransactionDAO.getMonthlySpendingByCategory() error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
        
        return spending;
    }
    

    
    public static BigDecimal getMonthlyCategoryTotal(int userId, Category category) {
        String sql = "SELECT COALESCE(SUM(amount), 0) as total FROM transactions " +
                     "WHERE user_id = ? AND category = ? " +
                     "AND MONTH(transaction_date) = MONTH(CURDATE()) " +
                     "AND YEAR(transaction_date) = YEAR(CURDATE())";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setString(2, category.getDbValue());
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getBigDecimal("total");
            }
            
        } catch (SQLException e) {
            System.err.println("TransactionDAO.getMonthlyCategoryTotal() error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
        
        return BigDecimal.ZERO;
    }
    
    

    public static int getMonthlyImpulseCount(int userId) {
        String sql = "SELECT COUNT(*) as count FROM transactions " +
                     "WHERE user_id = ? AND was_impulse = TRUE " +
                     "AND MONTH(transaction_date) = MONTH(CURDATE()) " +
                     "AND YEAR(transaction_date) = YEAR(CURDATE())";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
            
        } catch (SQLException e) {
            System.err.println("TransactionDAO.getMonthlyImpulseCount() error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
        
        return 0;
    }
    

    
    public static boolean update(Transaction transaction) {
        String sql = "UPDATE transactions SET amount = ?, category = ?, description = ?, " +
                     "was_impulse = ?, transaction_date = ? WHERE transaction_id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setBigDecimal(1, transaction.getAmount());
            stmt.setString(2, transaction.getCategory().getDbValue());
            stmt.setString(3, transaction.getDescription());
            stmt.setBoolean(4, transaction.isWasImpulse());
            stmt.setDate(5, transaction.getTransactionDate());
            stmt.setInt(6, transaction.getTransactionId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("TransactionDAO.update() error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt);
        }
        
        return false;
    }
    

    public static boolean delete(int transactionId) {
        String sql = "DELETE FROM transactions WHERE transaction_id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, transactionId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("TransactionDAO.delete() error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt);
        }
        
        return false;
    }
    

    public static int deleteAllForUser(int userId) {
        String sql = "DELETE FROM transactions WHERE user_id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            
            return stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("TransactionDAO.deleteAllForUser() error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt);
        }
        
        return 0;
    }
    

    private static List<Transaction> executeTransactionListQuery(String sql, int userId) {
        List<Transaction> transactions = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("TransactionDAO query error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
        
        return transactions;
    }
    

    
    private static Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        return new Transaction(
            rs.getInt("transaction_id"),
            rs.getInt("user_id"),
            rs.getBigDecimal("amount"),
            Category.fromDbValue(rs.getString("category")),
            rs.getString("description"),
            rs.getBoolean("was_impulse"),
            rs.getDate("transaction_date"),
            rs.getTimestamp("created_at")
        );
    }
}
