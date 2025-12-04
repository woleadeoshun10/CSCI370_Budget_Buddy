Requirement Type	Role	Want	So	Stakeholder Requirement Correlation
Functional	Dashboard	Visual Analytics	System shall display a monthly spending bar chart.	SR-01
Functional	Dashboard	Visual Analytics	System shall display a category breakdown using a pie chart.	SR-01
Functional	Dashboard	Visual Analytics	System shall display goal progress using a circular progress indicator.	SR-01
Functional	Dashboard	Visual Analytics	System shall display streak information visually (badge, counter, or icon).	SR-01
Functional	Dashboard	Visual Analytics	System shall update all visualizations in real time when new transactions or decisions occur.	SR-01
Functional	Bank Integration	Bank Linking	System shall allow users to securely connect external bank accounts using a third-party provider (e.g., Plaid).	SR-02
Functional	Bank Integration	Transaction Sync	System shall retrieve recent transactions from connected bank accounts.	SR-02
Functional	Bank Integration	Transaction Sync	System shall automatically import new transactions at least once every 24 hours.	SR-02
Functional	Bank Integration	Notifications	System shall notify users if bank syncing fails.	SR-02
Functional	Bank Integration	Security	System shall store bank authentication tokens securely using industry-standard encryption.	SR-02
Functional	UI/UX	Performance	System shall load all dashboard views in under 2 seconds on a standard mobile device.	SR-03
Functional	UI/UX	Navigation	System shall provide a consistent navigation bar across all screens.	SR-03
Functional	UI/UX	Usability	System shall allow users to complete core tasks (add transaction, view dashboard) in 3 taps or fewer.	SR-03
Functional	UI/UX	Responsiveness	System shall support responsive design for both mobile and desktop layouts.	SR-03
Functional	UI/UX	Accessibility	System shall use accessible color contrast and readable font sizes (WCAG compliant).	SR-03
Functional	Education	Tooltips	System shall provide optional tooltips explaining financial terms (e.g., “budget”, “streak”, “goal progress”).	SR-04
Functional	Education	Dashboard Insights	System shall display short educational messages on the dashboard (e.g., spending tips, savings advice).	SR-04
Functional	Education	Behavior Insights	System shall provide explanations when a user’s spending trends increase or decrease.	SR-04
Functional	Education	Learning Module	System shall include a “Learn” section with basic financial literacy modules.	SR-04
Functional	Profile Setup	Account Configuration	System shall collect hourly wage during initial setup.	SR-05
Functional	Profile Setup	Account Configuration	System shall collect monthly budget during initial setup.	SR-05
Functional	Profile Setup	Account Configuration	System shall save all profile data to the database.	SR-05
Functional	Registration	Authentication	System shall allow new users to create an account with a username and password.	SR-06
Functional	Registration	Authentication	System shall require passwords to be at least 8 characters.	SR-06
Functional	Login	Authentication	System shall authenticate users with username and password.	SR-06
Functional	Login	Session	System shall create a session upon successful login.	SR-06
Functional	Login	Error Handling	System shall display an error message for invalid credentials.	SR-06
Functional	Login	Navigation	System shall redirect authenticated users to the dashboard.	SR-06
Functional	Logout	Security	System shall allow users to log out.	SR-06
Functional	Logout	Navigation	System shall redirect users to the login page after logout.	SR-06
Functional	Goal Management	Goal Setup	System shall allow users to create one goal with a name and target amount.	SR-07
Functional	Goal Management	Goal Setup	System shall allow an optional deadline date for the goal.	SR-07
Functional	Goal Management	Goal Setup	System shall prevent creating multiple goals.	SR-07
Functional	Goal Management	Goal Setup	System shall save the goal to the database.	SR-07
Functional	Goal Management	Tracking	System shall calculate progress as (Total Saved ÷ Target Amount) × 100%.	SR-07
Functional	Goal Management	Tracking	System shall show current saved amount and target amount.	SR-07
Functional	Goal Management	Tracking	System shall update goal progress in real time after new transactions or decisions.	SR-07
Functional	Transaction Management	History	System shall display all user transactions in reverse chronological order.	SR-08
Functional	Transaction Management	History	System shall show transaction amount, category, and date.	SR-08
Functional	Transaction Management	History	System shall display “No transactions yet” if history is empty.	SR-08
Functional	Transaction Management	History	System shall limit the initial view to the most recent 20 transactions.	SR-08
Functional	Dashboard	Streak	System shall display the user’s current streak count.	SR-09
Functional	Dashboard	Savings	System shall calculate total saved from “Skip it” decisions.	SR-09
Functional	Dashboard	Savings	System shall display total saved amount on the dashboard.	SR-09
Functional	Dashboard	Goal Progress	System shall display current goal progress as a percentage.	SR-09
Functional	Transaction Management	Spending Total	System shall calculate total spending for the current month.	SR-09
Functional	Transaction Management	Spending Total	System shall compare monthly total against the user’s monthly budget.	SR-09
Functional	Transaction Management	Spending Total	System shall display monthly spending total on the dashboard.	SR-09
Functional	Calculator	Input	System shall provide an input field for purchase amount.	SR-10
Functional	Calculator	Input	System shall validate that input is numeric and positive.	SR-10
Functional	Calculator	Input	System shall allow optional category selection.	SR-10
Functional	Calculator	Work Hours	System shall calculate work hours by dividing purchase amount by hourly wage.	SR-10
Functional	Calculator	Work Hours	System shall display calculated work hours rounded to one decimal place.	SR-10
Functional	Calculator	Decision	System shall display “Skip it” and “Buy Anyway” buttons.	SR-10
Functional	Calculator	Logging	System shall log each decision with timestamp.	SR-10
Functional	Calculator	Logging	System shall record purchase amount, category, and user choice.	SR-10
Functional	Calculator	User Stats	System shall update user statistics after each decision.	SR-10
Functional	Transactions	Manual Entry	System shall allow users to manually add transactions with amount, category, and date.	SR-10
Functional	Transactions	Manual Entry	System shall save manual transactions with the User_ID.	SR-10
Functional	Notifications	Budget Alerts	System shall notify the user when monthly spending reaches 80% of their monthly budget.	SR-11
Functional	Notifications	Budget Alerts	System shall notify users when weekly spending exceeds their weekly limit (if configured).	SR-11
Functional	Notifications	UI Alerts	System shall display an alert banner in the dashboard when spending limits are approached.	SR-11
Functional	Notifications	Settings	System shall allow users to enable/disable budget notifications.	SR-11
Functional	Categorization	Auto-Assign	System shall automatically assign categories based on merchant name and purchase type.	SR-12
Functional	Categorization	User Overrides	System shall allow users to manually change auto-assigned categories.	SR-12
Functional	Categorization	Learning	System shall learn user preferences based on manual corrections.	SR-12
Functional	Categorization	Bank Import	System shall categorize imported bank transactions automatically.	SR-12
Functional	Goal Impact	Forecasting	System shall calculate how a potential purchase affects the user’s goal completion timeline.	SR-13
Functional	Goal Impact	Forecasting	System shall display: “This purchase will delay your goal by X days/weeks.”	SR-13
Functional	Goal Impact	Savings Impact	System shall show the updated projected savings amount after the purchase.	SR-13
Functional	Goal Impact	Pre-Decision	System shall show goal impact before the user selects “Skip it” or “Buy Anyway.”	SR-13