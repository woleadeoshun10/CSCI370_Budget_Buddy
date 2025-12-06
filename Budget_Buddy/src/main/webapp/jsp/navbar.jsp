<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="edu.cs.budgetbuddy.model.User" %>
<%
    User user = (User) request.getAttribute("user");
%>
<nav class="navbar">
    <h1>💰 Budget Buddy</h1>
    <div>
        <span style="color: white;">Welcome, <%= user.getUsername() %></span>
        <a href="${pageContext.request.contextPath}/auth?action=logout">Logout</a>
    </div>
</nav>
