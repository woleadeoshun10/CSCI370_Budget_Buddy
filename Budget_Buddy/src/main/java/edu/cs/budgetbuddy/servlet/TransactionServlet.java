package edu.cs.budgetbuddy.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.cs.budgetbuddy.dao.TransactionDAO;
import edu.cs.budgetbuddy.model.Transaction;
import edu.cs.budgetbuddy.model.Transaction.Category;
import edu.cs.budgetbuddy.model.User;

// Servlet to handle transaction-related actions: viewing history, adding, deleting
@WebServlet("/transaction")
public class TransactionServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // Handle GET requests to show transaction history or add form
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = getLoggedInUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("add".equals(action)) {
            showAddForm(request, response, user);
        } else {
            showTransactionHistory(request, response, user);
        }
    }
 
    // Handle POST requests to add or delete transactions
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Must be logged in
        User user = getLoggedInUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("add".equals(action)) {
            processAddTransaction(request, response, user);
        } else if ("delete".equals(action)) {
            processDeleteTransaction(request, response, user);
        } else {
            response.sendRedirect(request.getContextPath() + "/transaction");
        }
    }

    // Display the transaction history with optional filtering
    private void showTransactionHistory(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        
        int userId = user.getUserId();
        
        // Get filter parameter
        String filter = request.getParameter("filter");
        List<Transaction> transactions;
        
        if ("all".equals(filter)) {
            transactions = TransactionDAO.findByUserId(userId);
        } else {
            // Default to current month
            transactions = TransactionDAO.findCurrentMonth(userId);
        }
        
        // Get spending summary
        BigDecimal monthlyTotal = TransactionDAO.getMonthlyTotal(userId);
        Map<Category, BigDecimal> spendingByCategory = TransactionDAO.getMonthlySpendingByCategory(userId);
        int impulseCount = TransactionDAO.getMonthlyImpulseCount(userId);
        
        // Calculate budget status
        BigDecimal budget = user.getMonthlyBudget();
        BigDecimal remaining = budget.subtract(monthlyTotal);
        double budgetPercent = 0;
        if (budget.compareTo(BigDecimal.ZERO) > 0) {
            budgetPercent = monthlyTotal.divide(budget, 4, RoundingMode.HALF_UP)
                                        .multiply(new BigDecimal("100")).doubleValue();
        }
        
        // Set attributes
        request.setAttribute("user", user);
        request.setAttribute("transactions", transactions);
        request.setAttribute("monthlyTotal", monthlyTotal);
        request.setAttribute("spendingByCategory", spendingByCategory);
        request.setAttribute("impulseCount", impulseCount);
        request.setAttribute("budget", budget);
        request.setAttribute("remaining", remaining);
        request.setAttribute("budgetPercent", budgetPercent);
        request.setAttribute("filter", filter != null ? filter : "month");
        request.setAttribute("categories", Category.getAllCategories());
        
        request.getRequestDispatcher("/jsp/transactions.jsp").forward(request, response);
    }

    // Display the form to add a new transaction
    private void showAddForm(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        
        request.setAttribute("user", user);
        request.setAttribute("categories", Category.getAllCategories());
        
        // Set today's date as default
        request.setAttribute("today", new Date(System.currentTimeMillis()));
        
        request.getRequestDispatcher("/jsp/addTransaction.jsp").forward(request, response);
    }

    // Process adding a new transaction
    private void processAddTransaction(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        
        String amountStr = request.getParameter("amount");
        String categoryStr = request.getParameter("category");
        String description = request.getParameter("description");
        String dateStr = request.getParameter("transactionDate");
        String wasImpulseStr = request.getParameter("wasImpulse");
        
        // Validate amount
        if (amountStr == null || amountStr.trim().isEmpty()) {
            request.setAttribute("error", "Please enter an amount.");
            showAddForm(request, response, user);
            return;
        }
        
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr.trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NumberFormatException("Amount must be positive");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Please enter a valid positive amount.");
            showAddForm(request, response, user);
            return;
        }
        
        // Parse category
        Category category = Category.OTHER;
        if (categoryStr != null && !categoryStr.trim().isEmpty()) {
            category = Category.fromDbValue(categoryStr);
        }
        
        // Parse date
        Date transactionDate;
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            try {
                transactionDate = Date.valueOf(dateStr.trim());
            } catch (IllegalArgumentException e) {
                transactionDate = new Date(System.currentTimeMillis());
            }
        } else {
            transactionDate = new Date(System.currentTimeMillis());
        }
        
        // Parse impulse flag
        boolean wasImpulse = "true".equals(wasImpulseStr) || "on".equals(wasImpulseStr);
        
        // Create transaction
        Transaction transaction = new Transaction(
            user.getUserId(),
            amount,
            category,
            description != null ? description.trim() : "",
            wasImpulse,
            transactionDate
        );
        
        // Save to database
        Transaction created = TransactionDAO.create(transaction);
        
        if (created != null) {
            // Success - redirect to history with message
            response.sendRedirect(request.getContextPath() + 
                                  "/transaction?message=added&amount=" + amount);
        } else {
            request.setAttribute("error", "Failed to add transaction. Please try again.");
            showAddForm(request, response, user);
        }
    }

    // Process deleting a transaction
    private void processDeleteTransaction(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        
        String transactionIdStr = request.getParameter("transactionId");
        
        if (transactionIdStr == null || transactionIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/transaction?error=invalid");
            return;
        }
        
        try {
            int transactionId = Integer.parseInt(transactionIdStr.trim());
            
            // Verify the transaction belongs to this user
            Transaction transaction = TransactionDAO.findById(transactionId);
            if (transaction == null || transaction.getUserId() != user.getUserId()) {
                response.sendRedirect(request.getContextPath() + "/transaction?error=notfound");
                return;
            }
            
            // Delete it
            boolean success = TransactionDAO.delete(transactionId);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/transaction?message=deleted");
            } else {
                response.sendRedirect(request.getContextPath() + "/transaction?error=deletefailed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/transaction?error=invalid");
        }
    }

    // Get the currently logged-in user from the session
    private User getLoggedInUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (User) session.getAttribute("user");
        }
        return null;
    }
}
