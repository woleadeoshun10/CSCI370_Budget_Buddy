package edu.cs.budgetbuddy.dao;

import edu.cs.budgetbuddy.model.NudgeLog;
import edu.cs.budgetbuddy.util.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class NudgeLogDAO {

    // Creating a log to track decision making
	
    public static NudgeLog create(NudgeLog log) {
        String sql = "INSERT INTO nudge_logs (user_id, amount, work_hours, category, decision, " +
                     "streak_at_decision, nudge_message) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, log.getUserId());
            stmt.setBigDecimal(2, log.getAmount());
            stmt.setBigDecimal(3, log.getWorkHours());
            stmt.setString(4, log.getCategory());
            stmt.setString(5, log.getDecision());
            stmt.setInt(6, log.getStreakAtDecision());
            stmt.setString(7, log.getNudgeMessage());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    log.setLogId(rs.getInt(1));
                    return log;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
        return null;
    }


     //Get skip rate for a user

    public static double getSkipRate(int userId) {
        String sql = "SELECT " +
                     "COUNT(CASE WHEN decision = 'skip' THEN 1 END) as skips, " +
                     "COUNT(*) as total " +
                     "FROM nudge_logs WHERE user_id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                int total = rs.getInt("total");
                if (total == 0) return 0.0;
                int skips = rs.getInt("skips");
                return (skips * 100.0) / total;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
        return 0.0;
    }


     //Get total saved for a user

    public static BigDecimal getTotalSaved(int userId) {
        String sql = "SELECT COALESCE(SUM(amount), 0) as total FROM nudge_logs " +
                     "WHERE user_id = ? AND decision = 'skip'";
        
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
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
        return BigDecimal.ZERO;
    }

     //Get recent nudge logs for a user
    
    public static List<NudgeLog> findRecent(int userId, int limit) {
        String sql = "SELECT * FROM nudge_logs WHERE user_id = ? ORDER BY created_at DESC LIMIT ?";
        List<NudgeLog> logs = new ArrayList<>();
        
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
                logs.add(mapResultSetToNudgeLog(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
        return logs;
    }

     //Get nudge count for a user
    
    public static int getNudgeCount(int userId) {
        String sql = "SELECT COUNT(*) as count FROM nudge_logs WHERE user_id = ?";
        
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
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
        return 0;
    }

     public static BigDecimal getTotalWorkHoursSaved(int userId) {
        String sql = "SELECT COALESCE(SUM(work_hours), 0) as total FROM nudge_logs " +
                     "WHERE user_id = ? AND decision = 'skip'";
        
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
            System.err.println("NudgeLogDAO.getTotalWorkHoursSaved() error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
        
        return BigDecimal.ZERO;
    }

    private static NudgeLog mapResultSetToNudgeLog(ResultSet rs) throws SQLException {
        return new NudgeLog(
            rs.getInt("log_id"),
            rs.getInt("user_id"),
            rs.getBigDecimal("amount"),
            rs.getBigDecimal("work_hours"),
            rs.getString("category"),
            rs.getString("decision"),
            rs.getInt("streak_at_decision"),
            rs.getString("nudge_message"),
            rs.getTimestamp("created_at")
        );
    }
}
