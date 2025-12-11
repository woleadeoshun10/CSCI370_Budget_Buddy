<%--Adewole: page that shows you how many work hours that purchase costs--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="edu.cs.budgetbuddy.model.User" %>
<%@ page import="java.math.BigDecimal" %>
<%
    // grab all the calculated stuff from the servlet
    User user = (User) request.getAttribute("user");
    BigDecimal amount = (BigDecimal) request.getAttribute("amount");
    String formattedWorkHours = (String) request.getAttribute("formattedWorkHours");
    String nudgeMessage = (String) request.getAttribute("nudgeMessage");
    Integer currentStreak = (Integer) request.getAttribute("currentStreak");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Budget Buddy - Think About It</title>
    <style>
        /* basic resets */
        * { margin: 0; padding: 0; box-sizing: border-box; }
        
        /* light gray background */
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background: #f5f7fa;
            min-height: 100vh;
        }
        
        /* top navbar */
        .navbar {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            padding: 15px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .navbar h1 { color: white; font-size: 24px; }
        .navbar a { color: white; text-decoration: none; margin-left: 20px; }
        
        /* center everything */
        .container {
            max-width: 500px;
            margin: 50px auto;
            padding: 20px;
        }
        
        /* white box holding everything */
        .card {
            background: white;
            padding: 40px;
            border-radius: 16px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.1);
            text-align: center;
        }
        
        /* big purple display showing the conversion */
        .work-hours-display {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            border-radius: 12px;
            margin-bottom: 25px;
        }
        .work-hours-display .amount {
            font-size: 36px;
            font-weight: 700;
        }
        .work-hours-display .hours {
            font-size: 24px;
            margin-top: 10px;
            opacity: 0.9;
        }
        
        /* the message trying to convince you not to buy */
        .nudge-message {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 25px;
            text-align: left;
            white-space: pre-line;
            color: #333;
            line-height: 1.6;
        }
        
        /* yellow warning if you have a streak */
        .streak-warning {
            background: #fff3cd;
            border-left: 4px solid #ffc107;
            padding: 15px;
            margin-bottom: 20px;
            text-align: left;
            border-radius: 0 8px 8px 0;
        }
        
        /* wrapper for the two buttons */
        .buttons {
            display: flex;
            gap: 15px;
        }
        
        /* base button style */
        .btn {
            flex: 1;
            padding: 16px;
            border: none;
            border-radius: 10px;
            font-size: 18px;
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.2s;
        }
        .btn:hover {
            transform: translateY(-2px);
        }
        
        /* green skip button */
        .btn-skip {
            background: #28a745;
            color: white;
        }
        
        /* gray buy button */
        .btn-buy {
            background: #6c757d;
            color: white;
        }
    </style>
</head>
<body>
    <%-- top navbar --%>
    <nav class="navbar">
        <h1>💰 Budget Buddy</h1>
        <div>
            <a href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
            <a href="${pageContext.request.contextPath}/auth?action=logout">Logout</a>
        </div>
    </nav>
    
    <%-- main content --%>
    <div class="container">
        <div class="card">
            <%-- the big conversion display --%>
            <div class="work-hours-display">
                <div class="amount">$<%= amount %></div>
                <div class="hours">= <%= formattedWorkHours %> of work</div>
            </div>
            
            <%-- only show warning if user actually has a streak --%>
            <% if (currentStreak != null && currentStreak > 0) { %>
                <div class="streak-warning">
                    ⚠️ <strong>Warning:</strong> You have a <%= currentStreak %>-day streak! 
                    Buying now will reset it to 0.
                </div>
            <% } %>
            
            <%-- the nudge trying to talk you out of it --%>
            <div class="nudge-message"><%= nudgeMessage %></div>
            
            <%-- decision buttons --%>
            <form action="${pageContext.request.contextPath}/calculator" method="post">
                <input type="hidden" name="action" value="decide">
                <div class="buttons">
                    <%-- skip = good, keeps streak and saves money --%>
                    <button type="submit" name="decision" value="skip" class="btn btn-skip">
                        ✅ Skip It
                    </button>
                    <%-- buy = you lose streak but get the thing --%>
                    <button type="submit" name="decision" value="buy" class="btn btn-buy">
                        💸 Buy Anyway
                    </button>
                </div>
            </form>
        </div>
    </div>
</body>
</html>
