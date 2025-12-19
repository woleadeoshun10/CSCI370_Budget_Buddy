#  **Budget Buddy \- Project Documentation**

**Team:** Allan Zhang, Titivut (Jeffy) Rabaib, Adewole Adeoshun  
**Duration:** 3 Weeks (December 2025\)  
**Technology:** Java 21, Servlets, JSP, MySQL, Apache Tomcat 10.1


# **1\. ZIP File Contents**

## **1.1 Project Structure**

**Budget-Buddy.zip**

* src/main/java/edu/cs/budgetbuddy/  
*   \- model/ (User, Transaction, Goal, NudgeLog)  
*   \- dao/ (Database access objects)  
*   \- servlet/ (Request handlers)  
* sql/schema.sql (Database schema \- 4 tables)  
* pom.xml (Maven dependencies)
* Documentation (Contains all necessary documentation files)

# 

# **2\. Installation & Configuration**

## **2.1 System Requirements**

* JDK: 21.0.x or higher  
* Apache Tomcat: 10.1.33  
* MySQL: 8.0+  
* Maven: 3.9.x

## **2.2 Quick Install (5 Steps)**

**Step 1:** Install Java 21 \- Verify with: java \-version  
**Step 2:** Setup MySQL database (CREATE DATABASE budget\_buddy)  
**Step 3:** Load schema: mysql \-u db\_user\_test \-p budget\_buddy \< schema.sql  
**Step 4:** Build project: mvn clean package  
**Step 5:** Deploy WAR to Tomcat webapps folder

# 

# **3\. Deployment Guide**

## **3.1 Software Versions**

| Component | Version | Purpose |
| ----- | ----- | ----- |
| JDK | 21.0.x | Java runtime |
| Apache Tomcat | 10.1.33 | Servlet container |
| MySQL | 8.0.33 | Database |

# 

# **4\. Team Responsibilities & Honor Statement**

## **4.1 Team Breakdown**

| Team Member | Role | Key Responsibilities | Hours |
| ----- | ----- | ----- | ----- |
| Allan Zhang (Person A) | Backend Developer Auth & Core Logic | AuthServlet, FrictionNudgeServlet, Adaptive nudges | 41 hrs |
| Titivut (Jeffy) Rabaib (Person B) | Backend Developer Data Layer | Database schema, Models, DAOs, Skip rate calc | 41 hrs |
| Adewole Adeoshun (Person C) | Frontend Developer UI/UX | All JSP pages, CSS, User testing | 42 hrs |

## **4.2 Honor Statement**

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

# **5\. System Limitations**

## **5.1 Functional Limitations**

| Limitation | Impact | Reason |
| ----- | ----- | ----- |
| No Bank Integration | Manual transaction entry only | Extra hours, API costs |
| Single Goal Per User | Cannot track multiple goals | Simplifies DB design for MVP |
| No Receipt Upload | Cannot scan/process receipts | Requires OCR (out of scope) |
| Basic Password Security | SHA-256 (not BCrypt) | Academic project but could be refined to a different sign-in process |
| Incorrect Savings Usage Towards Goal | Avoided spending but not necessarily saving | Incorrectly calculating, alternative feature needs to be implemented

## **5.2 Known Issues**

* Browser Compatibility: Tested on Chrome/Firefox only  
* Mobile Layout: Basic responsive design, may need refinement    

