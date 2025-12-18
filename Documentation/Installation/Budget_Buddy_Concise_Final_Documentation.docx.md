#  **Budget Buddy \- Project Documentation**

**Team:** Allan Zhang, Titivut (Jeffy) Rabaib, Adewole Adeoshun  
**Duration:** 3 Weeks (December 2025\)  
**Technology:** Java 21, Servlets, JSP, MySQL, Apache Tomcat 10.1

# **1\. Requirements & Specifications**

## **1.1 Core Functional Requirements (Verifiable)**

| ID | Requirement | Test Verification |
| ----- | ----- | ----- |
| FR-01 | User registration with unique username/email | Create account → Verify in users table |
| FR-02 | User authentication with session management | Login → Check session → Logout → Verify cleared |
| FR-03 | Calculate work hours \= amount ÷ hourly\_wage | Input $35 @ $10/hr → Verify output \= 3.5 hours |
| FR-04 | Log every Skip/Buy decision to database | Make decision → Verify entry in nudge\_logs table |
| FR-05 | Calculate skip rate \= (skips ÷ total) × 100 | 6 skips / 10 decisions → Verify 60% displayed |

# 

# **2\. Test Plan**

## **2.1 Traceability Matrix**

| Use Case | Requirements | Test Cases | Status |
| ----- | ----- | ----- | ----- |
| UC-01: Use Friction Calculator | FR-03, FR-04, FR-06 | TC-001, TC-002, TC-003 |  PASS |
| UC-02: Register Account | FR-01 | TC-004, TC-005, TC-006 |  PASS |
| UC-03: Login/Logout | FR-02 | TC-007, TC-008, TC-009 |  PASS |

## 

## **2.2 Test Results Summary**

* Unit Tests: 12/12 passed (100%)  
* Integration Tests: 8/8 passed (100%)  
* System Tests: 6/6 passed (100%)  
* User Acceptance: 5/5 users, avg skip rate 68% 


# 

# **3\. ZIP File Contents**

## **3.1 Project Structure**

**budget-buddy-sprint3.zip**

* src/main/java/edu/cs/budgetbuddy/  
*   \- model/ (User, Transaction, Goal, NudgeLog)  
*   \- dao/ (Database access objects)  
*   \- servlet/ (Request handlers)  
* sql/schema.sql (Database schema \- 4 tables)  
* pom.xml (Maven dependencies)

# 

# **4\. Installation & Configuration**

## **4.1 System Requirements**

* JDK: 21.0.x or higher  
* Apache Tomcat: 10.1.33  
* MySQL: 8.0+  
* Maven: 3.9.x

## **4.2 Quick Install (5 Steps)**

**Step 1:** Install Java 21 \- Verify with: java \-version  
**Step 2:** Setup MySQL database (CREATE DATABASE budget\_buddy)  
**Step 3:** Load schema: mysql \-u db\_user\_test \-p budget\_buddy \< schema.sql  
**Step 4:** Build project: mvn clean package  
**Step 5:** Deploy WAR to Tomcat webapps folder

# 

# **5\. Deployment Guide**

## **5.1 Software Versions**

| Component | Version | Purpose |
| ----- | ----- | ----- |
| JDK | 21.0.x | Java runtime |
| Apache Tomcat | 10.1.33 | Servlet container |
| MySQL | 8.0.33 | Database |

# 

# **6\. Team Responsibilities & Honor Statement**

## **6.1 Team Breakdown**

| Team Member | Role | Key Responsibilities | Hours |
| ----- | ----- | ----- | ----- |
| Allan Zhang (Person A) | Backend Developer Auth & Core Logic | AuthServlet, FrictionNudgeServlet, Adaptive nudges | 41 hrs |
| Titivut (Jeffy) Rabaib (Person B) | Backend Developer Data Layer | Database schema, Models, DAOs, Skip rate calc | 41 hrs |
| Adewole Adeoshun (Person C) | Frontend Developer UI/UX | All JSP pages, CSS, User testing | 42 hrs |

## **6.2 Honor Statement**

We, the undersigned members of the Budget Buddy development team, hereby certify that:

* All code, documentation, and materials submitted are our own original work  
* No plagiarism \- proper attribution of all external sources  
* Each team member contributed substantially and proportionally  
* Man-hours reported (124 total) accurately reflect actual time invested  
* Adhered to all academic integrity policies

Signature: Person A \- Allan Zhang \_\_\_\_\_\_\_\_\_\_\_\_\_\_\_ Date: \_12/17/2025\_\_\_  
Signature: Person B \- Titivut (Jeffy) Rabaib \_\_\_\_\_\_\_\_\_\_\_\_\_\_\_ Date: \_12/18/2025\_\_\_  
Signature: Person C \- Adewole Adeoshun \_\_\_\_\_\_\_\_\_\_\_\_\_\_\_ Date: \_\_\_\_\_\_\_\_\_\_\_

# 

# **7\. System Limitations**

## **7.1 Functional Limitations**

| Limitation | Impact | Reason |
| ----- | ----- | ----- |
| No Bank Integration | Manual transaction entry only | 40-50 extra hours, API costs |
| Single Goal Per User | Cannot track multiple goals | Simplifies DB design for MVP |
| No Receipt Upload | Cannot scan/process receipts | Requires OCR (out of scope) |
| Basic Password Security | SHA-256 (not BCrypt) | Acceptable for academic project |

## **7.2 Known Issues**

* Browser Compatibility: Tested on Chrome/Firefox only  
* Mobile Layout: Basic responsive design, may need refinement  
* Large Datasets: Dashboard slow with 1000+ transactions  

# 

# **8\. Project Summary**

**Status:** ✅ Complete (Sprint 3\)  
**Tests Passed:** 26/26 (100%)  
**Deployment:** Functional on localhost, AWS-ready

**Key Achievement:** Successfully validated that friction-based behavioral intervention reduces impulse spending by translating costs into work hours at the moment of temptation.
