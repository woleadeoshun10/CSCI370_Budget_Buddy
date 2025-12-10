package edu.cs.budgetbuddy.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;


public class Transaction {

    private int transactionId;
    private int userId;
    private BigDecimal amount;
    private Category category;
    private String description;
    private boolean wasImpulse;
    private Date transactionDate;
    private Timestamp createdAt;
    
    public enum Category {
        FOOD("food", "Food & Dining"),
        ENTERTAINMENT("entertainment", "Entertainment"),
        SHOPPING("shopping", "Shopping"),
        TRANSPORT("transport", "Transportation"),
        BILLS("bills", "Bills & Utilities"),
        OTHER("other", "Other");
        
        private final String dbValue;
        private final String displayName;
        
        Category(String dbValue, String displayName) {
            this.dbValue = dbValue;
            this.displayName = displayName;
        }
        
        public String getDbValue() {
            return dbValue;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
      
         // Convert database string to enum
         
        public static Category fromDbValue(String value) {
            for (Category cat : values()) {
                if (cat.dbValue.equalsIgnoreCase(value)) {
                    return cat;
                }
            }
            return OTHER;
        }
        

        public static Category[] getAllCategories() {
            return values();
        }
    }
    

    public Transaction() {
        this.category = Category.OTHER;
        this.wasImpulse = false;
        this.transactionDate = new Date(System.currentTimeMillis());
    }
    
    
    public Transaction(int userId, BigDecimal amount, Category category, String description) {
        this();
        this.userId = userId;
        this.amount = amount;
        this.category = category;
        this.description = description;
    }
    
    
    public Transaction(int userId, BigDecimal amount, Category category, 
                       String description, boolean wasImpulse, Date transactionDate) {
        this.userId = userId;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.wasImpulse = wasImpulse;
        this.transactionDate = transactionDate;
    }
    

    public Transaction(int transactionId, int userId, BigDecimal amount, Category category,
                       String description, boolean wasImpulse, Date transactionDate, 
                       Timestamp createdAt) {
        this.transactionId = transactionId;
        this.userId = userId;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.wasImpulse = wasImpulse;
        this.transactionDate = transactionDate;
        this.createdAt = createdAt;
    }
    
    
    public BigDecimal calculateWorkHours(BigDecimal hourlyWage) {
        if (hourlyWage == null || hourlyWage.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        return amount.divide(hourlyWage, 2, RoundingMode.HALF_UP);
    }
    

    public String getFormattedAmount() {
        return String.format("$%.2f", amount);
    }
    

    public boolean isCurrentMonth() {
        if (transactionDate == null) {
            return false;
        }
        Date now = new Date(System.currentTimeMillis());
        
        // Compare year and month
        java.util.Calendar transCal = java.util.Calendar.getInstance();
        transCal.setTime(transactionDate);
        
        java.util.Calendar nowCal = java.util.Calendar.getInstance();
        nowCal.setTime(now);
        
        return transCal.get(java.util.Calendar.YEAR) == nowCal.get(java.util.Calendar.YEAR) &&
               transCal.get(java.util.Calendar.MONTH) == nowCal.get(java.util.Calendar.MONTH);
    }
    

    
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isWasImpulse() {
        return wasImpulse;
    }

    public void setWasImpulse(boolean wasImpulse) {
        this.wasImpulse = wasImpulse;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    
    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", userId=" + userId +
                ", amount=" + getFormattedAmount() +
                ", category=" + category.getDisplayName() +
                ", description='" + description + '\'' +
                ", wasImpulse=" + wasImpulse +
                ", transactionDate=" + transactionDate +
                '}';
    }
}
