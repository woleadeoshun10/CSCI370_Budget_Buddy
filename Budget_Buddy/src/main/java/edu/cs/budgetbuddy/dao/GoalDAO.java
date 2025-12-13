package edu.cs.budgetbuddy.dao;

import edu.cs.budgetbuddy.model.Goal;
import edu.cs.budgetbuddy.util.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.*;


public class GoalDAO {

    public static Goal create(Goal goal) {
        String sql = "INSERT INTO goals (user_id, goal_name, target_amount, current_amount, " +
                     "deadline, future_self_message) VALUES (?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setInt(1, goal.getUserId());
            stmt.setString(2, goal.getGoalName());
            stmt.setBigDecimal(3, goal.getTargetAmount());
            stmt.setBigDecimal(4, goal.getCurrentAmount());
            stmt.setDate(5, goal.getDeadline());
            stmt.setString(6, goal.getFutureSelfMessage());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    goal.setGoalId(rs.getInt(1));
                    System.out.println("GoalDAO: Created goal ID " + goal.getGoalId() + 
                                       " for user " + goal.getUserId());
                    return goal;
                }
            }
            
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                System.err.println("GoalDAO: User " + goal.getUserId() + 
                                   " already has a goal. Use update() instead.");
            } else {
                System.err.println("GoalDAO.create() error: " + e.getMessage());
            }
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
        
        return null;
    }
    

    public static Goal createOrUpdate(Goal goal) {
        Goal existing = findByUserId(goal.getUserId());
        
        if (existing == null) {
            return create(goal);
        } else {
            goal.setGoalId(existing.getGoalId());
            if (update(goal)) {
                return goal;
            }
        }
        
        return null;
    }
    
    
    public static Goal findById(int goalId) {
        String sql = "SELECT * FROM goals WHERE goal_id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, goalId);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToGoal(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("GoalDAO.findById() error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
        
        return null;
    }
    
    
    public static Goal findByUserId(int userId) {
        String sql = "SELECT * FROM goals WHERE user_id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToGoal(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("GoalDAO.findByUserId() error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt, rs);
        }
        
        return null;
    }
    
    
    public static boolean hasGoal(int userId) {
        return findByUserId(userId) != null;
    }
    
    
    public static BigDecimal getProgressPercent(int userId) {
        Goal goal = findByUserId(userId);
        if (goal != null) {
            return BigDecimal.valueOf(goal.getProgressPercent());
        }
        return null;
    }
    
    
    public static boolean update(Goal goal) {
        String sql = "UPDATE goals SET goal_name = ?, target_amount = ?, current_amount = ?, " +
                     "deadline = ?, future_self_message = ?, is_completed = ? WHERE goal_id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setString(1, goal.getGoalName());
            stmt.setBigDecimal(2, goal.getTargetAmount());
            stmt.setBigDecimal(3, goal.getCurrentAmount());
            stmt.setDate(4, goal.getDeadline());
            stmt.setString(5, goal.getFutureSelfMessage());
            stmt.setBoolean(6, goal.isCompleted());
            stmt.setInt(7, goal.getGoalId());
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("GoalDAO: Updated goal ID " + goal.getGoalId());
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("GoalDAO.update() error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt);
        }
        
        return false;
    }
    
    public static boolean addProgress(int userId, BigDecimal amount) {
        String sql = "UPDATE goals SET current_amount = current_amount + ?, " +
                     "is_completed = (current_amount + ? >= target_amount) " +
                     "WHERE user_id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setBigDecimal(1, amount);
            stmt.setBigDecimal(2, amount);
            stmt.setInt(3, userId);
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("GoalDAO: Added $" + amount + " to goal for user " + userId);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("GoalDAO.addProgress() error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt);
        }
        
        return false;
    }
    

    public static boolean updateCurrentAmount(int goalId, BigDecimal newAmount) {
        String sql = "UPDATE goals SET current_amount = ?, " +
                     "is_completed = (? >= target_amount) WHERE goal_id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            
            stmt.setBigDecimal(1, newAmount);
            stmt.setBigDecimal(2, newAmount);
            stmt.setInt(3, goalId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("GoalDAO.updateCurrentAmount() error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt);
        }
        
        return false;
    }
    

    public static boolean markCompleted(int goalId) {
        String sql = "UPDATE goals SET is_completed = TRUE WHERE goal_id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, goalId);
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("GoalDAO: Marked goal " + goalId + " as completed!");
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("GoalDAO.markCompleted() error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt);
        }
        
        return false;
    }
    

    public static boolean delete(int goalId) {
        String sql = "DELETE FROM goals WHERE goal_id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, goalId);
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("GoalDAO: Deleted goal " + goalId);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("GoalDAO.delete() error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt);
        }
        
        return false;
    }

    
    public static boolean deleteForUser(int userId) {
        String sql = "DELETE FROM goals WHERE user_id = ?";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DatabaseUtil.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("GoalDAO.deleteForUser() error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(conn, stmt);
        }
        
        return false;
    }
    

    private static Goal mapResultSetToGoal(ResultSet rs) throws SQLException {
        return new Goal(
            rs.getInt("goal_id"),
            rs.getInt("user_id"),
            rs.getString("goal_name"),
            rs.getBigDecimal("target_amount"),
            rs.getBigDecimal("current_amount"),
            rs.getDate("deadline"),
            rs.getString("future_self_message"),
            rs.getBoolean("is_completed"),
            rs.getTimestamp("created_at"),
            rs.getTimestamp("updated_at")
        );
    }
}
