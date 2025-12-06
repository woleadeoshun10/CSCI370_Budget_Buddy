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
}
