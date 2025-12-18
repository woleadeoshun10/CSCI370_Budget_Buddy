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
import edu.cs.budgetbuddy.model.User.KnowledgeLevel;
import edu.cs.budgetbuddy.util.DatabaseUtil;

//Get login and signup info.
@WebServlet("/auth")
public class AuthServlet extends HttpServlet {

    //Redirect to login or signup pages, or process login/signup form submissions.
    //Also handle logout and profile viewing/updating.
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

    //Process login and signup form submissions.
    //Also handle profile updates.
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

    // Show login page
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

    // Show signup page
    private void showSignupPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.getRequestDispatcher("/jsp/signup.jsp").forward(request, response);
    }

    // Show profile page
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

    // Handle logout
    private void logout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            System.out.println("AuthServlet: Logging out user");
            session.invalidate();
        }
        
        response.sendRedirect(request.getContextPath() + "/auth?action=login&message=logged_out");
    }

    // Process login form submission
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

    // Process signup form submission
    private void processSignup(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String hourlyWageStr = request.getParameter("hourlyWage");
        String monthlyBudgetStr = request.getParameter("monthlyBudget");
        String knowledgeLevelStr = request.getParameter("knowledgeLevel");
        
        // Validate required fields
        if (username == null || username.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.isEmpty()) {
            
            request.setAttribute("error", "Please fill in all required fields.");
            preserveSignupFormData(request, username, email, hourlyWageStr, monthlyBudgetStr);
            request.getRequestDispatcher("/jsp/signup.jsp").forward(request, response);
            return;
        }
        
        // Validate password match
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match.");
            preserveSignupFormData(request, username, email, hourlyWageStr, monthlyBudgetStr);
            request.getRequestDispatcher("/jsp/signup.jsp").forward(request, response);
            return;
        }
        
        // Validate password length
        if (password.length() < 6) {
            request.setAttribute("error", "Password must be at least 6 characters.");
            preserveSignupFormData(request, username, email, hourlyWageStr, monthlyBudgetStr);
            request.getRequestDispatcher("/jsp/signup.jsp").forward(request, response);
            return;
        }
        
        // Check if username already exists
        if (UserDAO.usernameExists(username.trim())) {
            request.setAttribute("error", "Username already taken. Please choose another.");
            preserveSignupFormData(request, null, email, hourlyWageStr, monthlyBudgetStr);
            request.getRequestDispatcher("/jsp/signup.jsp").forward(request, response);
            return;
        }
        
        // Check if email already exists
        if (UserDAO.emailExists(email.trim())) {
            request.setAttribute("error", "Email already registered. Please login or use another email.");
            preserveSignupFormData(request, username, null, hourlyWageStr, monthlyBudgetStr);
            request.getRequestDispatcher("/jsp/signup.jsp").forward(request, response);
            return;
        }
        
        // Create new user
        User newUser = new User(
            username.trim(),
            email.trim(),
            DatabaseUtil.hashPassword(password)
        );
        
        // Set optional fields
        try {
            if (hourlyWageStr != null && !hourlyWageStr.trim().isEmpty()) {
                newUser.setHourlyWage(new BigDecimal(hourlyWageStr.trim()));
            }
            if (monthlyBudgetStr != null && !monthlyBudgetStr.trim().isEmpty()) {
                newUser.setMonthlyBudget(new BigDecimal(monthlyBudgetStr.trim()));
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid number format for wage or budget.");
            preserveSignupFormData(request, username, email, hourlyWageStr, monthlyBudgetStr);
            request.getRequestDispatcher("/jsp/signup.jsp").forward(request, response);
            return;
        }
        
        if (knowledgeLevelStr != null && !knowledgeLevelStr.trim().isEmpty()) {
            newUser.setKnowledgeLevel(KnowledgeLevel.fromDbValue(knowledgeLevelStr));
        }
        
        // Save to database
        User createdUser = UserDAO.create(newUser);
        
        if (createdUser != null) {
            // Success - log them in automatically
            HttpSession session = request.getSession(true);
            session.setAttribute("user", createdUser);
            session.setAttribute("userId", createdUser.getUserId());
            session.setMaxInactiveInterval(30 * 60);
            
            System.out.println("AuthServlet: Signup successful for " + username);
            
            // Redirect to goal setup (first-time user flow)
            response.sendRedirect(request.getContextPath() + "/goal?action=setup&welcome=true");
        } else {
            request.setAttribute("error", "Registration failed. Please try again.");
            preserveSignupFormData(request, username, email, hourlyWageStr, monthlyBudgetStr);
            request.getRequestDispatcher("/jsp/signup.jsp").forward(request, response);
        }
    }

    // Process profile update form submission
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

    // Get the currently logged-in user from the session
    private User getLoggedInUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (User) session.getAttribute("user");
        }
        return null;
    }

    // Preserve form data on signup page in case of errors
    private void preserveSignupFormData(HttpServletRequest request, 
                                        String username, String email,
                                        String hourlyWage, String monthlyBudget) {
        if (username != null) request.setAttribute("username", username);
        if (email != null) request.setAttribute("email", email);
        if (hourlyWage != null) request.setAttribute("hourlyWage", hourlyWage);
        if (monthlyBudget != null) request.setAttribute("monthlyBudget", monthlyBudget);
    }
}
