<!--Adewole: Embedded CSS in this signup page for simplicity-->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!-- basic html setup -->
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Budget Buddy - Sign Up</title>

    <!-- styles specific to this page -->
    <style>
        /* reset default stuff */
        * { margin: 0; padding: 0; box-sizing: border-box; }

        /* purple background + center form */
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }

        /* white box holding the signup form */
        .container {
            background: white;
            padding: 40px;
            border-radius: 16px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
            width: 100%;
            max-width: 450px;
        }

        /* page title */
        h1 {
            text-align: center;
            color: #333;
            margin-bottom: 10px;
        }

        /* little description text under title */
        .subtitle {
            text-align: center;
            color: #666;
            margin-bottom: 30px;
        }

        /* space between each form input */
        .form-group {
            margin-bottom: 20px;
        }

        /* wrapper for two inputs side by side */
        .form-row {
            display: flex;
            gap: 15px;
        }
        .form-row .form-group {
            flex: 1;  /* make both even size */
        }

        /* labels above inputs */
        label {
            display: block;
            margin-bottom: 8px;
            color: #333;
            font-weight: 500;
        }

        /* styling for all input fields */
        input {
            width: 100%;
            padding: 12px 16px;
            border: 2px solid #e1e1e1;
            border-radius: 8px;
            font-size: 16px;
            transition: border-color 0.3s;
        }

        /* highlight when clicking input */
        input:focus {
            outline: none;
            border-color: #667eea;
        }

        /* submit button */
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

        /* small animation on hover */
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

        /* link under form for login */
        .login-link {
            text-align: center;
            margin-top: 20px;
            color: #666;
        }
        .login-link a {
            color: #667eea;
            text-decoration: none;
            font-weight: 500;
        }

        /* small hint text under password */
        .hint {
            font-size: 12px;
            color: #999;
            margin-top: 4px;
        }
    </style>
</head>

<body>
    <!-- outer box -->
    <div class="container">

        <!-- title + subtitle -->
        <h1>💰 Budget Buddy</h1>
        <p class="subtitle">Create Your Account</p>

        <!-- if backend sends an "error" attribute, show it -->
        <% if (request.getAttribute("error") != null) { %>
            <div class="error"><%= request.getAttribute("error") %></div>
        <% } %>

        <!-- signup form -->
        <form action="${pageContext.request.contextPath}/auth" method="post">

            <!-- tells servlet this is SIGNUP -->
            <input type="hidden" name="action" value="signup">

            <!-- username -->
            <div class="form-group">
                <label for="username">Username *</label>
                <input type="text" id="username" name="username" required>
            </div>

            <!-- email -->
            <div class="form-group">
                <label for="email">Email *</label>
                <input type="email" id="email" name="email" required>
            </div>

            <!-- password -->
            <div class="form-group">
                <label for="password">Password *</label>
                <input type="password" id="password" name="password" required>
                <p class="hint">At least 6 characters</p>
            </div>

            <!-- confirm password -->
            <div class="form-group">
                <label for="confirmPassword">Confirm Password *</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required>
            </div>

            <!-- wage + budget fields (side-by-side) -->
            <div class="form-row">
                <div class="form-group">
                    <label for="hourlyWage">Hourly Wage ($)</label>
                    <input type="number" id="hourlyWage" name="hourlyWage" step="0.01" value="15.00">
                </div>

                <div class="form-group">
                    <label for="monthlyBudget">Monthly Budget ($)</label>
                    <input type="number" id="monthlyBudget" name="monthlyBudget" step="0.01" value="500.00">
                </div>
            </div>

            <!-- submit button -->
            <button type="submit">Create Account</button>
        </form>

        <!-- link to login page -->
        <p class="login-link">
            Already have an account?
            <a href="${pageContext.request.contextPath}/auth?action=login">Login</a>
        </p>

    </div>
</body>
</html>