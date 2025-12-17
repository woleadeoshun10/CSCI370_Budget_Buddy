<%--Adewole--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="edu.cs.budgetbuddy.model.User" %>
<%@ page import="edu.cs.budgetbuddy.model.Transaction" %>
<%@ page import="edu.cs.budgetbuddy.model.Transaction.Category" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.math.BigDecimal" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Transactions - Budget Buddy</title>
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
        
        .navbar-links {
            display: flex;
            gap: 20px;
        }
        
        /* nav links that light up on hover */
        .navbar-links a {
            color: white;
            text-decoration: none;
            padding: 8px 16px;
            border-radius: 8px;
            transition: background 0.3s;
        }
        
        .navbar-links a:hover {
            background: rgba(255,255,255,0.2);
        }
        
        /* main content container */
        .container {
            max-width: 1000px;
            margin: 0 auto;
            padding: 30px;
        }
        
        /* page title and add button row */
        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
        }
        
        .page-header h2 {
            color: #333;
            font-size: 1.8em;
        }
        
        /* purple gradient add button */
        .add-btn {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 12px 25px;
            border-radius: 10px;
            text-decoration: none;
            font-weight: 600;
            transition: transform 0.2s;
        }
        
        /* little bounce on hover */
        .add-btn:hover {
            transform: translateY(-2px);
        }
        
        /* grid of summary cards (spent, remaining, budget, impulse) */
        .summary-cards {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        
        /* individual summary card */
        .summary-card {
            background: white;
            border-radius: 15px;
            padding: 20px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
        }
        
        .summary-card h3 {
            color: #888;
            font-size: 0.85em;
            text-transform: uppercase;
            margin-bottom: 10px;
        }
        
        .summary-card .value {
            font-size: 1.8em;
            font-weight: 700;
            color: #333;
        }
        
        /* red for spent amount */
        .summary-card.spent .value { color: #d63031; }
        /* green for remaining amount */
        .summary-card.remaining .value { color: #00b894; }
        /* red when over budget */
        .summary-card.over .value { color: #d63031; }
        
        /* progress bar container (gray background) */
        .progress-bar {
            background: #e1e1e1;
            border-radius: 10px;
            height: 10px;
            margin-top: 10px;
            overflow: hidden;
        }
        
        /* colored progress fill */
        .progress-fill {
            height: 100%;
            border-radius: 10px;
            transition: width 0.5s;
        }
        
        /* green when under 80% budget */
        .progress-fill.success { background: #00b894; }
        /* yellow when 80-100% budget */
        .progress-fill.warning { background: #fdcb6e; }
        /* red when over budget */
        .progress-fill.danger { background: #d63031; }
        
        /* filter buttons (this month, all time) */
        .filter-bar {
            background: white;
            border-radius: 10px;
            padding: 15px 20px;
            margin-bottom: 20px;
            display: flex;
            gap: 10px;
        }
        
        .filter-btn {
            padding: 8px 20px;
            border: 2px solid #e1e1e1;
            border-radius: 8px;
            background: white;
            color: #666;
            cursor: pointer;
            transition: all 0.3s;
            text-decoration: none;
        }
        
        /* purple border for active filter */
        .filter-btn:hover, .filter-btn.active {
            border-color: #667eea;
            color: #667eea;
            background: #f0f3ff;
        }
        
        /* main white box holding all transactions */
        .transactions-list {
            background: white;
            border-radius: 15px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
            overflow: hidden;
        }
        
        /* single transaction row */
        .transaction-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 20px 25px;
            border-bottom: 1px solid #f0f0f0;
            transition: background 0.3s;
        }
        
        /* light gray on hover */
        .transaction-item:hover {
            background: #f8f9fa;
        }
        
        .transaction-item:last-child {
            border-bottom: none;
        }
        
        /* left side (icon + details) */
        .transaction-info {
            display: flex;
            align-items: center;
            gap: 15px;
        }
        
        /* colored square with category emoji */
        .category-icon {
            width: 45px;
            height: 45px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.3em;
        }
        
        /* different background colors for each category */
        .category-icon.food { background: #ffeaa7; }
        .category-icon.entertainment { background: #dfe6e9; }
        .category-icon.shopping { background: #fab1a0; }
        .category-icon.transport { background: #81ecec; }
        .category-icon.bills { background: #a29bfe; }
        .category-icon.other { background: #b2bec3; }
        
        /* transaction description and date */
        .transaction-details h4 {
            color: #333;
            font-size: 1em;
            margin-bottom: 5px;
        }
        
        .transaction-details .meta {
            color: #888;
            font-size: 0.85em;
        }
        
        /* right side (amount + impulse badge) */
        .transaction-amount {
            text-align: right;
        }
        
        .transaction-amount .amount {
            font-size: 1.2em;
            font-weight: 600;
            color: #d63031;
        }
        
        /* yellow impulse badge */
        .transaction-amount .impulse-badge {
            display: inline-block;
            background: #fff3cd;
            color: #856404;
            padding: 3px 8px;
            border-radius: 5px;
            font-size: 0.7em;
            margin-top: 5px;
        }
        
        /* trash can delete button */
        .delete-btn {
            background: none;
            border: none;
            color: #ddd;
            cursor: pointer;
            padding: 5px;
            margin-left: 15px;
            transition: color 0.3s;
        }
        
        /* red on hover */
        .delete-btn:hover {
            color: #d63031;
        }
        
        /* shown when no transactions exist */
        .empty-state {
            text-align: center;
            padding: 60px 30px;
            color: #888;
        }
        
        .empty-state .icon {
            font-size: 4em;
            margin-bottom: 20px;
        }
        
        .empty-state p {
            margin-bottom: 20px;
        }
        
        /* green success message banner */
        .message-banner {
            padding: 15px 20px;
            border-radius: 10px;
            margin-bottom: 20px;
            text-align: center;
        }
        
        .message-banner.success {
            background: #d4edda;
            color: #155724;
        }
        
        /* white box showing spending breakdown by category */
        .category-breakdown {
            background: white;
            border-radius: 15px;
            padding: 25px;
            margin-bottom: 25px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
        }
        
        .category-breakdown h3 {
            margin-bottom: 20px;
            color: #333;
        }
        
        /* single row in category breakdown */
        .category-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 10px 0;
            border-bottom: 1px solid #f0f0f0;
        }
        
        .category-row:last-child {
            border-bottom: none;
        }
    </style>
</head>
<body>
    <%-- grab all the transaction data from servlet --%>
    <%
        User user = (User) request.getAttribute("user");
        List<Transaction> transactions = (List<Transaction>) request.getAttribute("transactions");
        Map<Category, BigDecimal> spendingByCategory = (Map<Category, BigDecimal>) request.getAttribute("spendingByCategory");
        BigDecimal monthlyTotal = (BigDecimal) request.getAttribute("monthlyTotal");
        BigDecimal budget = (BigDecimal) request.getAttribute("budget");
        BigDecimal remaining = (BigDecimal) request.getAttribute("remaining");
        double budgetPercent = (Double) request.getAttribute("budgetPercent");
        int impulseCount = (Integer) request.getAttribute("impulseCount");
        String filter = (String) request.getAttribute("filter");
        
        // figure out progress bar color based on budget usage
        String progressClass = budgetPercent > 100 ? "danger" : budgetPercent > 80 ? "warning" : "success";
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
        <%-- page title and add button --%>
        <div class="page-header">
            <h2>💳 Transactions</h2>
            <a href="${pageContext.request.contextPath}/transaction?action=add" class="add-btn">+ Add Transaction</a>
        </div>
        
        <%-- success message when transaction is added --%>
        <% if ("added".equals(request.getParameter("message"))) { %>
            <div class="message-banner success">
                ✓ Transaction added successfully!
            </div>
        <% } %>
        
        <%-- success message when transaction is deleted --%>
        <% if ("deleted".equals(request.getParameter("message"))) { %>
            <div class="message-banner success">
                ✓ Transaction deleted.
            </div>
        <% } %>
        
        <%-- four summary cards showing key metrics --%>
        <div class="summary-cards">
            <div class="summary-card spent">
                <h3>Spent This Month</h3>
                <div class="value">$<%= String.format("%.2f", monthlyTotal) %></div>
                <%-- progress bar showing budget usage --%>
                <div class="progress-bar">
                    <div class="progress-fill <%= progressClass %>" style="width: <%= Math.min(100, budgetPercent) %>%"></div>
                </div>
            </div>
            
            <%-- remaining or over budget card (changes color based on status) --%>
            <div class="summary-card <%= remaining.compareTo(BigDecimal.ZERO) >= 0 ? "remaining" : "over" %>">
                <h3><%= remaining.compareTo(BigDecimal.ZERO) >= 0 ? "Remaining" : "Over Budget" %></h3>
                <div class="value">$<%= String.format("%.2f", remaining.abs()) %></div>
            </div>
            
            <div class="summary-card">
                <h3>Budget</h3>
                <div class="value">$<%= String.format("%.2f", budget) %></div>
            </div>
            
            <div class="summary-card">
                <h3>Impulse Purchases</h3>
                <div class="value"><%= impulseCount %></div>
            </div>
        </div>
        
        <%-- category breakdown box (only show if there's spending data) --%>
        <% if (spendingByCategory != null && !spendingByCategory.isEmpty()) { %>
            <div class="category-breakdown">
                <h3>Spending by Category</h3>
                <% for (Map.Entry<Category, BigDecimal> entry : spendingByCategory.entrySet()) { %>
                    <div class="category-row">
                        <span><%= entry.getKey().getDisplayName() %></span>
                        <strong>$<%= String.format("%.2f", entry.getValue()) %></strong>
                    </div>
                <% } %>
            </div>
        <% } %>
        
        <%-- filter buttons (this month vs all time) --%>
        <div class="filter-bar">
            <a href="${pageContext.request.contextPath}/transaction?filter=month" 
               class="filter-btn <%= "month".equals(filter) || filter == null ? "active" : "" %>">This Month</a>
            <a href="${pageContext.request.contextPath}/transaction?filter=all" 
               class="filter-btn <%= "all".equals(filter) ? "active" : "" %>">All Time</a>
        </div>
        
        <%-- list of all transactions --%>
        <div class="transactions-list">
            <% if (transactions != null && !transactions.isEmpty()) { %>
                <%-- loop through each transaction and display it --%>
                <% for (Transaction tx : transactions) { 
                    String iconClass = tx.getCategory().getDbValue();
                    // pick the right emoji for each category
                    String icon = "food".equals(iconClass) ? "🍔" : 
                                  "entertainment".equals(iconClass) ? "🎬" :
                                  "shopping".equals(iconClass) ? "🛍️" :
                                  "transport".equals(iconClass) ? "🚗" :
                                  "bills".equals(iconClass) ? "📄" : "📦";
                %>
                    <div class="transaction-item">
                        <%-- left side: category icon and details --%>
                        <div class="transaction-info">
                            <div class="category-icon <%= iconClass %>"><%= icon %></div>
                            <div class="transaction-details">
                                <h4><%= tx.getDescription() != null && !tx.getDescription().isEmpty() ? tx.getDescription() : tx.getCategory().getDisplayName() %></h4>
                                <div class="meta"><%= tx.getCategory().getDisplayName() %> • <%= tx.getTransactionDate() %></div>
                            </div>
                        </div>
                        <%-- right side: amount and impulse badge --%>
                        <div class="transaction-amount">
                            <div class="amount">-<%= tx.getFormattedAmount() %></div>
                            <% if (tx.isWasImpulse()) { %>
                                <div class="impulse-badge">Impulse</div>
                            <% } %>
                        </div>
                        <%-- delete button (trash can icon) --%>
                        <form action="${pageContext.request.contextPath}/transaction" method="post" style="display: inline;">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="transactionId" value="<%= tx.getTransactionId() %>">
                            <button type="submit" class="delete-btn" onclick="return confirm('Delete this transaction?')">🗑️</button>
                        </form>
                    </div>
                <% } %>
            <% } else { %>
                <%-- empty state when no transactions exist --%>
                <div class="empty-state">
                    <div class="icon">📭</div>
                    <p>No transactions recorded yet</p>
                    <a href="${pageContext.request.contextPath}/transaction?action=add" class="add-btn">Add Your First Transaction</a>
                </div>
            <% } %>
        </div>
    </div>
</body>
</html>
