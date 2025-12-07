<!--Adewole-->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<!-- letting JSP know this is the error page so it can catch exceptions -->

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Budget Buddy - Error</title>

    <style>
        /* basic resets */
        * { margin: 0; padding: 0; box-sizing: border-box; }

        /* whole background gradient + center layout */
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        /* main error box in the middle */
        .container {
            background: white;
            padding: 50px;
            border-radius: 16px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
            text-align: center;
            max-width: 400px;
        }

        /* the big emoji on top */
        .emoji {
            font-size: 64px;
            margin-bottom: 20px;
        }

        /* title */
        h1 {
            color: #333;
            margin-bottom: 15px;
        }

        /* small error text */
        p {
            color: #666;
            margin-bottom: 25px;
        }

        /* button-style link back to dashboard */
        a {
            display: inline-block;
            padding: 12px 30px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            text-decoration: none;
            border-radius: 8px;
            font-weight: 600;
        }
    </style>
</head>

<body>
    <!-- the actual error card -->
    <div class="container">
        <div class="emoji">😅</div> <!-- little goofy error emoji -->
        <h1>Oops! Something went wrong</h1>
        <p>Don't worry, your savings streak is still intact! Let's get you back on track.</p>

        <!-- taking user back to dashboard safely -->
        <a href="${pageContext.request.contextPath}/dashboard">Back to Dashboard</a>
    </div>
</body>
</html>