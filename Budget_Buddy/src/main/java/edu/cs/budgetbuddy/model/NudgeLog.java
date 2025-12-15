package edu.cs.budgetbuddy.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class NudgeLog {

    // Primary Key
    private int logId;
    
    // Foreign Key
    private int userId;
    
    // Nudge Details
    private BigDecimal amount;
    private BigDecimal workHours;
    private String category;
    
    // User Decision
    private Decision decision;

    private int streakAtDecision;
    private BigDecimal goalProgressAtDecision; 
    private String knowledgeLevelAtDecision;

    private String nudgeMessage;

    private Timestamp createdAt;

    public enum Decision {
        SKIP("skip"),
        BUY("buy");
        
        private final String dbValue;
        
        Decision(String dbValue) {
            this.dbValue = dbValue;
        }
        
        public String getDbValue() {
            return dbValue;
        }
        
        public static Decision fromDbValue(String value) {
            if ("skip".equalsIgnoreCase(value)) {
                return SKIP;
            }
            return BUY;
        }
    }

    public NudgeLog() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public NudgeLog(int userId, BigDecimal amount, BigDecimal workHours, 
                    String category, Decision decision) {
        this();
        this.userId = userId;
        this.amount = amount;
        this.workHours = workHours;
        this.category = category;
        this.decision = decision;
    }

    public NudgeLog(int userId, BigDecimal amount, BigDecimal workHours, String category,
                    Decision decision, int streakAtDecision, BigDecimal goalProgressAtDecision,
                    String knowledgeLevelAtDecision, String nudgeMessage) {
        this();
        this.userId = userId;
        this.amount = amount;
        this.workHours = workHours;
        this.category = category;
        this.decision = decision;
        this.streakAtDecision = streakAtDecision;
        this.goalProgressAtDecision = goalProgressAtDecision;
        this.knowledgeLevelAtDecision = knowledgeLevelAtDecision;
        this.nudgeMessage = nudgeMessage;
    }

    public NudgeLog(int logId, int userId, BigDecimal amount, BigDecimal workHours,
                    String category, Decision decision, int streakAtDecision,
                    BigDecimal goalProgressAtDecision, String knowledgeLevelAtDecision,
                    String nudgeMessage, Timestamp createdAt) {
        this.logId = logId;
        this.userId = userId;
        this.amount = amount;
        this.workHours = workHours;
        this.category = category;
        this.decision = decision;
        this.streakAtDecision = streakAtDecision;
        this.goalProgressAtDecision = goalProgressAtDecision;
        this.knowledgeLevelAtDecision = knowledgeLevelAtDecision;
        this.nudgeMessage = nudgeMessage;
        this.createdAt = createdAt;
    }

    public boolean wasSuccessfulIntervention() {
        return decision == Decision.SKIP;
    }

    public BigDecimal getImpactAmount() {
        if (decision == Decision.SKIP) {
            return amount;  // Positive = saved
        } else {
            return amount.negate();  // Negative = spent
        }
    }

    public String getFormattedWorkHours() {
        if (workHours == null) {
            return "0 minutes";
        }
        
        double hours = workHours.doubleValue();
        
        if (hours < 1) {
            int minutes = (int) (hours * 60);
            return minutes + " minute" + (minutes != 1 ? "s" : "");
        } else if (hours == 1) {
            return "1 hour";
        } else {
            return String.format("%.1f hours", hours);
        }
    }
    
    public String getDecisionSummary() {
        String action = (decision == Decision.SKIP) ? "Skipped" : "Bought";
        return String.format("%s $%.2f (%s of work)", 
                action, amount, getFormattedWorkHours());
    }

    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getWorkHours() {
        return workHours;
    }

    public void setWorkHours(BigDecimal workHours) {
        this.workHours = workHours;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Decision getDecision() {
        return decision;
    }

    public void setDecision(Decision decision) {
        this.decision = decision;
    }

    public int getStreakAtDecision() {
        return streakAtDecision;
    }

    public void setStreakAtDecision(int streakAtDecision) {
        this.streakAtDecision = streakAtDecision;
    }

    public BigDecimal getGoalProgressAtDecision() {
        return goalProgressAtDecision;
    }

    public void setGoalProgressAtDecision(BigDecimal goalProgressAtDecision) {
        this.goalProgressAtDecision = goalProgressAtDecision;
    }

    public String getKnowledgeLevelAtDecision() {
        return knowledgeLevelAtDecision;
    }

    public void setKnowledgeLevelAtDecision(String knowledgeLevelAtDecision) {
        this.knowledgeLevelAtDecision = knowledgeLevelAtDecision;
    }

    public String getNudgeMessage() {
        return nudgeMessage;
    }

    public void setNudgeMessage(String nudgeMessage) {
        this.nudgeMessage = nudgeMessage;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "NudgeLog{" +
                "logId=" + logId +
                ", userId=" + userId +
                ", decision=" + getDecisionSummary() +
                ", streakAtDecision=" + streakAtDecision +
                ", goalProgress=" + goalProgressAtDecision + "%" +
                ", createdAt=" + createdAt +
                '}';
    }
}
