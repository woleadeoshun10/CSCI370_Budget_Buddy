package edu.cs.budgetbuddy.dao;

import edu.cs.budgetbuddy.model.User;
import edu.cs.budgetbuddy.util.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.*;

public class UserDAO {

    // Create a new user
    public static User create(User user) {
        String sql = "INSERT INTO users (username, email, password_hash, hourly_wage, monthly_budget, knowledge_level) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setBigDecimal(4, user.getHourlyWage());
            stmt.setBigDecimal(5, user.getMonthlyBudget());
            stmt.setString(6, user.getKnowledgeLevel());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    user.setUserId(rs.getInt(1));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
        return null;
    }

    public static User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
        return null;
    }


    public static User findById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
        return null;
    }

    public static boolean usernameExists(String username) {
        return findByUsername(username) != null;
    }

    public static boolean emailExists(String email) {
        String sql = "SELECT user_id FROM users WHERE email = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
        return false;
    }

    public static User authenticate(String username, String password) {
        User user = findByUsername(username);
        
        if (user != null) {
            String hashedInput = DatabaseUtil.hashPassword(password);
            if (hashedInput.equals(user.getPasswordHash())) {
                updateLastLogin(user.getUserId());
                return user;
            }
        }
        return null;
    }


    // Update last login timestamp
    public static boolean updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt);
        }
        return false;
    }

    // Records a Skip decision (Calculator)
    public static boolean recordSkip(int userId, BigDecimal amountSaved) {
        String sql = "UPDATE users SET " +
                "current_streak = current_streak + 1, " +
                "longest_streak = GREATEST(longest_streak, current_streak + 1), " +
                "skip_count = skip_count + 1, " +
                "total_saved = total_saved + ? " +
                "WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setBigDecimal(1, amountSaved);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt);
        }
        return false;
    }


     // Records a Buy decision (Calculator)
    public static boolean recordBuy(int userId) {
        String sql = "UPDATE users SET " +
                "current_streak = 0, " +
                "buy_count = buy_count + 1 " +
                "WHERE user_id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt);
        }
        return false;
    }

    // Helper method that converts a ResultSet row into a fully populated User object.
    private static User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getBigDecimal("hourly_wage"),
                rs.getBigDecimal("monthly_budget"),
                rs.getString("knowledge_level"),
                rs.getInt("current_streak"),
                rs.getInt("longest_streak"),
                rs.getBigDecimal("total_saved"),
                rs.getInt("skip_count"),
                rs.getInt("buy_count"),
                rs.getTimestamp("created_at"),
                rs.getTimestamp("last_login")
        );
    }

}
