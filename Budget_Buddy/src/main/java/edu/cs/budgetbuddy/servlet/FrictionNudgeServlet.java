package edu.cs.budgetbuddy.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.cs.budgetbuddy.model.User;

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

            }

    private void processDecision(HttpServletRequest request, HttpServletResponse response, User user)
        throws ServletException, IOException {
        
    }

    private User getLoggedInUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (User) session.getAttribute("user");
        }
        return null;
    }
    
}