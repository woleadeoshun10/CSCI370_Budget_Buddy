<%--Adewole--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="edu.cs.budgetbuddy.model.User" %>
<%@ page import="edu.cs.budgetbuddy.model.Transaction.Category" %>
<%@ page import="java.sql.Date" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Transaction - Budget Buddy</title>
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
        
        /* center container for the form */
        .container {
            max-width: 500px;
            margin: 40px auto;
            padding: 0 20px;
        }
        
        /* main white card holding the form */
        .form-card {
            background: white;
            border-radius: 20px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.1);
            padding: 40px;
        }
        
        .form-card h2 {
            color: #333;
            margin-bottom: 30px;
            text-align: center;
            font-size: 1.5em;
        }
        
        /* wrapper for each form input */
        .form-group {
            margin-bottom: 25px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 8px;
            color: #333;
            font-weight: 600;
        }
        
        /* all inputs, selects, and textareas get the same styling */
        .form-group input, .form-group select, .form-group textarea {
            width: 100%;
            padding: 14px 18px;
            border: 2px solid #e1e1e1;
            border-radius: 12px;
            font-size: 1em;
            transition: border-color 0.3s;
        }
        
        /* purple border on focus */
        .form-group input:focus, .form-group select:focus, .form-group textarea:focus {
            outline: none;
            border-color: #667eea;
        }
        
        /* bigger, centered text for amount input */
        .amount-input {
            font-size: 1.5em !important;
            text-align: center;
            font-weight: 600;
        }
        
        /* yellow box for the impulse purchase checkbox */
        .checkbox-group {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 15px;
            background: #fff3cd;
            border-radius: 10px;
        }
        
        .checkbox-group input[type="checkbox"] {
            width: 20px;
            height: 20px;
        }
        
        .checkbox-group label {
            margin: 0;
            color: #856404;
        }
        
        /* button container at the bottom */
        .btn-group {
            display: flex;
            gap: 15px;
            margin-top: 30px;
        }
        
        /* base button styles */
        .btn {
            flex: 1;
            padding: 15px;
            border-radius: 12px;
            font-size: 1em;
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.2s;
            text-align: center;
            text-decoration: none;
        }
        
        /* little bounce on hover */
        .btn:hover {
            transform: translateY(-2px);
        }
        
        /* purple gradient button (main action) */
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
        }
        
        /* gray button (cancel) */
        .btn-secondary {
            background: #f8f9fa;
            color: #666;
            border: 2px solid #e1e1e1;
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
        
        /* blue tip box at the bottom */
        .tip {
            background: #e8f4fd;
            border-radius: 10px;
            padding: 15px;
            margin-top: 20px;
            font-size: 0.9em;
            color: #0c5460;
        }
        
        /* grid layout for category icons (3 columns) */
        .category-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 10px;
        }
        
        /* each category box (food, shopping, etc.) */
        .category-option {
            display: flex;
            flex-direction: column;
            align-items: center;
            padding: 15px 10px;
            border: 2px solid #e1e1e1;
            border-radius: 12px;
            cursor: pointer;
            transition: all 0.3s;
        }
        
        /* light purple on hover */
        .category-option:hover {
            border-color: #667eea;
            background: #f0f3ff;
        }
        
        /* hide the actual radio button */
        .category-option input {
            display: none;
        }
        
        /* purple text when selected */
        .category-option input:checked + .category-content {
            color: #667eea;
        }
        
        /* purple border and background when selected */
        .category-option input:checked ~ .category-option,
        .category-option:has(input:checked) {
            border-color: #667eea;
            background: #f0f3ff;
        }
        
        /* emoji icon for each category */
        .category-option .icon {
            font-size: 1.5em;
            margin-bottom: 5px;
        }
        
        /* category name under the emoji */
        .category-option .name {
            font-size: 0.8em;
            color: #666;
        }
    </style>
</head>
<body>
    <%-- grab user data and categories from servlet --%>
    <%
        User user = (User) request.getAttribute("user");
        Category[] categories = (Category[]) request.getAttribute("categories");
        Date today = (Date) request.getAttribute("today");
    %>

    <%-- top navigation bar --%>
    <nav class="navbar">
        <h1>💰 Budget Buddy</h1>
        <div class="navbar-links">
            <a href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
            <a href="${pageContext.request.contextPath}/transaction">Transactions</a>
            <a href="${pageContext.request.contextPath}/auth?action=logout">Logout</a>
        </div>
    </nav>
    
    <div class="container">
        <div class="form-card">
            <h2>➕ Add Transaction</h2>
            
            <%-- show error message if something went wrong --%>
            <% if (request.getAttribute("error") != null) { %>
                <div class="error-message">
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>
            
            <%-- actual transaction form --%>
            <form action="${pageContext.request.contextPath}/transaction" method="post">
                <input type="hidden" name="action" value="add">
                
                <%-- amount input (big and centered) --%>
                <div class="form-group">
                    <label for="amount">Amount</label>
                    <input type="number" id="amount" name="amount" 
                           class="amount-input"
                           step="0.01" min="0.01" 
                           placeholder="$0.00" required>
                </div>
                
                <%-- category selection grid with emojis --%>
                <div class="form-group">
                    <label>Category</label>
                    <div class="category-grid">
                        <label class="category-option">
                            <input type="radio" name="category" value="food" checked>
                            <div class="category-content">
                                <div class="icon">🍔</div>
                                <div class="name">Food</div>
                            </div>
                        </label>
                        <label class="category-option">
                            <input type="radio" name="category" value="entertainment">
                            <div class="category-content">
                                <div class="icon">🎬</div>
                                <div class="name">Entertainment</div>
                            </div>
                        </label>
                        <label class="category-option">
                            <input type="radio" name="category" value="shopping">
                            <div class="category-content">
                                <div class="icon">🛍️</div>
                                <div class="name">Shopping</div>
                            </div>
                        </label>
                        <label class="category-option">
                            <input type="radio" name="category" value="transport">
                            <div class="category-content">
                                <div class="icon">🚗</div>
                                <div class="name">Transport</div>
                            </div>
                        </label>
                        <label class="category-option">
                            <input type="radio" name="category" value="bills">
                            <div class="category-content">
                                <div class="icon">📄</div>
                                <div class="name">Bills</div>
                            </div>
                        </label>
                        <label class="category-option">
                            <input type="radio" name="category" value="other">
                            <div class="category-content">
                                <div class="icon">📦</div>
                                <div class="name">Other</div>
                            </div>
                        </label>
                    </div>
                </div>
                
                <%-- optional description field --%>
                <div class="form-group">
                    <label for="description">Description (optional)</label>
                    <input type="text" id="description" name="description" 
                           placeholder="e.g., Coffee at Starbucks">
                </div>
                
                <%-- date picker (defaults to today) --%>
                <div class="form-group">
                    <label for="transactionDate">Date</label>
                    <input type="date" id="transactionDate" name="transactionDate" 
                           value="<%= today %>">
                </div>
                
                <%-- impulse purchase checkbox in yellow box --%>
                <div class="form-group">
                    <div class="checkbox-group">
                        <input type="checkbox" id="wasImpulse" name="wasImpulse" value="true">
                        <label for="wasImpulse">This was an impulse purchase</label>
                    </div>
                </div>
                
                <%-- cancel and submit buttons --%>
                <div class="btn-group">
                    <a href="${pageContext.request.contextPath}/transaction" class="btn btn-secondary">Cancel</a>
                    <button type="submit" class="btn btn-primary">Add Transaction</button>
                </div>
            </form>
            
            <%-- helpful tip about the calculator --%>
            <div class="tip">
                <strong>💡 Tip:</strong> For impulse purchases, try using the Friction Calculator 
                first to think about it before buying!
            </div>
        </div>
    </div>
    
    <%-- javascript to highlight the selected category --%>
    <script>
        // update category styling when user clicks
        document.querySelectorAll('.category-option input').forEach(input => {
            input.addEventListener('change', function() {
                // reset all categories to default
                document.querySelectorAll('.category-option').forEach(opt => {
                    opt.style.borderColor = '#e1e1e1';
                    opt.style.background = 'white';
                });
                // highlight the selected one
                if (this.checked) {
                    this.closest('.category-option').style.borderColor = '#667eea';
                    this.closest('.category-option').style.background = '#f0f3ff';
                }
            });
        });
        
        // highlight food (default) on page load
        document.querySelector('.category-option input:checked').dispatchEvent(new Event('change'));
    </script>
</body>
</html>
