<!--Adewole: Embedded CSS in this login page for simplicity-->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!-- basic html setup -->
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Budget Buddy - Login</title>

    <!-- all css for this page -->
    <style>
        /* reset defaults */
        * { margin: 0; padding: 0; box-sizing: border-box; }

        /* main purple background + center form */
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        /* white login box */
        .container {
            background: white;
            padding: 40px;
            border-radius: 16px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
            width: 100%;
            max-width: 400px;
        }

        /* title */
        h1 {
            text-align: center;
            color: #333;
            margin-bottom: 10px;
        }

        /* small subtitle under title */
        .subtitle {
            text-align: center;
            color: #666;
            margin-bottom: 30px;
        }

        /* spacing between form inputs */
        .form-group {
            margin-bottom: 20px;
        }

        /* labels above inputs */
        label {
            display: block;
            margin-bottom: 8px;
            color: #333;
            font-weight: 500;
        }

        /* input fields */
        input[type="text"], input[type="password"] {
            width: 100%;
            padding: 12px 16px;
            border: 2px solid #e1e1e1;
            border-radius: 8px;
            font-size: 16px;
            transition: border-color 0.3s;
        }

        /* highlight input on click */
        input:focus {
            outline: none;
            border-color: #667eea;
        }

        /* login button */
        button {
            width: 100%;
            padding: 14px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: transform 0.2s;
        }

        /* small hover lift */
        button:hover {
            transform: translateY(-2px);
        }

        /* error message box */
        .error {
            background: #fee;
            color: #c00;
            padding: 12px;
            border-radius: 8px;
            margin-bottom: 20px;
            text-align: center;
        }

        /* bottom link to signup */
        .signup-link {
            text-align: center;
            margin-top: 20px;
            color: #666;
        }
        .signup-link a {
            color: #667eea;
            text-decoration: none;
            font-weight: 500;
        }
    </style>
</head>

<body>
    <!-- white container holding the form -->
    <div class="container">
        <h1>💰 Budget Buddy</h1>
        <p class="subtitle">Sprint 1 - Login</p>

        <!-- show backend error if exists -->
        <% if (request.getAttribute("error") != null) { %>
            <div class="error"><%= request.getAttribute("error") %></div>
        <% } %>

        <!-- login form -->
        <form action="${pageContext.request.contextPath}/auth" method="post">

            <!-- tells servlet this is login -->
            <input type="hidden" name="action" value="login">

            <!-- username input -->
            <div class="form-group">
                <label for="username">Username</label>

                <!-- refill input if error happened -->
                <input type="text" id="username" name="username"
                    value="<%= request.getAttribute("username") != null ? request.getAttribute("username") : "" %>"
                    required>
            </div>

            <!-- password input -->
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required>
            </div>

            <!-- login button -->
            <button type="submit">Login</button>
        </form>

        <!-- link to signup page -->
        <p class="signup-link">
            Don't have an account?
            <a href="${pageContext.request.contextPath}/auth?action=signup">Sign up</a>
        </p>
    </div>
</body>
</html>


