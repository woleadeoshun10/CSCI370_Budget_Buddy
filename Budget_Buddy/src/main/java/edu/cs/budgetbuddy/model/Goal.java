package edu.cs.budgetbuddy.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class Goal {

    private int goalId;
    private int userId;   
    private String goalName;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private Date deadline;
    private String futureSelfMessage;
    private boolean isCompleted;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    

    public Goal() {
        this.currentAmount = BigDecimal.ZERO;
        this.isCompleted = false;
    }
    

    public Goal(int userId, String goalName, BigDecimal targetAmount, Date deadline) {
        this();
        this.userId = userId;
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.deadline = deadline;
    }
    
    
    public Goal(int userId, String goalName, BigDecimal targetAmount, 
                Date deadline, String futureSelfMessage) {
        this(userId, goalName, targetAmount, deadline);
        this.futureSelfMessage = futureSelfMessage;
    }
    
    
    public Goal(int goalId, int userId, String goalName, BigDecimal targetAmount,
                BigDecimal currentAmount, Date deadline, String futureSelfMessage,
                boolean isCompleted, Timestamp createdAt, Timestamp updatedAt) {
        this.goalId = goalId;
        this.userId = userId;
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.deadline = deadline;
        this.futureSelfMessage = futureSelfMessage;
        this.isCompleted = isCompleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    

    public double getProgressPercent() {
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return 0.0;
        }
        BigDecimal progress = currentAmount.multiply(new BigDecimal("100"))
                .divide(targetAmount, 2, RoundingMode.HALF_UP);
        return Math.min(progress.doubleValue(), 100.0);
    }
    

    public BigDecimal getAmountRemaining() {
        BigDecimal remaining = targetAmount.subtract(currentAmount);
        return remaining.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : remaining;
    }
    

    public long getDaysUntilDeadline() {
        if (deadline == null) {
            return Long.MAX_VALUE;
        }
        LocalDate today = LocalDate.now();
        LocalDate deadlineDate = deadline.toLocalDate();
        return ChronoUnit.DAYS.between(today, deadlineDate);
    }
    

    public int calculateDelayDays(BigDecimal purchaseAmount) {
        long daysRemaining = getDaysUntilDeadline();
        if (daysRemaining <= 0) {
            return 0;
        }
        
        BigDecimal amountRemaining = getAmountRemaining();
        if (amountRemaining.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        

        BigDecimal dailySavingsNeeded = amountRemaining.divide(
                new BigDecimal(daysRemaining), 2, RoundingMode.HALF_UP);
        
        if (dailySavingsNeeded.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        
        BigDecimal delayDays = purchaseAmount.divide(dailySavingsNeeded, 0, RoundingMode.HALF_UP);
        return delayDays.intValue();
    }
    

    public void addProgress(BigDecimal amount) {
        this.currentAmount = this.currentAmount.add(amount);
        checkCompletion();
    }
    

    private void checkCompletion() {
        if (currentAmount.compareTo(targetAmount) >= 0) {
            this.isCompleted = true;
        }
    }
    

    public String getFormattedTargetAmount() {
        return String.format("$%.2f", targetAmount);
    }
    

    public String getFormattedCurrentAmount() {
        return String.format("$%.2f", currentAmount);
    }
    

    public String getStatusSummary() {
        return String.format("%s / %s (%.1f%%)", 
                getFormattedCurrentAmount(), 
                getFormattedTargetAmount(), 
                getProgressPercent());
    }
    

    public boolean isOnTrack() {
        if (deadline == null || isCompleted) {
            return true;
        }
        
        if (createdAt == null) {
            return true;
        }
        
        long daysSinceStart = ChronoUnit.DAYS.between(
                createdAt.toLocalDateTime().toLocalDate(), 
                LocalDate.now());
        
        if (daysSinceStart <= 0) {
            return true;
        }
        
        long totalDays = ChronoUnit.DAYS.between(
                createdAt.toLocalDateTime().toLocalDate(),
                deadline.toLocalDate());
        
        if (totalDays <= 0) {
            return false;
        }
        
        double expectedPercent = (daysSinceStart * 100.0) / totalDays;
        return getProgressPercent() >= expectedPercent;
    }
    
    public int getGoalId() {
        return goalId;
    }

    public void setGoalId(int goalId) {
        this.goalId = goalId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
        checkCompletion();
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getFutureSelfMessage() {
        return futureSelfMessage;
    }

    public void setFutureSelfMessage(String futureSelfMessage) {
        this.futureSelfMessage = futureSelfMessage;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    

    
    @Override
    public String toString() {
        return "Goal{" +
                "goalId=" + goalId +
                ", userId=" + userId +
                ", goalName='" + goalName + '\'' +
                ", progress=" + getStatusSummary() +
                ", deadline=" + deadline +
                ", daysRemaining=" + getDaysUntilDeadline() +
                ", isCompleted=" + isCompleted +
                ", isOnTrack=" + isOnTrack() +
                '}';
    }
}
