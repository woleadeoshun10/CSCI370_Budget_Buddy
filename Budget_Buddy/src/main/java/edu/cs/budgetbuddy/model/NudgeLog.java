package edu.cs.budgetbuddy.model;

import java.math.BigDecimal;
import java.sql.Timestamp;


//Friction Calculator Element

public class NudgeLog {

    private int logId;
    private int userId;
    private BigDecimal amount;
    private BigDecimal workHours;
    private String category;
    private String decision;
    private int streakAtDecision;
    private String nudgeMessage;
    private Timestamp createdAt;
    
    public NudgeLog() {
        this.category = "other";
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }
    
    public NudgeLog(int userId, BigDecimal amount, BigDecimal workHours, String category, String decision) {
        this();
        this.userId = userId;
        this.amount = amount;
        this.workHours = workHours;
        this.category = category;
        this.decision = decision;
    }
    
    public NudgeLog(int logId, int userId, BigDecimal amount, BigDecimal workHours,
                    String category, String decision, int streakAtDecision,
                    String nudgeMessage, Timestamp createdAt) {
        this.logId = logId;
        this.userId = userId;
        this.amount = amount;
        this.workHours = workHours;
        this.category = category;
        this.decision = decision;
        this.streakAtDecision = streakAtDecision;
        this.nudgeMessage = nudgeMessage;
        this.createdAt = createdAt;
    }
    
    public boolean isSkip() {
        return "skip".equals(decision);
    }
    
    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public BigDecimal getWorkHours() { return workHours; }
    public void setWorkHours(BigDecimal workHours) { this.workHours = workHours; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
    
    public int getStreakAtDecision() { return streakAtDecision; }
    public void setStreakAtDecision(int streakAtDecision) { this.streakAtDecision = streakAtDecision; }
    
    public String getNudgeMessage() { return nudgeMessage; }
    public void setNudgeMessage(String nudgeMessage) { this.nudgeMessage = nudgeMessage; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
