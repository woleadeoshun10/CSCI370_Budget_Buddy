-- Budget Buddy Database Schema

CREATE DATABASE IF NOT EXISTS budget_buddy;
USE budget_buddy;

-- TABLE 1: users
-- Stores user profiles, authentication, and settings

CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    
    -- Authentication
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    
    -- Profile Settings
    hourly_wage DECIMAL(10,2) NOT NULL DEFAULT 15.00,
    monthly_budget DECIMAL(10,2) NOT NULL DEFAULT 500.00,
    knowledge_level ENUM('beginner', 'intermediate', 'advanced') NOT NULL DEFAULT 'beginner',
    
    -- Gamification
    current_streak INT NOT NULL DEFAULT 0,
    longest_streak INT NOT NULL DEFAULT 0,
    total_saved DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    skip_count INT NOT NULL DEFAULT 0,
    buy_count INT NOT NULL DEFAULT 0,
    
    -- Commitment Contract
    commitment_message TEXT,
    future_self_message TEXT,
    
    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    
    -- Indexes for faster lookups
    INDEX idx_username (username),
    INDEX idx_email (email)
);


-- TABLE 2: transactions
-- Stores all user spending records

CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    
    -- Transaction Details
    amount DECIMAL(10,2) NOT NULL,
    category ENUM('food', 'entertainment', 'shopping', 'transport', 'bills', 'other') NOT NULL DEFAULT 'other',
    description VARCHAR(255),
    
    -- Decision Tracking (was this an impulse that went through?)
    was_impulse BOOLEAN DEFAULT FALSE,
    
    -- Timestamps
    transaction_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign Key
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    
    -- Indexes
    INDEX idx_user_id (user_id),
    INDEX idx_transaction_date (transaction_date),
    INDEX idx_category (category)
);


-- TABLE 3: goals
-- Stores user savings goals (one per user for MVP)

CREATE TABLE IF NOT EXISTS goals (
    goal_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,  -- UNIQUE ensures one goal per user
    
    -- Goal Details
    goal_name VARCHAR(100) NOT NULL,
    target_amount DECIMAL(10,2) NOT NULL,
    current_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    deadline DATE,
    
    -- Motivation
    future_self_message TEXT,
    
    -- Status
    is_completed BOOLEAN DEFAULT FALSE,
    
    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign Key
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    
    -- Indexes
    INDEX idx_user_id (user_id)
);


-- TABLE 4: nudge_logs
-- Tracks every friction calculator interaction
-- Critical for measuring effectiveness

CREATE TABLE IF NOT EXISTS nudge_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    
    -- Nudge Details
    amount DECIMAL(10,2) NOT NULL,
    work_hours DECIMAL(10,2) NOT NULL,
    category ENUM('food', 'entertainment', 'shopping', 'transport', 'bills', 'other') NOT NULL DEFAULT 'other',
    
    -- User Decision
    decision ENUM('skip', 'buy') NOT NULL,
    
    -- Context at Decision Time (for analysis)
    streak_at_decision INT NOT NULL DEFAULT 0,
    goal_progress_at_decision DECIMAL(5,2),  -- percentage 0-100
    knowledge_level_at_decision ENUM('beginner', 'intermediate', 'advanced'),
    
    -- Nudge Content Shown
    nudge_message TEXT,
    
    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign Key
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    
    -- Indexes
    INDEX idx_user_id (user_id),
    INDEX idx_decision (decision),
    INDEX idx_created_at (created_at)
);

-- SAMPLE DATA (for testing)
-- Sample User (password is 'test123' - hashed with SHA-256)

INSERT INTO users (
    username, 
    email, 
    password_hash, 
    hourly_wage, 
    monthly_budget, 
    knowledge_level,
    commitment_message,
    future_self_message
) VALUES (
    'testuser',
    'test@example.com',
    'ecd71870d1963316a97e3ac3408c9835ad8cf0f3c1bc703527c30265534f75ae',  -- SHA-256 of 'test123'
    22.50,
    400.00,
    'intermediate',
    'I will think twice before every impulse purchase.',
    'Future me deserves that Spring Break trip more than present me needs DoorDash.'
);

-- Sample Goal for test user
INSERT INTO goals (
    user_id,
    goal_name,
    target_amount,
    current_amount,
    deadline,
    future_self_message
) VALUES (
    1,
    'Spring Break Trip',
    500.00,
    127.50,
    '2026-03-15',
    'Imagine yourself on the beach - every dollar saved gets you closer!'
);

-- Sample Transactions
INSERT INTO transactions (user_id, amount, category, description, was_impulse, transaction_date) VALUES
(1, 12.99, 'food', 'Chipotle lunch', TRUE, CURDATE()),
(1, 45.00, 'bills', 'Phone bill', FALSE, DATE_SUB(CURDATE(), INTERVAL 3 DAY)),
(1, 23.50, 'entertainment', 'Movie tickets', FALSE, DATE_SUB(CURDATE(), INTERVAL 5 DAY)),
(1, 8.75, 'food', 'Coffee and pastry', TRUE, DATE_SUB(CURDATE(), INTERVAL 1 DAY));

-- Sample Nudge Logs
INSERT INTO nudge_logs (user_id, amount, work_hours, category, decision, streak_at_decision, goal_progress_at_decision, knowledge_level_at_decision, nudge_message) VALUES
(1, 35.00, 1.56, 'food', 'skip', 3, 25.50, 'intermediate', 'That''s 1.5 hours of work. Your Spring Break fund is at 25% - keep going!'),
(1, 12.99, 0.58, 'food', 'buy', 4, 25.50, 'intermediate', 'That''s about 35 minutes of work.'),
(1, 89.99, 4.00, 'shopping', 'skip', 0, 25.50, 'intermediate', 'That''s 4 hours of work! This would delay your goal by 5 days.');
