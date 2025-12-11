<%--Adewole: Friction Calculator page--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="edu.cs.budgetbuddy.model.User" %>
<%
    // Check if user is logged in
    User user = (User) request.getAttribute("user");
    if (user == null) {
        // Redirect to login if not logged in
        response.sendRedirect(request.getContextPath() + "/auth?action=login");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Budget Buddy - Friction Calculator</title>
    <style>
        /* Reset defaults */
        * { margin: 0; padding: 0; box-sizing: border-box; }
        
        /* Body with light background */
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background: #f5f7fa;
            min-height: 100vh;
        }
        
        /* Navbar styles */
        .navbar {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            padding: 15px 30px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .navbar h1 { color: white; font-size: 24px; }
        .navbar a { color: white; text-decoration: none; margin-left: 20px; }
        
        /* Main container */
        .container {
            max-width: 500px;
            margin: 50px auto;
            padding: 20px;
        }
        
        /* White card holding the form */
        .card {
            background: white;
            padding: 40px;
            border-radius: 16px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.1);
        }
        
        /* Title */
        h2 {
            text-align: center;
            color: #333;
            margin-bottom: 10px;
        }
        
        /* Subtitle text */
        .subtitle {
            text-align: center;
            color: #666;
            margin-bottom: 30px;
        }
        
        /* Form input spacing */
        .form-group {
            margin-bottom: 25px;
        }
        
        /* Labels above inputs */
        label {
            display: block;
            margin-bottom: 8px;
            color: #333;
            font-weight: 500;
        }
        
        /* Input and select styling */
        input[type="number"], select {
            width: 100%;
            padding: 15px;
            border: 2px solid #e1e1e1;
            border-radius: 10px;
            font-size: 18px;
            text-align: center;
        }
        
        /* Focus highlight */
        input:focus, select:focus {
            outline: none;
            border-color: #667eea;
        }
        
        /* Large amount input */
        .amount-input {
            font-size: 32px;
            font-weight: 700;
        }
        
        /* Submit button */
        button {
            width: 100%;
            padding: 16px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 10px;
            font-size: 18px;
            font-weight: 600;
            cursor: pointer;
        }
        
        /* Button hover effect */
        button:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
        }
        
        /* Error message box */
        .error {
            background: #fee;
            color: #c00;
            padding: 12px;
            border-radius: 8px;
            margin-bottom: 20px;
            text-align: center;
        }
        
        /* User info display */
        .user-info {
            text-align: center;
            margin-bottom: 20px;
            color: #666;
        }
    </style>
</head>
<body>
    <%-- Navigation bar --%>
    <nav class="navbar">
        <h1>💰 Budget Buddy</h1>
        <div>
            <a href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
            <a href="${pageContext.request.contextPath}/auth?action=logout">Logout</a>
        </div>
    </nav>
    
    <%-- Main container --%>
    <div class="container">
        <div class="card">
            <%-- Page title --%>
            <h2>🧮 Friction Calculator</h2>
            <p class="subtitle">How much are you thinking of spending?</p>
            
            <%-- Display user wage and streak --%>
            <div class="user-info">
                Your hourly wage: $<%= user.getHourlyWage() %> | 
                Current streak: <%= user.getCurrentStreak() %> days 🔥
            </div>
            
            <%-- Show error if exists --%>
            <% if (request.getAttribute("error") != null) { %>
                <div class="error"><%= request.getAttribute("error") %></div>
            <% } %>
            
            <%-- Calculator form --%>
            <form action="${pageContext.request.contextPath}/calculator" method="post">
                <%-- Purchase amount input --%>
                <div class="form-group">
                    <label for="amount">Purchase Amount ($)</label>
                    <input type="number" id="amount" name="amount" step="0.01" min="0.01" 
                           class="amount-input" placeholder="0.00" required>
                </div>
                
                <%-- Category dropdown --%>
                <div class="form-group">
                    <label for="category">Category</label>
                    <select id="category" name="category">
                        <option value="food">🍔 Food & Dining</option>
                        <option value="entertainment">🎬 Entertainment</option>
                        <option value="shopping">🛍️ Shopping</option>
                        <option value="transport">🚗 Transportation</option>
                        <option value="other" selected>📦 Other</option>
                    </select>
                </div>
                
                <%-- Submit button --%>
                <button type="submit">Calculate Work Hours</button>
            </form>
        </div>
    </div>
</body>
</html>
