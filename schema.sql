-- Budget Buddy Database Schema - Sprint 1
-- Foundation: User Authentication Only

CREATE DATABASE IF NOT EXISTS budget_buddy;
USE budget_buddy;

-- Users table for authentication
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    
    -- Authentication
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    
    -- Profile Settings (for future sprints)
    hourly_wage DECIMAL(10,2) NOT NULL DEFAULT 15.00,
    monthly_budget DECIMAL(10,2) NOT NULL DEFAULT 500.00,
    knowledge_level ENUM('beginner', 'intermediate', 'advanced') NOT NULL DEFAULT 'beginner',
    
    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    
    INDEX idx_username (username),
    INDEX idx_email (email)
);

-- Sample test user (password: test123)
INSERT INTO users (username, email, password_hash, hourly_wage, monthly_budget, knowledge_level) 
VALUES (
    'testuser',
    'test@example.com',
    'ecd71870d1963316a97e3ac3408c9835ad8cf0f3c1bc703527c30265534f75ae',
    22.50,
    400.00,
    'intermediate'
);
