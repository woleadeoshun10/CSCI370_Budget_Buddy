package edu.cs.budgetbuddy.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.cs.budgetbuddy.dao.GoalDAO;
import edu.cs.budgetbuddy.dao.NudgeLogDAO;
import edu.cs.budgetbuddy.dao.TransactionDAO;
import edu.cs.budgetbuddy.dao.UserDAO;
import edu.cs.budgetbuddy.model.Goal;
import edu.cs.budgetbuddy.model.NudgeLog;
import edu.cs.budgetbuddy.model.Transaction;
import edu.cs.budgetbuddy.model.Transaction.Category;
import edu.cs.budgetbuddy.model.User;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
 
    private static final long serialVersionUID = 1L;

    // Handle GET requests to display the dashboard using user data from database
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = getLoggedInUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }
        
        int userId = user.getUserId();

        user = UserDAO.findById(userId);
        request.getSession().setAttribute("user", user);

        //Set dashboard attributes
        int currentStreak = user.getCurrentStreak();
        int longestStreak = user.getLongestStreak();
        BigDecimal totalSaved = user.getTotalSaved();
        int skipCount = user.getSkipCount();
        int buyCount = user.getBuyCount();

        double skipRate = NudgeLogDAO.getSkipRate(userId);
        int totalNudges = NudgeLogDAO.getNudgeCount(userId);
        BigDecimal totalWorkHoursSaved = NudgeLogDAO.getTotalWorkHoursSaved(userId);

        Goal goal = GoalDAO.findByUserId(userId);
        double goalProgressPercent = 0;
        BigDecimal goalAmountRemaining = BigDecimal.ZERO;
        long daysUntilDeadline = 0;
        boolean isOnTrack = true;
        
        if (goal != null) {
            goalProgressPercent = goal.getProgressPercent();
            goalAmountRemaining = goal.getAmountRemaining();
            daysUntilDeadline = goal.getDaysUntilDeadline();
            isOnTrack = goal.isOnTrack();
        }

        BigDecimal monthlyTotal = TransactionDAO.getMonthlyTotal(userId);
        BigDecimal monthlyBudget = user.getMonthlyBudget();
        BigDecimal budgetRemaining = monthlyBudget.subtract(monthlyTotal);
        double budgetUsedPercent = 0;
        if (monthlyBudget.compareTo(BigDecimal.ZERO) > 0) {
            budgetUsedPercent = monthlyTotal.divide(monthlyBudget, 4, RoundingMode.HALF_UP)
                                           .multiply(new BigDecimal("100")).doubleValue();
        }

        Map<Category, BigDecimal> spendingByCategory = TransactionDAO.getMonthlySpendingByCategory(userId);

        List<Transaction> recentTransactions = TransactionDAO.findRecent(userId, 5);

        List<NudgeLog> recentNudges = NudgeLogDAO.findRecent(userId, 5);

        int impulseCount = TransactionDAO.getMonthlyImpulseCount(userId);

        // Determine skip rate status message and class
        String skipRateStatus;
        String skipRateClass;
        if (totalNudges == 0) {
            skipRateStatus = "No data yet - use the calculator!";
            skipRateClass = "neutral";
        } else if (skipRate >= 60) {
            skipRateStatus = "Excellent! You're beating impulses!";
            skipRateClass = "success";
        } else if (skipRate >= 40) {
            skipRateStatus = "Good progress - keep it up!";
            skipRateClass = "warning";
        } else {
            skipRateStatus = "Room for improvement";
            skipRateClass = "danger";
        }
        
        // Determine budget status message and class
        String budgetStatus;
        String budgetClass;
        if (budgetUsedPercent > 100) {
            budgetStatus = "Over budget!";
            budgetClass = "danger";
        } else if (budgetUsedPercent > 80) {
            budgetStatus = "Approaching limit";
            budgetClass = "warning";
        } else {
            budgetStatus = "On track";
            budgetClass = "success";
        }
        
        request.setAttribute("user", user);

        request.setAttribute("currentStreak", currentStreak);
        request.setAttribute("longestStreak", longestStreak);
        request.setAttribute("totalSaved", totalSaved);
        request.setAttribute("skipCount", skipCount);
        request.setAttribute("buyCount", buyCount);

        request.setAttribute("skipRate", skipRate);
        request.setAttribute("skipRateFormatted", String.format("%.1f", skipRate));
        request.setAttribute("totalNudges", totalNudges);
        request.setAttribute("skipRateStatus", skipRateStatus);
        request.setAttribute("skipRateClass", skipRateClass);
        request.setAttribute("totalWorkHoursSaved", totalWorkHoursSaved);

        request.setAttribute("goal", goal);
        request.setAttribute("goalProgressPercent", goalProgressPercent);
        request.setAttribute("goalAmountRemaining", goalAmountRemaining);
        request.setAttribute("daysUntilDeadline", daysUntilDeadline);
        request.setAttribute("isOnTrack", isOnTrack);

        request.setAttribute("monthlyTotal", monthlyTotal);
        request.setAttribute("monthlyBudget", monthlyBudget);
        request.setAttribute("budgetRemaining", budgetRemaining);
        request.setAttribute("budgetUsedPercent", budgetUsedPercent);
        request.setAttribute("budgetStatus", budgetStatus);
        request.setAttribute("budgetClass", budgetClass);

        request.setAttribute("spendingByCategory", spendingByCategory);

        request.setAttribute("recentTransactions", recentTransactions);
        request.setAttribute("recentNudges", recentNudges);

        request.setAttribute("impulseCount", impulseCount);

        if ("true".equals(request.getParameter("welcome"))) {
            request.setAttribute("welcomeMessage", true);
        }

        String message = request.getParameter("message");
        if (message != null) {
            request.setAttribute("message", message);
        }
        
        request.getRequestDispatcher("/jsp/dashboard.jsp").forward(request, response);
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
