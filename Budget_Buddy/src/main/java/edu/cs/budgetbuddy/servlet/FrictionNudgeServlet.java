package edu.cs.budgetbuddy.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;

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
import edu.cs.budgetbuddy.model.NudgeLog.Decision;
import edu.cs.budgetbuddy.model.Transaction;
import edu.cs.budgetbuddy.model.Transaction.Category;
import edu.cs.budgetbuddy.model.User;
import edu.cs.budgetbuddy.model.User.KnowledgeLevel;

// Handle GET and POST requests for the friction nudge calculator
@WebServlet("/calculator")
public class FrictionNudgeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // Handle GET requests to display the calculator form
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = getLoggedInUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }

        request.setAttribute("user", user);
        request.setAttribute("categories", Category.getAllCategories());
        request.getRequestDispatcher("/jsp/calculator.jsp").forward(request, response);
    }

    // Handle POST requests to process the calculator input
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = getLoggedInUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("decide".equals(action)) {
            processDecision(request, response, user);
        } else {
            calculateAndShowNudge(request, response, user);
        }
    }

    //  Calculate nudge details and show the result page
    private void calculateAndShowNudge(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        
        String amountStr = request.getParameter("amount");
        String categoryStr = request.getParameter("category");
        String description = request.getParameter("description");

        if (amountStr == null || amountStr.trim().isEmpty()) {
            request.setAttribute("error", "Please enter an amount.");
            request.setAttribute("user", user);
            request.setAttribute("categories", Category.getAllCategories());
            request.getRequestDispatcher("/jsp/calculator.jsp").forward(request, response);
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
            request.setAttribute("user", user);
            request.setAttribute("categories", Category.getAllCategories());
            request.getRequestDispatcher("/jsp/calculator.jsp").forward(request, response);
            return;
        }
        
        Category category = Category.OTHER;
        if (categoryStr != null && !categoryStr.trim().isEmpty()) {
            category = Category.fromDbValue(categoryStr);
        }

        // Calculate work hours equivalent
        BigDecimal workHours = user.calculateWorkHours(amount);

        Goal goal = GoalDAO.findByUserId(user.getUserId());

        BigDecimal categorySpending = TransactionDAO.getMonthlyCategoryTotal(user.getUserId(), category);

        BigDecimal monthlyTotal = TransactionDAO.getMonthlyTotal(user.getUserId());

        String nudgeMessage = generateNudgeMessage(user, amount, workHours, category, 
                                                    categorySpending, monthlyTotal, goal);

        request.getSession().setAttribute("pendingAmount", amount);
        request.getSession().setAttribute("pendingWorkHours", workHours);
        request.getSession().setAttribute("pendingCategory", category);
        request.getSession().setAttribute("pendingDescription", description);
        request.getSession().setAttribute("pendingNudgeMessage", nudgeMessage);

        int goalDelayDays = 0;
        if (goal != null) {
            goalDelayDays = goal.calculateDelayDays(amount);
        }

        request.setAttribute("user", user);
        request.setAttribute("amount", amount);
        request.setAttribute("workHours", workHours);
        request.setAttribute("formattedWorkHours", formatWorkHours(workHours));
        request.setAttribute("category", category);
        request.setAttribute("categorySpending", categorySpending);
        request.setAttribute("monthlyTotal", monthlyTotal);
        request.setAttribute("monthlyBudget", user.getMonthlyBudget());
        request.setAttribute("goal", goal);
        request.setAttribute("goalDelayDays", goalDelayDays);
        request.setAttribute("nudgeMessage", nudgeMessage);
        request.setAttribute("currentStreak", user.getCurrentStreak());

        request.getRequestDispatcher("/jsp/result.jsp").forward(request, response);
    }

    // Process the user's decision to skip or buy
    private void processDecision(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();

        BigDecimal amount = (BigDecimal) session.getAttribute("pendingAmount");
        BigDecimal workHours = (BigDecimal) session.getAttribute("pendingWorkHours");
        Category category = (Category) session.getAttribute("pendingCategory");
        String description = (String) session.getAttribute("pendingDescription");
        String nudgeMessage = (String) session.getAttribute("pendingNudgeMessage");
        
        if (amount == null) {
            response.sendRedirect(request.getContextPath() + "/calculator");
            return;
        }
        
        String decisionStr = request.getParameter("decision");
        Decision decision = "skip".equals(decisionStr) ? Decision.SKIP : Decision.BUY;

        Goal goal = GoalDAO.findByUserId(user.getUserId());
        BigDecimal goalProgress = (goal != null) ? BigDecimal.valueOf(goal.getProgressPercent()) : null;

        NudgeLog log = new NudgeLog(
            user.getUserId(),
            amount,
            workHours,
            category.getDbValue(),
            decision,
            user.getCurrentStreak(),
            goalProgress,
            user.getKnowledgeLevel().getDbValue(),
            nudgeMessage
        );
        NudgeLogDAO.create(log);

        if (decision == Decision.SKIP) {
            UserDAO.recordSkip(user.getUserId(), amount);

            if (goal != null) {
                GoalDAO.addProgress(user.getUserId(), amount);
            }

            request.setAttribute("decision", "skip");
            request.setAttribute("message", "Great job! You saved $" + amount + 
                                 " (" + formatWorkHours(workHours) + " of work)!");
            request.setAttribute("amountSaved", amount);
            
        } else {
            UserDAO.recordBuy(user.getUserId());

            Transaction transaction = new Transaction(
                user.getUserId(),
                amount,
                category,
                description,
                true,
                new Date(System.currentTimeMillis())
            );
            TransactionDAO.create(transaction);

            request.setAttribute("decision", "buy");
            request.setAttribute("message", "Purchase recorded. Your streak has been reset.");
            request.setAttribute("amountSpent", amount);
        }

        session.removeAttribute("pendingAmount");
        session.removeAttribute("pendingWorkHours");
        session.removeAttribute("pendingCategory");
        session.removeAttribute("pendingDescription");
        session.removeAttribute("pendingNudgeMessage");

        User refreshedUser = UserDAO.findById(user.getUserId());
        session.setAttribute("user", refreshedUser);

        request.setAttribute("user", refreshedUser);
        request.setAttribute("skipRate", NudgeLogDAO.getSkipRate(user.getUserId()));
        request.setAttribute("totalSaved", NudgeLogDAO.getTotalSaved(user.getUserId()));

        request.getRequestDispatcher("/jsp/decision.jsp").forward(request, response);
    }

    // Generate a nudge message based on user knowledge level and spending details
    private String generateNudgeMessage(User user, BigDecimal amount, BigDecimal workHours,
                                        Category category, BigDecimal categorySpending,
                                        BigDecimal monthlyTotal, Goal goal) {
        
        StringBuilder message = new StringBuilder();
        KnowledgeLevel level = user.getKnowledgeLevel();

        message.append("You're about to spend: $").append(amount.setScale(2, RoundingMode.HALF_UP));
        message.append("\n• That's ").append(formatWorkHours(workHours)).append(" of work");
        
        if (level == KnowledgeLevel.BEGINNER) {
            BigDecimal dailyEquivalent = amount.multiply(new BigDecimal("30"));
            message.append("\n• Tip: $").append(amount).append(" daily = $")
                   .append(dailyEquivalent.setScale(0, RoundingMode.HALF_UP)).append("/month");
            message.append("\n\nIs this worth ").append(formatWorkHours(workHours)).append(" of your time?");
            
        } else if (level == KnowledgeLevel.INTERMEDIATE) {
            message.append("\n• You've spent $").append(categorySpending.setScale(2, RoundingMode.HALF_UP))
                   .append(" on ").append(category.getDisplayName().toLowerCase()).append(" this month");
            
            if (goal != null) {
                message.append("\n• ").append(goal.getGoalName()).append(" goal: $")
                       .append(goal.getCurrentAmount().setScale(0, RoundingMode.HALF_UP))
                       .append(" / $").append(goal.getTargetAmount().setScale(0, RoundingMode.HALF_UP))
                       .append(" saved");
                
                int delayDays = goal.calculateDelayDays(amount);
                if (delayDays > 0) {
                    message.append("\n• This purchase delays your goal by ").append(delayDays).append(" day(s)");
                }
            }

            BigDecimal newTotal = monthlyTotal.add(amount);
            if (newTotal.compareTo(user.getMonthlyBudget()) > 0) {
                BigDecimal over = newTotal.subtract(user.getMonthlyBudget());
                message.append("\n• ⚠️ This puts you $").append(over.setScale(2, RoundingMode.HALF_UP))
                       .append(" over budget");
            }
            
        } else {
            BigDecimal weeklyIncome = user.getHourlyWage().multiply(new BigDecimal("40"));
            BigDecimal incomePercent = amount.divide(weeklyIncome, 4, RoundingMode.HALF_UP)
                                            .multiply(new BigDecimal("100"));
            
            message.append(" (").append(incomePercent.setScale(1, RoundingMode.HALF_UP))
                   .append("% of weekly income)");

            message.append("\n• ").append(category.getDisplayName()).append(" spending: $")
                   .append(categorySpending.setScale(2, RoundingMode.HALF_UP));

            BigDecimal budgetPercent = monthlyTotal.divide(user.getMonthlyBudget(), 4, RoundingMode.HALF_UP)
                                                   .multiply(new BigDecimal("100"));
            message.append("\n• Monthly budget: ").append(budgetPercent.setScale(0, RoundingMode.HALF_UP))
                   .append("% used ($").append(monthlyTotal.setScale(2, RoundingMode.HALF_UP))
                   .append(" / $").append(user.getMonthlyBudget().setScale(2, RoundingMode.HALF_UP)).append(")");

            if (goal != null) {
                int delayDays = goal.calculateDelayDays(amount);
                if (delayDays > 0) {
                    message.append("\n• Delays ").append(goal.getGoalName())
                           .append(" by ").append(delayDays).append(" day(s)");
                }
            }

            if (user.getCurrentStreak() > 0) {
                message.append("\n• 🔥 You have a ").append(user.getCurrentStreak())
                       .append("-day streak at risk!");
            }

            BigDecimal projectedOver = monthlyTotal.add(amount).subtract(user.getMonthlyBudget());
            if (projectedOver.compareTo(BigDecimal.ZERO) > 0) {
                message.append("\n• At this rate: $").append(projectedOver.setScale(2, RoundingMode.HALF_UP))
                       .append(" over budget by month-end");
            }
        }
        
        return message.toString();
    }

    // Format work hours into a readable string
    private String formatWorkHours(BigDecimal workHours) {
        double hours = workHours.doubleValue();
        
        if (hours < 1) {
            int minutes = (int) Math.round(hours * 60);
            return minutes + " minute" + (minutes != 1 ? "s" : "");
        } else if (hours == 1.0) {
            return "1 hour";
        } else {
            return workHours.setScale(1, RoundingMode.HALF_UP) + " hours";
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
