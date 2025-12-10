package edu.cs.budgetbuddy.servlet;

import edu.cs.budgetbuddy.dao.NudgeLogDAO;
import edu.cs.budgetbuddy.dao.UserDAO;
import edu.cs.budgetbuddy.model.NudgeLog;
import edu.cs.budgetbuddy.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

@WebServlet("/calculator")
public class FrictionNudgeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        User user = getLoggedInUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }
        
        request.setAttribute("user", user);
        request.getRequestDispatcher("/jsp/calculator.jsp").forward(request, response);
    }

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
 
    private void calculateAndShowNudge(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        
        String amountStr = request.getParameter("amount");
        String category = request.getParameter("category");
        if (category == null || category.isEmpty()) category = "other";
        
        if (amountStr == null || amountStr.trim().isEmpty()) {
            request.setAttribute("error", "Please enter an amount.");
            request.setAttribute("user", user);
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
            request.getRequestDispatcher("/jsp/calculator.jsp").forward(request, response);
            return;
        }
        
        BigDecimal workHours = user.calculateWorkHours(amount);
        
        String nudgeMessage = generateNudgeMessage(user, amount, workHours);
        
        HttpSession session = request.getSession();
        session.setAttribute("pendingAmount", amount);
        session.setAttribute("pendingWorkHours", workHours);
        session.setAttribute("pendingCategory", category);
        session.setAttribute("pendingNudgeMessage", nudgeMessage);
        
        request.setAttribute("user", user);
        request.setAttribute("amount", amount);
        request.setAttribute("workHours", workHours);
        request.setAttribute("formattedWorkHours", formatWorkHours(workHours));
        request.setAttribute("nudgeMessage", nudgeMessage);
        request.setAttribute("currentStreak", user.getCurrentStreak());
        
        request.getRequestDispatcher("/jsp/result.jsp").forward(request, response);
            }

    private void processDecision(HttpServletRequest request, HttpServletResponse response, User user)
        throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        BigDecimal amount = (BigDecimal) session.getAttribute("pendingAmount");
        BigDecimal workHours = (BigDecimal) session.getAttribute("pendingWorkHours");
        String category = (String) session.getAttribute("pendingCategory");
        String nudgeMessage = (String) session.getAttribute("pendingNudgeMessage");
        
        if (amount == null) {
            response.sendRedirect(request.getContextPath() + "/calculator");
            return;
        }
        
        String decision = request.getParameter("decision");
        
        NudgeLog log = new NudgeLog(user.getUserId(), amount, workHours, category, decision);
        log.setStreakAtDecision(user.getCurrentStreak());
        log.setNudgeMessage(nudgeMessage);
        NudgeLogDAO.create(log);
        
        if ("skip".equals(decision)) {
            UserDAO.recordSkip(user.getUserId(), amount);
            request.setAttribute("decision", "skip");
            request.setAttribute("message", "Great job! You saved $" + amount.setScale(2, RoundingMode.HALF_UP) + 
                                 " (" + formatWorkHours(workHours) + " of work)!");
            request.setAttribute("amountSaved", amount);
        } else {
            UserDAO.recordBuy(user.getUserId());
            request.setAttribute("decision", "buy");
            request.setAttribute("message", "Purchase recorded. Your streak has been reset.");
            request.setAttribute("amountSpent", amount);
        }

        session.removeAttribute("pendingAmount");
        session.removeAttribute("pendingWorkHours");
        session.removeAttribute("pendingCategory");
        session.removeAttribute("pendingNudgeMessage");

        User refreshedUser = UserDAO.findById(user.getUserId());
        session.setAttribute("user", refreshedUser);
        
        request.setAttribute("user", refreshedUser);
        request.setAttribute("skipRate", NudgeLogDAO.getSkipRate(user.getUserId()));
        request.setAttribute("totalSaved", refreshedUser.getTotalSaved());
        
        request.getRequestDispatcher("/jsp/decision.jsp").forward(request, response);
    }

    private String generateNudgeMessage(User user, BigDecimal amount, BigDecimal workHours) {
        StringBuilder message = new StringBuilder();
        
        message.append("You're about to spend: $").append(amount.setScale(2, RoundingMode.HALF_UP));
        message.append("\n• That's ").append(formatWorkHours(workHours)).append(" of work at $")
               .append(user.getHourlyWage()).append("/hour");
        
        if (user.getCurrentStreak() > 0) {
            message.append("\n• 🔥 You have a ").append(user.getCurrentStreak())
                   .append("-day streak at risk!");
        }
        
        message.append("\n\nIs this worth ").append(formatWorkHours(workHours)).append(" of your time?");
        
        return message.toString();
    }

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

    private User getLoggedInUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (User) session.getAttribute("user");
        }
        return null;
    }
    
}