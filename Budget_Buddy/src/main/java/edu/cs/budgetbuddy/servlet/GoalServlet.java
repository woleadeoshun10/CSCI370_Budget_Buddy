package edu.cs.budgetbuddy.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.cs.budgetbuddy.dao.GoalDAO;
import edu.cs.budgetbuddy.model.Goal;
import edu.cs.budgetbuddy.model.User;

// Servlet to handle goal-related actions: setup, edit, view status, add progress, delete
@WebServlet("/goal")
public class GoalServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // Handle GET requests to display goal status, setup form, or edit form
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        User user = getLoggedInUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("setup".equals(action)) {
            showSetupForm(request, response, user);
        } else if ("edit".equals(action)) {
            showEditForm(request, response, user);
        } else {
            showGoalStatus(request, response, user);
        }
    }

    // Handle POST requests to save, delete, or add progress to goal
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User user = getLoggedInUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("save".equals(action)) {
            processSaveGoal(request, response, user);
        } else if ("delete".equals(action)) {
            processDeleteGoal(request, response, user);
        } else if ("addProgress".equals(action)) {
            processAddProgress(request, response, user);
        } else {
            response.sendRedirect(request.getContextPath() + "/goal");
        }
    }

    // Display the current goal status to the user
    private void showGoalStatus(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        
        Goal goal = GoalDAO.findByUserId(user.getUserId());
        
        if (goal == null) {
            response.sendRedirect(request.getContextPath() + "/goal?action=setup");
            return;
        }
        
        request.setAttribute("user", user);
        request.setAttribute("goal", goal);
        request.setAttribute("progressPercent", goal.getProgressPercent());
        request.setAttribute("amountRemaining", goal.getAmountRemaining());
        request.setAttribute("daysRemaining", goal.getDaysUntilDeadline());
        request.setAttribute("isOnTrack", goal.isOnTrack());
        
        request.getRequestDispatcher("/jsp/goal.jsp").forward(request, response);
    }

    // Display the goal setup form to the user
    private void showSetupForm(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        
        Goal existingGoal = GoalDAO.findByUserId(user.getUserId());
        if (existingGoal != null) {
            response.sendRedirect(request.getContextPath() + "/goal?action=edit");
            return;
        }
        
        request.setAttribute("user", user);
        request.setAttribute("isNewUser", "true".equals(request.getParameter("welcome")));
        

        long threeMonthsLater = System.currentTimeMillis() + (90L * 24 * 60 * 60 * 1000);
        request.setAttribute("suggestedDeadline", new Date(threeMonthsLater));
        
        request.getRequestDispatcher("/jsp/setGoal.jsp").forward(request, response);
    }

    // Display the goal edit form to the user
    private void showEditForm(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        
        Goal goal = GoalDAO.findByUserId(user.getUserId());
        
        if (goal == null) {
            response.sendRedirect(request.getContextPath() + "/goal?action=setup");
            return;
        }
        
        request.setAttribute("user", user);
        request.setAttribute("goal", goal);
        request.setAttribute("isEdit", true);
        
        request.getRequestDispatcher("/jsp/setGoal.jsp").forward(request, response);
    }

    // Process saving (creating or updating) a goal
    private void processSaveGoal(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        
        String goalName = request.getParameter("goalName");
        String targetAmountStr = request.getParameter("targetAmount");
        String currentAmountStr = request.getParameter("currentAmount");
        String deadlineStr = request.getParameter("deadline");
        String futureSelfMessage = request.getParameter("futureSelfMessage");
        

        if (goalName == null || goalName.trim().isEmpty()) {
            request.setAttribute("error", "Please enter a goal name.");
            showSetupForm(request, response, user);
            return;
        }
        
       
        if (targetAmountStr == null || targetAmountStr.trim().isEmpty()) {
            request.setAttribute("error", "Please enter a target amount.");
            showSetupForm(request, response, user);
            return;
        }
        
        BigDecimal targetAmount;
        try {
            targetAmount = new BigDecimal(targetAmountStr.trim());
            if (targetAmount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NumberFormatException("Amount must be positive");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Please enter a valid positive target amount.");
            showSetupForm(request, response, user);
            return;
        }
        

        BigDecimal currentAmount = BigDecimal.ZERO;
        if (currentAmountStr != null && !currentAmountStr.trim().isEmpty()) {
            try {
                currentAmount = new BigDecimal(currentAmountStr.trim());
                if (currentAmount.compareTo(BigDecimal.ZERO) < 0) {
                    currentAmount = BigDecimal.ZERO;
                }
            } catch (NumberFormatException e) {
                // Ignore, use default
            }
        }
        

        Date deadline = null;
        if (deadlineStr != null && !deadlineStr.trim().isEmpty()) {
            try {
                deadline = Date.valueOf(deadlineStr.trim());
            } catch (IllegalArgumentException e) {
                // Ignore invalid date
            }
        }
        

        Goal existingGoal = GoalDAO.findByUserId(user.getUserId());
        Goal goal;
        
        if (existingGoal != null) {

            goal = existingGoal;
            goal.setGoalName(goalName.trim());
            goal.setTargetAmount(targetAmount);
            goal.setCurrentAmount(currentAmount);
            goal.setDeadline(deadline);
            goal.setFutureSelfMessage(futureSelfMessage != null ? futureSelfMessage.trim() : null);
            
            boolean success = GoalDAO.update(goal);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/goal?message=updated");
            } else {
                request.setAttribute("error", "Failed to update goal. Please try again.");
                request.setAttribute("goal", goal);
                request.getRequestDispatcher("/jsp/setGoal.jsp").forward(request, response);
            }
        } else {
            // Create new
            goal = new Goal(
                user.getUserId(),
                goalName.trim(),
                targetAmount,
                deadline,
                futureSelfMessage != null ? futureSelfMessage.trim() : null
            );
            goal.setCurrentAmount(currentAmount);
            
            Goal created = GoalDAO.create(goal);
            
            if (created != null) {
                boolean isWelcome = "true".equals(request.getParameter("welcome"));
                if (isWelcome) {
                    response.sendRedirect(request.getContextPath() + "/dashboard?welcome=true");
                } else {
                    response.sendRedirect(request.getContextPath() + "/goal?message=created");
                }
            } else {
                request.setAttribute("error", "Failed to create goal. Please try again.");
                showSetupForm(request, response, user);
            }
        }
    }

    // Process deleting the user's goal
    private void processDeleteGoal(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        
        boolean success = GoalDAO.deleteForUser(user.getUserId());
        
        if (success) {
            response.sendRedirect(request.getContextPath() + "/goal?action=setup&message=deleted");
        } else {
            response.sendRedirect(request.getContextPath() + "/goal?error=deletefailed");
        }
    }

    // Process adding progress to the user's goal
    private void processAddProgress(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        
        String amountStr = request.getParameter("amount");
        
        if (amountStr == null || amountStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/goal?error=noamount");
            return;
        }
        
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountStr.trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NumberFormatException("Amount must be positive");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/goal?error=invalidamount");
            return;
        }
        
        boolean success = GoalDAO.addProgress(user.getUserId(), amount);
        
        if (success) {
            response.sendRedirect(request.getContextPath() + "/goal?message=progress&amount=" + amount);
        } else {
            response.sendRedirect(request.getContextPath() + "/goal?error=progressfailed");
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