<%--Adewole--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="edu.cs.budgetbuddy.model.User" %>
<%@ page import="edu.cs.budgetbuddy.model.Goal" %>
<%@ page import="java.sql.Date" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Set Goal - Budget Buddy</title>
    <style>
        /* basic resets - no margins/padding to mess things up */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        /* purple gradient background + center everything */
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }
        
        /* main white card that holds the goal form */
        .goal-container {
            background: white;
            border-radius: 25px;
            box-shadow: 0 25px 80px rgba(0, 0, 0, 0.3);
            padding: 50px;
            width: 100%;
            max-width: 500px;
        }
        
        /* header with target emoji and title */
        .header {
            text-align: center;
            margin-bottom: 35px;
        }
        
        /* big emoji at the top */
        .header .icon {
            font-size: 3.5em;
            margin-bottom: 15px;
        }
        
        .header h1 {
            color: #333;
            font-size: 1.8em;
            margin-bottom: 10px;
        }
        
        .header p {
            color: #666;
        }
        
        /* green welcome banner for new users - makes them feel special */
        .welcome-banner {
            background: linear-gradient(135deg, #00b894 0%, #00cec9 100%);
            color: white;
            padding: 20px;
            border-radius: 15px;
            margin-bottom: 30px;
            text-align: center;
        }
        
        .welcome-banner h3 {
            margin-bottom: 5px;
        }
        
        /* wrapper for each input field */
        .form-group {
            margin-bottom: 25px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 8px;
            color: #333;
            font-weight: 600;
        }
        
        /* all text inputs and textareas get the same styling */
        .form-group input, .form-group textarea {
            width: 100%;
            padding: 14px 18px;
            border: 2px solid #e1e1e1;
            border-radius: 12px;
            font-size: 1em;
            transition: border-color 0.3s;
        }
        
        /* purple border on focus - matches our theme */
        .form-group input:focus, .form-group textarea:focus {
            outline: none;
            border-color: #667eea;
        }
        
        /* textareas can be resized vertically */
        .form-group textarea {
            resize: vertical;
            min-height: 100px;
        }
        
        /* small helper text under inputs */
        .form-group small {
            display: block;
            margin-top: 6px;
            color: #888;
            font-size: 0.85em;
        }
        
        /* two inputs side by side (target amount + current amount) */
        .form-row {
            display: flex;
            gap: 15px;
        }
        
        .form-row .form-group {
            flex: 1;
        }
        
        /* bigger, centered text for amount inputs */
        .amount-input {
            font-size: 1.3em !important;
            text-align: center;
            font-weight: 600;
        }
        
        /* button container - holds multiple buttons in a row */
        .btn-group {
            display: flex;
            gap: 15px;
            margin-top: 30px;
        }
        
        /* base button styles */
        .btn {
            flex: 1;
            padding: 16px;
            border-radius: 12px;
            font-size: 1.1em;
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.2s;
            text-align: center;
            text-decoration: none;
            border: none;
        }
        
        /* little bounce effect on hover */
        .btn:hover {
            transform: translateY(-2px);
        }
        
        /* purple gradient button (main action) */
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        /* gray button (cancel/back) */
        .btn-secondary {
            background: #f8f9fa;
            color: #666;
            border: 2px solid #e1e1e1;
        }
        
        /* red button (delete) */
        .btn-danger {
            background: #fff;
            color: #d63031;
            border: 2px solid #d63031;
        }
        
        /* red error message box */
        .error-message {
            background: #ffe6e6;
            color: #d63031;
            padding: 15px;
            border-radius: 10px;
            margin-bottom: 25px;
            text-align: center;
        }
        
        /* gray box with example goals */
        .examples {
            background: #f8f9fa;
            border-radius: 12px;
            padding: 20px;
            margin-bottom: 25px;
        }
        
        .examples h4 {
            color: #333;
            margin-bottom: 12px;
            font-size: 0.95em;
        }
        
        /* example goal chips laid out horizontally */
        .example-goals {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
        }
        
        /* clickable goal chips - purple outline */
        .example-goal {
            background: white;
            padding: 8px 15px;
            border-radius: 20px;
            font-size: 0.85em;
            color: #667eea;
            border: 1px solid #667eea;
            cursor: pointer;
            transition: all 0.3s;
        }
        
        /* fills in purple on hover */
        .example-goal:hover {
            background: #667eea;
            color: white;
        }
        
        /* yellow motivation box (future self message) */
        .motivation-section {
            background: #fff3cd;
            border-radius: 12px;
            padding: 20px;
            margin-top: 20px;
        }
        
        .motivation-section h4 {
            color: #856404;
            margin-bottom: 10px;
        }
        
        .motivation-section p {
            font-size: 0.9em;
            color: #856404;
            margin-bottom: 15px;
        }
        
        /* skip link for new users who don't want to set a goal yet */
        .skip-link {
            display: block;
            text-align: center;
            margin-top: 20px;
            color: #888;
            text-decoration: none;
        }
        
        .skip-link:hover {
            color: #667eea;
        }
    </style>
</head>
<body>
    <%-- grabbing all the data passed from the servlet --%>
    <%
        User user = (User) request.getAttribute("user");
        Goal goal = (Goal) request.getAttribute("goal");
        boolean isEdit = request.getAttribute("isEdit") != null && (Boolean) request.getAttribute("isEdit");
        boolean isNewUser = request.getAttribute("isNewUser") != null && (Boolean) request.getAttribute("isNewUser");
        Date suggestedDeadline = (Date) request.getAttribute("suggestedDeadline");
    %>

    <%-- main goal form card --%>
    <div class="goal-container">
        <%-- header with emoji and title --%>
        <div class="header">
            <div class="icon">🎯</div>
            <h1><%= isEdit ? "Edit Your Goal" : "Set Your Goal" %></h1>
            <p>What are you saving for?</p>
        </div>
        
        <%-- show welcome banner only for brand new users --%>
        <% if (isNewUser) { %>
            <div class="welcome-banner">
                <h3>🎉 Welcome to Budget Buddy!</h3>
                <p>Let's set up your first savings goal</p>
            </div>
        <% } %>
        
        <%-- if there's an error, show it in red --%>
        <% if (request.getAttribute("error") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("error") %>
            </div>
        <% } %>
        
        <%-- quick-fill examples (only show when creating new goal, not editing) --%>
        <% if (!isEdit) { %>
            <div class="examples">
                <h4>💡 Goal Ideas</h4>
                <div class="example-goals">
                    <%-- clicking these fills in the form with preset values --%>
                    <span class="example-goal" onclick="setGoal('Spring Break Trip', 500)">🏖️ Spring Break - $500</span>
                    <span class="example-goal" onclick="setGoal('New Phone', 800)">📱 New Phone - $800</span>
                    <span class="example-goal" onclick="setGoal('Emergency Fund', 1000)">🏦 Emergency Fund - $1000</span>
                    <span class="example-goal" onclick="setGoal('Concert Tickets', 200)">🎵 Concert - $200</span>
                </div>
            </div>
        <% } %>
        
        <%-- actual goal form --%>
        <form action="${pageContext.request.contextPath}/goal" method="post">
            <input type="hidden" name="action" value="save">
            <%-- pass along welcome flag if new user --%>
            <% if (isNewUser) { %>
                <input type="hidden" name="welcome" value="true">
            <% } %>
            
            <%-- goal name input --%>
            <div class="form-group">
                <label for="goalName">What's your goal?</label>
                <input type="text" id="goalName" name="goalName" 
                       value="<%= goal != null ? goal.getGoalName() : "" %>"
                       placeholder="e.g., Spring Break Trip" required>
            </div>
            
            <%-- target amount and current amount side by side --%>
            <div class="form-row">
                <div class="form-group">
                    <label for="targetAmount">Target Amount</label>
                    <input type="number" id="targetAmount" name="targetAmount" 
                           class="amount-input"
                           value="<%= goal != null ? goal.getTargetAmount() : "" %>"
                           step="0.01" min="1" 
                           placeholder="$500" required>
                </div>
                
                <div class="form-group">
                    <label for="currentAmount">Already Saved</label>
                    <input type="number" id="currentAmount" name="currentAmount" 
                           class="amount-input"
                           value="<%= goal != null ? goal.getCurrentAmount() : "0" %>"
                           step="0.01" min="0" 
                           placeholder="$0">
                </div>
            </div>
            
            <%-- optional deadline date --%>
            <div class="form-group">
                <label for="deadline">Target Date (optional)</label>
                <input type="date" id="deadline" name="deadline" 
                       value="<%= goal != null && goal.getDeadline() != null ? goal.getDeadline() : (suggestedDeadline != null ? suggestedDeadline : "") %>">
                <small>When do you want to reach this goal?</small>
            </div>
            
            <%-- yellow motivation box --%>
            <div class="motivation-section">
                <h4>💪 Future Self Message</h4>
                <p>Write a message to remind yourself why this goal matters. You'll see this when you're tempted to spend!</p>
                <div class="form-group" style="margin-bottom: 0;">
                    <textarea id="futureSelfMessage" name="futureSelfMessage" 
                              placeholder="e.g., Future me deserves this vacation more than present me needs DoorDash!"><%= goal != null && goal.getFutureSelfMessage() != null ? goal.getFutureSelfMessage() : "" %></textarea>
                </div>
            </div>
            
            <%-- buttons at the bottom --%>
            <div class="btn-group">
                <%-- show cancel button only when editing --%>
                <% if (isEdit) { %>
                    <a href="${pageContext.request.contextPath}/goal" class="btn btn-secondary">Cancel</a>
                <% } %>
                <button type="submit" class="btn btn-primary">
                    <%= isEdit ? "Update Goal" : "Set Goal" %> 🎯
                </button>
            </div>
        </form>
        
        <%-- delete button (only shows when editing existing goal) --%>
        <% if (isEdit) { %>
            <form action="${pageContext.request.contextPath}/goal" method="post" style="margin-top: 15px;">
                <input type="hidden" name="action" value="delete">
                <button type="submit" class="btn btn-danger" style="width: 100%;" 
                        onclick="return confirm('Are you sure you want to delete this goal?')">
                    Delete Goal
                </button>
            </form>
        <% } %>
        
        <%-- let new users skip goal setup if they want --%>
        <% if (isNewUser) { %>
            <a href="${pageContext.request.contextPath}/dashboard" class="skip-link">Skip for now →</a>
        <% } %>
    </div>
    
    <%-- JavaScript to fill in form when clicking example goals --%>
    <script>
        // fills in goal name and amount when user clicks an example
        function setGoal(name, amount) {
            document.getElementById('goalName').value = name;
            document.getElementById('targetAmount').value = amount;
        }
    </script>
</body>
</html>
