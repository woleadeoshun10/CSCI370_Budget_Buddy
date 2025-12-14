package edu.cs.budgetbuddy.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;

public class User {

    private int userId;
    private String username;
    private String email;
    private String passwordHash;
    private BigDecimal hourlyWage;
    private BigDecimal monthlyBudget;
    private KnowledgeLevel knowledgeLevel;
    private int currentStreak;
    private int longestStreak;
    private BigDecimal totalSaved;
    private int skipCount;
    private int buyCount;
    private String commitmentMessage;
    private String futureSelfMessage;
    private Timestamp createdAt;
    private Timestamp lastLogin;

    public enum KnowledgeLevel {
        BEGINNER("beginner"),
        INTERMEDIATE("intermediate"),
        ADVANCED("advanced");

        private final String dbValue;

        KnowledgeLevel(String dbValue) {
            this.dbValue = dbValue;
        }

        public String getDbValue() {
            return dbValue;
        }

        public static KnowledgeLevel fromDbValue(String value) {
            for (KnowledgeLevel level : values()) {
                if (level.dbValue.equalsIgnoreCase(value)) {
                    return level;
                }
            }
            return BEGINNER; // Defaulted
        }
    }
    
    // Default constructor
    public User() {
        this.hourlyWage = new BigDecimal("15.00");
        this.monthlyBudget = new BigDecimal("500.00");
        this.knowledgeLevel = KnowledgeLevel.BEGINNER;
        this.currentStreak = 0;
        this.longestStreak = 0;
        this.totalSaved = BigDecimal.ZERO;
        this.skipCount = 0;
        this.buyCount = 0;
    }

    public User(String username, String email, String passwordHash) {
        this();
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public User(int userId, String username, String email, String passwordHash,
                BigDecimal hourlyWage, BigDecimal monthlyBudget, KnowledgeLevel knowledgeLevel,
                int currentStreak, int longestStreak, BigDecimal totalSaved,
                int skipCount, int buyCount, String commitmentMessage, String futureSelfMessage,
                Timestamp createdAt, Timestamp lastLogin) {

        this.userId = userId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.hourlyWage = hourlyWage;
        this.monthlyBudget = monthlyBudget;
        this.knowledgeLevel = knowledgeLevel;
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.totalSaved = totalSaved;
        this.skipCount = skipCount;
        this.buyCount = buyCount;
        this.commitmentMessage = commitmentMessage;
        this.futureSelfMessage = futureSelfMessage;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }

    public BigDecimal calculateWorkHours(BigDecimal amount) {
        if (hourlyWage == null || hourlyWage.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return amount.divide(hourlyWage, 2, RoundingMode.HALF_UP);
    }

    public double getSkipRate() {
        int total = skipCount + buyCount;
        if (total == 0) {
            return 0.0;
        }
        return (skipCount * 100.0) / total;
    }


    public void incrementStreak() {
        this.currentStreak++;
        if (this.currentStreak > this.longestStreak) {
            this.longestStreak = this.currentStreak;
        }
    }


    public void breakStreak() {
        this.currentStreak = 0;
    }


    public void recordSkip(BigDecimal amount) {
        this.skipCount++;
        this.totalSaved = this.totalSaved.add(amount);
        incrementStreak();
    }


    public void recordBuy() {
        this.buyCount++;
        breakStreak();
    }


    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public BigDecimal getHourlyWage() { return hourlyWage; }
    public void setHourlyWage(BigDecimal hourlyWage) { this.hourlyWage = hourlyWage; }
    
    public BigDecimal getMonthlyBudget() { return monthlyBudget; }
    public void setMonthlyBudget(BigDecimal monthlyBudget) { this.monthlyBudget = monthlyBudget; }

    public KnowledgeLevel getKnowledgeLevel() { return knowledgeLevel; }
    public void setKnowledgeLevel(KnowledgeLevel knowledgeLevel) { this.knowledgeLevel = knowledgeLevel; }
    
    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }
    
    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }
    
    public BigDecimal getTotalSaved() { return totalSaved; }
    public void setTotalSaved(BigDecimal totalSaved) { this.totalSaved = totalSaved; }
    
    public int getSkipCount() { return skipCount; }
    public void setSkipCount(int skipCount) { this.skipCount = skipCount; }
    
    public int getBuyCount() { return buyCount; }
    public void setBuyCount(int buyCount) { this.buyCount = buyCount; }

    public String getCommitmentMessage() { return commitmentMessage; }
    public void setCommitmentMessage(String commitmentMessage) { this.commitmentMessage = commitmentMessage; }

    public String getFutureSelfMessage() { return futureSelfMessage; }
    public void setFutureSelfMessage(String futureSelfMessage) { this.futureSelfMessage = futureSelfMessage; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getLastLogin() { return lastLogin; }
    public void setLastLogin(Timestamp lastLogin) { this.lastLogin = lastLogin; }

}