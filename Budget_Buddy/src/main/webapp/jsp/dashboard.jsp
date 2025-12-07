<!--Adewole-->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="edu.cs.budgetbuddy.model.User" %>

<%
    <!-- pulling the user object that controller passed -->
    <!-- if user not logged in, kick them out to login page -->
    User user = (User) request.getAttribute("user");
    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/auth?action=login");
        return; <!-- stop the page -->
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Budget Buddy - Dashboard</title>

    <style>
        /* still inline css here (moving to style.css later) */
        * { margin: 0; padding: 0; box-sizing: border-box; }

        /* dashboard background */
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background: #f5f7fa;
            min-height: 100vh;
        }

        /* navbar styles */
        .navbar {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            padding: 15px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .navbar h1 {
            color: white;
            font-size: 24px;
        }
        .navbar a {
            color: white;
            text-decoration: none;
            margin-left: 20px;
        }

        /* main layout wrapper */
        .container {
            max-width: 1000px;
            margin: 0 auto;
            padding: 30px;
        }

        /* welcome section */
        .welcome {
            background: white;
            padding: 30px;
            border-radius: 16px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            margin-bottom: 30px;
            text-align: center;
        }
        .welcome h2 {
            color: #333;
            margin-bottom: 10px;
        }
        .welcome p {
            color: #666;
        }

        /* stats grid */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }

        /* individual stat card */
        .stat-card {
            background: white;
            padding: 25px;
            border-radius: 12px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            text-align: center;
        }
        .stat-card .icon {
            font-size: 32px;
            margin-bottom: 10px;
        }
        .stat-card .value {
            font-size: 28px;
            font-weight: 700;
            color: #333;
        }
        .stat-card .label {
            color: #666;
            margin-top: 5px;
        }

        /* announcement box */
        .info-box {
            background: #e8f4f8;
            border-left: 4px solid #667eea;
            padding: 20px;
            border-radius: 8px;
        }
        .info-box h3 {
            color: #333;
            margin-bottom: 10px;
        }
        .info-box p {
            color: #666;
        }

        /* user profile section */
        .user-info {
            background: white;
            padding: 20px;
            border-radius: 12px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            margin-top: 20px;
        }
        .user-info h3 {
            margin-bottom: 15px;
            color: #333;
        }
        .user-info p {
            color: #666;
            margin-bottom: 8px;
        }
    </style>
</head>

<body>

    <!-- pulling in the shared navbar so we don’t rewrite it each page -->
    <%@ include file="navbar.jsp" %>

    <div class="container">

        <!-- top welcome box -->
        <div class="welcome">
            <h2>Welcome to Budget Buddy! 🎉</h2>
            <p>Sprint 1 Complete - You can now sign up and log in!</p>
        </div>

        <!-- stat cards section -->
        <div class="stats-grid">

            <!-- day streak card -->
            <div class="stat-card">
                <div class="icon">🔥</div>
                <div class="value">${currentStreak}</div>
                <div class="label">Day Streak</div>
            </div>

            <!-- total saved -->
            <div class="stat-card">
                <div class="icon">💵</div>
                <div class="value">$${totalSaved}</div>
                <div class="label">Total Saved</div>
            </div>

            <!-- skip rate -->
            <div class="stat-card">
                <div class="icon">📊</div>
                <div class="value">${skipRate}%</div>
                <div class="label">Skip Rate</div>
            </div>

            <!-- monthly spent -->
            <div class="stat-card">
                <div class="icon">📅</div>
                <div class="value">$${monthlySpent}</div>
                <div class="label">Spent This Month</div>
            </div>
        </div>

        <!-- small “coming soon” section -->
        <div class="info-box">
            <h3>🚧 Coming in Sprint 2</h3>
            <p>The Friction Calculator - Enter a purchase amount and see how many work hours it costs you!</p>
        </div>

        <!-- bottom profile area -->
        <div class="user-info">
            <h3>Your Profile</h3>
            <p><strong>Username:</strong> <%= user.getUsername() %></p>
            <p><strong>Email:</strong> <%= user.getEmail() %></p>
            <p><strong>Hourly Wage:</strong> $<%= user.getHourlyWage() %></p>
            <p><strong>Monthly Budget:</strong> $<%= user.getMonthlyBudget() %></p>
        </div>

    </div>
</body>
</html>