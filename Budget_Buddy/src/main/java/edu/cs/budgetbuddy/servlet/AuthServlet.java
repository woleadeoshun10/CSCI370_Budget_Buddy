package edu.cs.budgetbuddy.servlet;

import java.io.IOException;
import java.math.BigDecimal;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.cs.budgetbuddy.dao.UserDAO;
import edu.cs.budgetbuddy.model.User;
import edu.cs.budgetbuddy.util.DatabaseUtil;

//Get login and signup info.
@WebServlet("/auth")
public class AuthServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) action = "login";
        
        switch (action) {
            case "login":
                showLoginPage(request, response);
                break;
            case "signup":
                showSignupPage(request, response);
                break;
            case "logout":
                logout(request, response);
                break;
            case "profile":
                showProfilePage(request, response);
                break;
            default:
                showLoginPage(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) action = "login";
        
        switch (action) {
            case "login":
                processLogin(request, response);
                break;
            case "signup":
                processSignup(request, response);
                break;
            case "profile":
                processProfileUpdate(request, response);
                break;
            default:
                showLoginPage(request, response);
        }
    }

    private void showLoginPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // If already logged in, redirect to dashboard
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        
        request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
    }

    private void showSignupPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.getRequestDispatcher("/jsp/signup.jsp").forward(request, response);
    }

    private void showProfilePage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Must be logged in
        User user = getLoggedInUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }
        
        // Refresh user data from database
        User freshUser = UserDAO.findById(user.getUserId());
        request.getSession().setAttribute("user", freshUser);
        request.setAttribute("user", freshUser);
        
        request.getRequestDispatcher("/jsp/profile.jsp").forward(request, response);
    }

    private void logout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            System.out.println("AuthServlet: Logging out user");
            session.invalidate();
        }
        
        response.sendRedirect(request.getContextPath() + "/auth?action=login&message=logged_out");
    }

    private void processLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Please enter both username and password.");
            request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
            return;
        }
        
        User user = UserDAO.authenticate(username.trim(), password);
        
        if (user != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getUserId());
            session.setMaxInactiveInterval(30 * 60); // 30 minutes
            
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            request.setAttribute("error", "Invalid username or password.");
            request.setAttribute("username", username);
            request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
        }
    }
    private void processSignup(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        BigDecimal hourlyWage = new BigDecimal(request.getParameter("hourlyWage"));
        BigDecimal monthlyBudget = new BigDecimal(request.getParameter("monthlyBudget"));
        
        // Form submission validation
        if (username == null || username.trim().isEmpty() ||
            email == null    || email.trim().isEmpty()    ||
            password == null || password.trim().isEmpty() ||
            confirmPassword == null || confirmPassword.trim().isEmpty()) {
            request.setAttribute("error", "All fields are required.");
            request.getRequestDispatcher("/jsp/signup.jsp").forward(request, response);
            return;
        }

        // Checks for password match
        if(!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match.");
            request.getRequestDispatcher("/jsp/signup.jsp").forward(request, response);
            return;
        }

        //Check for password length
        if(password.length() < 6) {
            request.setAttribute("error", "Password must be at least 6 characters long.");
            request.getRequestDispatcher("/jsp/signup.jsp").forward(request, response);
            return;
        }

        // Check if username already exists in system
        if (UserDAO.usernameExists(username.trim())) {
            request.setAttribute("error", "Username already taken.");
            request.getRequestDispatcher("/jsp/signup.jsp").forward(request, response);
            return;
        }

        //Check if email already exists in system
        if (UserDAO.emailExists(email.trim())) {
            request.setAttribute("error", "Email already registered.");
            request.getRequestDispatcher("/jsp/signup.jsp").forward(request, response);
            return;
        }

        User newUser = new User(username.trim(), email.trim(),
                                DatabaseUtil.hashPassword(password));

        //Adds user provided fields to the newUser object
        try {
            newUser.setHourlyWage(hourlyWage);
            newUser.setMonthlyBudget(monthlyBudget);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid number format for wage or budget.");
            request.getRequestDispatcher("/jsp/signup.jsp").forward(request, response);
            return;
        }

        //Attempts to create user using info provided during signup
        //Creates a session if successful or kicks the user back to signup page if not
        User createdUser = UserDAO.create(newUser);
        if (createdUser != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("user", createdUser);
            session.setAttribute("userId", createdUser.getUserId());
            session.setMaxInactiveInterval(30 * 60); // 30 minutes
            
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            request.setAttribute("error", "Failed to create account. Please try again.");
            request.getRequestDispatcher("/jsp/signup.jsp").forward(request, response);
        }
    }

    private void processProfileUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        User user = getLoggedInUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth?action=login");
            return;
        }
        
        String hourlyWageStr = request.getParameter("hourlyWage");
        String monthlyBudgetStr = request.getParameter("monthlyBudget");
        String knowledgeLevelStr = request.getParameter("knowledgeLevel");
        String commitmentMessage = request.getParameter("commitmentMessage");
        String futureSelfMessage = request.getParameter("futureSelfMessage");
        
        try {
            // Update user object
            if (hourlyWageStr != null && !hourlyWageStr.trim().isEmpty()) {
                user.setHourlyWage(new BigDecimal(hourlyWageStr.trim()));
            }
            if (monthlyBudgetStr != null && !monthlyBudgetStr.trim().isEmpty()) {
                user.setMonthlyBudget(new BigDecimal(monthlyBudgetStr.trim()));
            }
            if (knowledgeLevelStr != null && !knowledgeLevelStr.trim().isEmpty()) {
                user.setKnowledgeLevel(KnowledgeLevel.fromDbValue(knowledgeLevelStr));
            }
            if (commitmentMessage != null) {
                user.setCommitmentMessage(commitmentMessage.trim());
            }
            if (futureSelfMessage != null) {
                user.setFutureSelfMessage(futureSelfMessage.trim());
            }
            
            // Save to database
            boolean success = UserDAO.updateProfile(user);
            
            if (success) {
                // Update session with new data
                request.getSession().setAttribute("user", user);
                request.setAttribute("success", "Profile updated successfully!");
            } else {
                request.setAttribute("error", "Failed to update profile. Please try again.");
            }
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid number format for wage or budget.");
        }
        
        request.setAttribute("user", user);
        request.getRequestDispatcher("/jsp/profile.jsp").forward(request, response);
    }

    private User getLoggedInUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (User) session.getAttribute("user");
        }
        return null;
    }

    private void preserveSignupFormData(HttpServletRequest request, 
                                        String username, String email,
                                        String hourlyWage, String monthlyBudget) {
        if (username != null) request.setAttribute("username", username);
        if (email != null) request.setAttribute("email", email);
        if (hourlyWage != null) request.setAttribute("hourlyWage", hourlyWage);
        if (monthlyBudget != null) request.setAttribute("monthlyBudget", monthlyBudget);
    }
}
