<%--Adewole--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="edu.cs.budgetbuddy.model.User" %>
<%@ page import="edu.cs.budgetbuddy.model.Goal" %>
<%@ page import="java.math.BigDecimal" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Goal - Budget Buddy</title>
    <style>
        /* basic resets */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        /* light gray background for the whole page */
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #f5f7fa;
            min-height: 100vh;
        }
        
        /* purple gradient navbar at the top */
        .navbar {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            padding: 15px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .navbar h1 {
            color: white;
            font-size: 1.5em;
        }
        
        /* nav links that light up on hover */
        .navbar-links a {
            color: white;
            text-decoration: none;
            padding: 8px 16px;
            border-radius: 8px;
            margin-left: 10px;
        }
        
        .navbar-links a:hover {
            background: rgba(255,255,255,0.2);
        }
        
        /* center container for the goal card */
        .container {
            max-width: 600px;
            margin: 40px auto;
            padding: 0 20px;
        }
        
        /* main white card showing the goal */
        .goal-card {
            background: white;
            border-radius: 25px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.1);
            padding: 40px;
            text-align: center;
        }
        
        /* big emoji at the top */
        .goal-icon {
            font-size: 4em;
            margin-bottom: 20px;
        }
        
        .goal-name {
            font-size: 2em;
            color: #333;
            margin-bottom: 10px;
        }
        
        /* status badges (on track, behind, completed) */
        .goal-status {
            display: inline-block;
            padding: 8px 20px;
            border-radius: 20px;
            font-size: 0.9em;
            font-weight: 600;
            margin-bottom: 30px;
        }
        
        /* green badge for on track */
        .goal-status.on-track {
            background: #d4edda;
            color: #155724;
        }
        
        /* yellow badge for behind schedule */
        .goal-status.behind {
            background: #fff3cd;
            color: #856404;
        }
        
        /* blue badge for completed */
        .goal-status.completed {
            background: #cce5ff;
            color: #004085;
        }
        
        .progress-section {
            margin: 30px 0;
        }
        
        /* circular progress indicator */
        .progress-circle {
            position: relative;
            width: 200px;
            height: 200px;
            margin: 0 auto 20px;
        }
        
        /* rotate so it starts at the top */
        .progress-circle svg {
            transform: rotate(-90deg);
        }
        
        /* gray background circle */
        .progress-circle .bg {
            fill: none;
            stroke: #e1e1e1;
            stroke-width: 15;
        }
        
        /* purple gradient progress circle */
        .progress-circle .progress {
            fill: none;
            stroke: url(#gradient);
            stroke-width: 15;
            stroke-linecap: round;
            stroke-dasharray: 565.48;
            stroke-dashoffset: 565.48;
            transition: stroke-dashoffset 1s ease;
        }
        
        /* percentage text in the middle of the circle */
        .progress-text {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            text-align: center;
        }
        
        .progress-text .percent {
            font-size: 2.5em;
            font-weight: 700;
            color: #667eea;
        }
        
        .progress-text .label {
            color: #888;
            font-size: 0.9em;
        }
        
        /* three amount boxes in a row (saved, target, remaining) */
        .amounts {
            display: flex;
            justify-content: space-around;
            margin: 30px 0;
            padding: 25px;
            background: #f8f9fa;
            border-radius: 15px;
        }
        
        .amount-item {
            text-align: center;
        }
        
        .amount-item .value {
            font-size: 1.8em;
            font-weight: 700;
            color: #333;
        }
        
        /* green for current amount */
        .amount-item .value.current { color: #00b894; }
        /* red for remaining amount */
        .amount-item .value.remaining { color: #e17055; }
        
        .amount-item .label {
            color: #888;
            font-size: 0.85em;
            margin-top: 5px;
        }
        
        /* purple box showing days until deadline */
        .deadline-info {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            border-radius: 15px;
            margin: 25px 0;
        }
        
        .deadline-info .days {
            font-size: 2.5em;
            font-weight: 700;
        }
        
        .deadline-info .text {
            opacity: 0.9;
        }
        
        /* yellow motivation message box */
        .future-message {
            background: #fff3cd;
            border-radius: 15px;
            padding: 25px;
            margin: 25px 0;
            text-align: left;
        }
        
        .future-message h4 {
            color: #856404;
            margin-bottom: 10px;
        }
        
        .future-message p {
            color: #856404;
            font-style: italic;
            line-height: 1.6;
        }
        
        /* two buttons at the bottom */
        .action-buttons {
            display: flex;
            gap: 15px;
            margin-top: 30px;
        }
        
        .action-btn {
            flex: 1;
            padding: 15px 25px;
            border-radius: 12px;
            text-decoration: none;
            font-weight: 600;
            text-align: center;
            transition: transform 0.2s;
        }
        
        /* little bounce on hover */
        .action-btn:hover {
            transform: translateY(-2px);
        }
        
        /* purple gradient button */
        .action-btn.primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        /* gray outline button */
        .action-btn.secondary {
            background: #f8f9fa;
            color: #333;
            border: 2px solid #e1e1e1;
        }
        
        /* box to add progress manually */
        .add-progress {
            background: white;
            border-radius: 15px;
            padding: 25px;
            margin-top: 25px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
        }
        
        .add-progress h3 {
            margin-bottom: 15px;
            color: #333;
        }
        
        /* input and button side by side */
        .add-progress-form {
            display: flex;
            gap: 10px;
        }
        
        .add-progress-form input {
            flex: 1;
            padding: 12px 15px;
            border: 2px solid #e1e1e1;
            border-radius: 10px;
            font-size: 1em;
        }
        
        .add-progress-form button {
            padding: 12px 25px;
            background: #00b894;
            color: white;
            border: none;
            border-radius: 10px;
            font-weight: 600;
            cursor: pointer;
        }
        
        /* success message banner at the top */
        .message-banner {
            padding: 15px;
            border-radius: 10px;
            margin-bottom: 20px;
            text-align: center;
        }
        
        .message-banner.success {
            background: #d4edda;
            color: #155724;
        }
    </style>
</head>
<body>
    <%-- grab all the goal data from the servlet --%>
    <%
        User user = (User) request.getAttribute("user");
        Goal goal = (Goal) request.getAttribute("goal");
        double progressPercent = (Double) request.getAttribute("progressPercent");
        BigDecimal amountRemaining = (BigDecimal) request.getAttribute("amountRemaining");
        long daysRemaining = (Long) request.getAttribute("daysRemaining");
        boolean isOnTrack = (Boolean) request.getAttribute("isOnTrack");
        
        // math for the circular progress bar animation
        double circumference = 565.48;
        double offset = circumference - (progressPercent / 100.0 * circumference);
    %>

    <%-- top navigation bar --%>
    <nav class="navbar">
        <h1>💰 Budget Buddy</h1>
        <div class="navbar-links">
            <a href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
            <a href="${pageContext.request.contextPath}/transaction">Transactions</a>
            <a href="${pageContext.request.contextPath}/goal">Goal</a>
            <a href="${pageContext.request.contextPath}/calculator">Calculator</a>
            <a href="${pageContext.request.contextPath}/auth?action=logout">Logout</a>
        </div>
    </nav>
    
    <div class="container">
        <%-- show success message if user just added progress --%>
        <% if ("progress".equals(request.getParameter("message"))) { %>
            <div class="message-banner success">
                ✓ Added $<%= request.getParameter("amount") %> to your goal!
            </div>
        <% } %>
        
        <%-- show success message if user just updated goal --%>
        <% if ("updated".equals(request.getParameter("message"))) { %>
            <div class="message-banner success">
                ✓ Goal updated successfully!
            </div>
        <% } %>
        
        <%-- main goal display card --%>
        <div class="goal-card">
            <div class="goal-icon">🎯</div>
            <h1 class="goal-name"><%= goal.getGoalName() %></h1>
            
            <%-- show different status badges based on goal state --%>
            <% if (goal.isCompleted()) { %>
                <span class="goal-status completed">🎉 Completed!</span>
            <% } else if (isOnTrack) { %>
                <span class="goal-status on-track">✓ On Track</span>
            <% } else { %>
                <span class="goal-status behind">⚠ Behind Schedule</span>
            <% } %>
            
            <%-- circular progress indicator --%>
            <div class="progress-section">
                <div class="progress-circle">
                    <svg width="200" height="200">
                        <%-- purple gradient definition --%>
                        <defs>
                            <linearGradient id="gradient" x1="0%" y1="0%" x2="100%" y2="0%">
                                <stop offset="0%" stop-color="#667eea" />
                                <stop offset="100%" stop-color="#764ba2" />
                            </linearGradient>
                        </defs>
                        <%-- gray background circle --%>
                        <circle class="bg" cx="100" cy="100" r="90"></circle>
                        <%-- animated progress circle --%>
                        <circle class="progress" cx="100" cy="100" r="90" 
                                style="stroke-dashoffset: <%= offset %>"></circle>
                    </svg>
                    <%-- percentage text in the middle --%>
                    <div class="progress-text">
                        <div class="percent"><%= String.format("%.0f", progressPercent) %>%</div>
                        <div class="label">Complete</div>
                    </div>
                </div>
            </div>
            
            <%-- three boxes showing saved, target, and remaining amounts --%>
            <div class="amounts">
                <div class="amount-item">
                    <div class="value current"><%= goal.getFormattedCurrentAmount() %></div>
                    <div class="label">Saved</div>
                </div>
                <div class="amount-item">
                    <div class="value"><%= goal.getFormattedTargetAmount() %></div>
                    <div class="label">Target</div>
                </div>
                <div class="amount-item">
                    <div class="value remaining">$<%= String.format("%.2f", amountRemaining) %></div>
                    <div class="label">Remaining</div>
                </div>
            </div>
            
            <%-- countdown box (only show if goal has deadline and isn't complete) --%>
            <% if (goal.getDeadline() != null && !goal.isCompleted()) { %>
                <div class="deadline-info">
                    <div class="days"><%= daysRemaining %></div>
                    <div class="text">days until <%= goal.getDeadline() %></div>
                </div>
            <% } %>
            
            <%-- user's motivational message (if they wrote one) --%>
            <% if (goal.getFutureSelfMessage() != null && !goal.getFutureSelfMessage().isEmpty()) { %>
                <div class="future-message">
                    <h4>💪 Your Motivation</h4>
                    <p>"<%= goal.getFutureSelfMessage() %>"</p>
                </div>
            <% } %>
            
            <%-- action buttons at the bottom --%>
            <div class="action-buttons">
                <a href="${pageContext.request.contextPath}/calculator" class="action-btn primary">
                    🧮 Use Calculator
                </a>
                <a href="${pageContext.request.contextPath}/goal?action=edit" class="action-btn secondary">
                    ✏️ Edit Goal
                </a>
            </div>
        </div>
        
        <%-- manual progress form (only show if goal isn't complete) --%>
        <% if (!goal.isCompleted()) { %>
            <div class="add-progress">
                <h3>➕ Add Progress Manually</h3>
                <form action="${pageContext.request.contextPath}/goal" method="post" class="add-progress-form">
                    <input type="hidden" name="action" value="addProgress">
                    <input type="number" name="amount" step="0.01" min="0.01" placeholder="Amount saved" required>
                    <button type="submit">Add</button>
                </form>
            </div>
        <% } %>
    </div>
</body>
</html>
