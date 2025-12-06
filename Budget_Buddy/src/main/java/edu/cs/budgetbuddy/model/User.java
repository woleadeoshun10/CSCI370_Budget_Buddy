package edu.cs.budgetbuddy.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class User {

    private int userId;
    private String username;
    private String email;
    private String passwordHash;
    private BigDecimal hourlyWage;
    private BigDecimal monthlyBudget;
    private String knowledgeLevel;
    private Timestamp createdAt;
    private Timestamp lastLogin;
    
    // Default constructor
    public User() {
        this.hourlyWage = new BigDecimal("15.00");
        this.monthlyBudget = new BigDecimal("500.00");
        this.knowledgeLevel = "beginner";
    }

    public User(String username, String email, String passwordHash) {
        this();
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public User(int userId, String username, String email, String passwordHash,
                BigDecimal hourlyWage, BigDecimal monthlyBudget, String knowledgeLevel,
                Timestamp createdAt, Timestamp lastLogin) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.hourlyWage = hourlyWage;
        this.monthlyBudget = monthlyBudget;
        this.knowledgeLevel = knowledgeLevel;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
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

    public String getKnowledgeLevel() { return knowledgeLevel; }
    public void setKnowledgeLevel(String knowledgeLevel) { this.knowledgeLevel = knowledgeLevel; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getLastLogin() { return lastLogin; }
    public void setLastLogin(Timestamp lastLogin) { this.lastLogin = lastLogin; }

}