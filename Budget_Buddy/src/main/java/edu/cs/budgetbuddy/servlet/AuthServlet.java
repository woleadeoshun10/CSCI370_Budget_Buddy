package edu.cs.budgetbuddy.servlet;

import edu.cs.budgetbuddy.dao.UserDAO;
import edu.cs.budgetbuddy.model.User;
import edu.cs.budgetbuddy.util.DatabaseUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;

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
                request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
                break;
            case "signup":
                request.getRequestDispatcher("/jsp/signup.jsp").forward(request, response);
                break;
            case "logout":
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
                response.sendRedirect(request.getContextPath() + "/auth?action=login");
                break;
            default:
                request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
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
            default:
                request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
        }
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
        String confirmPassword = request.getParameter("confirm_password");
        BigDecimal hourlyWage = new BigDecimal(request.getParameter("hourly_wage"));
        BigDecimal monthlyBudget = new BigDecimal(request.getParameter("monthly_budget"));
        
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
}
