# **Budget Buddy**

SCRUM Project Plan

3-Person Team | 2 Weeks 

# **Team Roles:**

| Person | Role | Technical Focus | Responsibilities |
| :---: | ----- | ----- | ----- |
| Person A | Developer | Backend (Auth & Core Logic) | Prioritizes backlog, defines acceptance criteria, builds authentication & friction calculator |
| Person B | Developer | Backend (Data Layer) | Facilitates ceremonies, removes blockers, builds database & DAO layer |
| Person C | Developer | Frontend (UI/UX) | Builds all JSP pages, CSS styling, testing |

# **\*Scrum Master and Product Owner Role will cycle between each sprint and will be marked.**

# **Stakeholder Requirements/Product Backlog:**

| ID | User Story |
| ----- | ----- |
| US-01 | As a user, I can create an account so I can track my spending |
| US-02 | As a user, I can log in/out so my data is secure |
| US-03 | As a user, I can enter a purchase amount and see work hours so I understand the real cost |
| US-04 | As a user, I can click Skip/Buy so my decision is recorded |
| US-05 | As a user, I can see my dashboard with stats so I know my progress |
| US-06 | As a user, I can set a savings goal so I have something to work toward |
| US-07 | As a user, I can add transactions manually so I track all spending |
| US-08 | As a user, I can see my transaction history so I review past spending |
| US-09 | As a user, I can see adaptive nudges based on my level so advice is relevant |
| US-10 | As a user, I can see my streak count so I stay motivated |
| US-11 | As a user, I can update my profile (wage, budget) so calculations are accurate |
| US-12 | As a user, I can see goal progress in nudges so I understand impact |

## **Functional Requirements:**

The following functional requirements support the user stories above:

**User Authentication**

1. System shall allow users to create an account with username and password  
2. System shall authenticate users and create a session upon successful login  
3. System shall collect hourly wage and monthly budget during profile setup

**Friction Calculator**

1. System shall provide input field for purchase amount and validate it is numeric  
2. System shall calculate work hours by dividing purchase amount by hourly wage  
3. System shall display Skip It and Buy Anyway buttons and log user decision  
4. System shall update user statistics (streak, total saved) based on decision

**Transaction Management**

1. System shall allow users to manually add transactions with amount, category, and date  
2. System shall display transaction history in reverse chronological order  
3. System shall calculate and display monthly spending total on dashboard

**Goal Management**

1. System shall allow user to create one savings goal with name and target amount  
2. System shall calculate progress as (Total Saved / Target Amount) x 100%  
3. System shall update goal progress after each Skip/Buy decision

**Dashboard**

1. System shall display current streak count and total saved amount  
2. System shall display goal progress as a percentage  
3. System shall display skip rate (skips / total decisions)

# 

# **SPRINT 1: Foundation (Week 1\) Due by Monday, December 8th at 11:59 PM latest**

**Sprint 1 Roles:**

- **Person A** (Backend/Auth-Core Logic Developer): Allan Zhang  
- **Person B** (Backend \[Data-Layer\] Developer): Titivut (Jeffy) Rabaib **Scrum Master**  
- **Person C** (Frontend Developer):Adewole Adeoshun **Product Owner**

## **Sprint Goal**

*"Users can create accounts, log in, and access a basic dashboard."*

## 

## **Sprint Backlog:**

### **US-01: User Registration**

* T-1.1: Create database schema (all 4 tables) \- Person B  
* T-1.2: Create DatabaseUtil.java \- Person B  
* T-1.3: Create User.java model \- Person B  
* T-1.4: Create UserDAO.java (create, findByUsername) \- Person B  
* T-1.5: Create AuthServlet.java (signup POST) \- Person A  
* T-1.6: Create signup.jsp \- Person C

### **US-02: User Login/Logout**

* T-2.1: Add authenticate() to UserDAO \- Person B  
* T-2.2: Create AuthServlet (login/logout) \- Person A  
* T-2.3: Create login.jsp \- Person C  
* T-2.4: Implement session management \- Person A

### **US-05: Basic Dashboard**

* T-5.1: Create DashboardServlet (basic) \- Person A  
* T-5.2: Create dashboard.jsp (placeholder stats) \- Person C  
* T-5.3: Create navigation bar component \- Person C

### **Infrastructure Tasks**

* T-0.1: Set up Maven project structure \- Person B  
* T-0.2: Configure pom.xml with dependencies \- Person B  
* T-0.3: Create base CSS stylesheet \- Person C  
* T-0.4: Create error.jsp \- Person C

## **Sprint 1 \- Task Completion Checklist (Testing)**

* User can access signup page  
* User can create account (saved to database)  
* User can log in with credentials  
* User is redirected to dashboard after login  
* User can log out  
* Session persists across page refreshes  
* All pages have consistent styling 