# BudgetBuddy – SWE Project (CSCI 370)

BudgetBuddy is a simple financial awareness tool designed to help users pause and rethink impulse spending. Instead of tracking purchases after they happen, the app shows the *real cost* of a purchase before buying by converting prices into work hours and showing how each decision affects personal financial goals.

---

## Project Summary

BudgetBuddy reduces impulse spending by introducing a moment of reflection at the point of purchase.  
The system uses work-hour conversion, goal impact visualizations, simple nudges, and streak tracking to help users make smarter financial decisions.

---

## Team Members

- Jeffy 
- Adewole  
- Allan  

---

## Documentation & Notes

All of the documentation can be found in the Documentation folder both in the ZIP file and in GitHub.

This includes our functional and stakeholder requirements, validation matrix, sprint burndown charts with graphs, architecture diagram.

The installation folder contains a document with information such as the installation and configuration, team member responsibilities and honor statements, functional limitations and known issues.

Sprint completion was documented using GitHub Projects which can be accessed through the link here: (https://github.com/users/TitivutRabaib/projects/2)

---


## Project Structure
```
budget-buddy/
├── pom.xml                          # Maven configuration
├── sql/
│   └── schema.sql                   # Database schema (4 tables)
└── src/main/
    ├── java/edu/cs/budgetbuddy/
    │   ├── util/
    │   │   └── DatabaseUtil.java         # Database connection management
    │   ├── model/
    │   │   ├── User.java                 # User domain object
    │   │   ├── Transaction.java          # Transaction domain object
    │   │   ├── Goal.java                 # Savings goal domain object
    │   │   └── NudgeLog.java             # Friction calculator log
    │   ├── dao/
    │   │   ├── UserDAO.java              # User database operations
    │   │   ├── TransactionDAO.java       # Transaction database operations
    │   │   ├── GoalDAO.java              # Goal database operations
    │   │   └── NudgeLogDAO.java          # Nudge log database operations
    │   └── servlet/
    │       ├── AuthServlet.java          # Login/signup/profile handler
    │       ├── FrictionNudgeServlet.java # Calculator handler (CORE FEATURE)
    │       ├── TransactionServlet.java   # Transaction management
    │       ├── GoalServlet.java          # Goal management
    │       └── DashboardServlet.java     # Dashboard aggregation
    └── webapp/
        ├── WEB-INF/
        │   └── web.xml                   # Servlet configuration
        ├── jsp/
        │   ├── login.jsp                 # Login page
        │   ├── signup.jsp                # Registration page
        │   ├── dashboard.jsp             # Main dashboard
        │   ├── calculator.jsp            # Friction calculator input
        │   ├── result.jsp                # Nudge display
        │   ├── decision.jsp              # Decision confirmation
        │   ├── transactions.jsp          # Transaction history
        │   ├── addTransaction.jsp        # Add transaction form
        │   ├── goal.jsp                  # Goal progress view
        │   ├── setGoal.jsp               # Goal setup form
        │   ├── profile.jsp               # Profile settings
        │   └── error.jsp                 # Error page
        └── index.jsp                     # Landing page
```

---

## File Descriptions

### Configuration Files

**pom.xml**  
Maven project configuration. Defines dependencies (Servlet API, JSP, JSTL, MySQL Connector) and build settings.

**sql/schema.sql**  
Database schema with 4 tables: `users`, `transactions`, `goals`, `nudge_logs`.

**WEB-INF/web.xml**  
Servlet deployment descriptor. Configures session timeout, welcome files, and error pages.

---

### Utility Layer

**DatabaseUtil.java**  
Centralized database connection management. Handles connection creation, resource cleanup, and password hashing.

---

### Model Layer (Domain Objects)

**User.java**  
Represents a user with authentication info, profile settings (hourly wage, budget), and gamification stats (streak, total saved). Contains business logic for calculating work hours from purchase amounts.

**Transaction.java**  
Represents a spending transaction with amount, category, date, and description. Includes Category enum (FOOD, ENTERTAINMENT, SHOPPING, TRANSPORT, BILLS, OTHER).

**Goal.java**  
Represents a savings goal with target amount, current progress, and deadline. Calculates progress percentage and days until deadline.

**NudgeLog.java**  
Records every friction calculator interaction including amount, work hours calculated, user decision (skip/buy), and context at decision time.

---

### Data Access Layer (DAOs)

**UserDAO.java**  
Database operations for users table. Handles user creation, authentication, profile updates, and gamification stat updates (streak, total saved).

**TransactionDAO.java**  
Database operations for transactions table. Handles CRUD operations, spending calculations, and monthly summaries by category.

**GoalDAO.java**  
Database operations for goals table. Handles goal creation/update (one goal per user), progress tracking, and completion status.

**NudgeLogDAO.java**  
Database operations for nudge_logs table. Tracks friction calculator interactions and calculates skip rate metrics (the key success measurement).

---

### Servlet Layer (Controllers)

**AuthServlet.java**  
Handles user authentication and profile management. Routes: login, signup, logout, profile settings.

**FrictionNudgeServlet.java**
Implements the friction calculator. Converts purchase amounts to work hours, generates adaptive nudge messages based on user knowledge level, and processes skip/buy decisions.

**TransactionServlet.java**  
Manages manual transaction entry and history viewing. Displays spending summaries and budget status.

**GoalServlet.java**  
Manages savings goal creation, editing, and progress tracking. Enforces one goal per user.

**DashboardServlet.java**  
Aggregates data from all DAOs to display main dashboard with statistics: streak, total saved, skip rate, goal progress, recent activity.

---

### View Layer (JSP Pages)

**login.jsp**  
User login form.

**signup.jsp**  
New user registration form with profile setup (hourly wage, budget, knowledge level).

**dashboard.jsp**  
Main dashboard showing key statistics, goal progress, recent transactions, and recent decisions.

**calculator.jsp**  
Friction calculator input form where users enter purchase amount and category.

**result.jsp**  
Displays nudge message with work hours calculation and Skip It/Buy Anyway decision buttons.

**decision.jsp**  
Confirms user decision and shows updated statistics.

**transactions.jsp**  
Transaction history with spending summary and category breakdown.

**addTransaction.jsp**  
Manual transaction entry form.

**goal.jsp**  
Goal progress display with visual progress bar and statistics.

**setGoal.jsp**  
Goal creation/editing form.

**profile.jsp**  
User profile settings (wage, budget, knowledge level, commitment messages).

**error.jsp**  
Generic error page for 404/500 errors.

**index.jsp**  
Landing page that redirects to login or dashboard.

---

## Database Schema

**users** - User accounts with authentication, settings, and gamification stats  
**transactions** - Spending records with amount, category, and date  
**goals** - Savings goals (one per user)  
**nudge_logs** - Friction calculator interaction logs for measuring effectiveness

---
