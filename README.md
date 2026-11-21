
HealthMate – Personal Health and Wellness Tracker


HealthMate is an Android-based personal health and wellness tracking application developed as part of the Mobile Application Development coursework for the BSc (Hons) Computing Science in Software Engineering programme.


1.	Project Overview

1.1	Introduction

HealthMate is an Android based Personal Health and Wellness Tracker developed to help users record, manage, monitor the daily health and wellness activities through a mobile application.  This application provides a central platform where users can maintain information about the daily physical activities, calorie expenditure, water intake, sleeping habits, exercise activities and mood.
The main purpose of HealthMate is to encourage users to develop healthier daily routines by providing an organized way to record each of those users’ wellness information and to review progress.
This application was developed by using Kotin and Jetpack Compose in Android Studio. SQLite was selected as the local database management system to store the user’s information and health records directly on the Android device. 
This application uses Android SQLiteOpenHelper class to create and manage the local database. This allows users to access the saved information without internet connection or an external database server.

1.2	Problem Statement 

Maintaining a healthy lifestyle involves monitoring different aspects of everyday life such as physical activity, calorie expenditure, water intake, sleeping habits and emotional wellbeing. However, users may find it difficult to maintain these records consistently when the information is stored in different places or not recorded at all. HealthMate addresses this problem by providing a single mobile application that allows users to record and review each user’s personal health and wellness information.

1.3	Project Objectives

These are the main objectives of HealthMate are:
•	To develop an Android mobile application for recording daily health information.
•	To provide functionality for managing steps, calories burned and water intake.
•	To implement local database storage using SQLite.
•	To provide CRUD functionality for adding, view, updating, and deleting health records.
•	To provide user registration and login functionality.
•	To allow users to maintain personal wellness information including sleep, exercise, and mood records.
•	To provide a dashboard and monthly reports to help users review saved information.
•	To implement a user-friendly interface with appropriate navigation, validation and visual components.

1.4	Development Technologies 

Programming Language - Kotlin
Development Environment - Android Studio
Mobile Platform - Android
UI Framework - Jetpack Compose
UI design components - Material Design 3
Local Database - SQLite
Database Integration - SQLiteOpenHelper
Application Architecture - Layered Architecture
Data Access Pattern - Repository Pattern
State Management - Jetpack Compose State
                
1.5	Project Perspective

•	HealthMate is a standalone native Android mobile application developed to help individuals record, manage and review their daily health activities and personal wellness information.
•	The application provides a centralized environment for managing daily steps, calories burned, water intake, sleep, exercise and mood information.
•	Unlike a basic health-record application that only displays information, HealthMate allows registered users to add, view, update, delete and review their personal records through an interactive mobile interface.
•	The application is developed using Kotlin and Jetpack Compose in Android Studio. Local data is stored using SQLite through Android's SQLiteOpenHelper class.

1.6	Project Scope

•	User registration and login.
•	Separate health records for each registered user.
•	Adding, viewing, updating and deleting daily health records.
•	Filtering health records by date.
•	A dashboard showing daily steps, calories and water intake.
•	Seven-day step and water activity charts.
•	Personal health-goal settings.
•	Profile name and photograph management.
•	Sleep tracking.
•	Exercise tracking.
•	Mood tracking.
•	Monthly health and wellness reports.
•	Local data storage using SQLite.
•	Input validation and error messages.
•	User logout.


2.	Application Features

2.1	User Registration and Login

Application allows new users to create an account by filling the details asked while registered users can login using the registered details to access the dashboard.

•	If the user is new to the application, click “Get Started” and the Registration Screen will appear. Fill the details and click register. 
•	If you are an already registered user, click “I already have an account” and the Login screen will appear fill the Username and Password and click Login.
•	If the Registered user forgotten the password click the “Forgot Password?” the “Forgot Password” screen will appear. Also, User can change password through the User Profile.

2.2	Daily Health Record Management
Users can record daily steps, calories burned and water intake with the Date. Users can also view previously saved records, update existing information and delete records when necessary.

These are the main health tracking functionalities allows user to record:
•	Steps walked
•	Calories burned
•	Water Intakes 
•	Date of the health record

2.3	Dashboard

The dashboard provides a summary of the user's daily health activities. It displays steps, calories burned and water intake. It also includes progress indicators and seven-day activity charts to help users review their progress. Dashboard is separated into pages such as Records, Wellness, Monthly Report and Profile.

2.4	Search and Filter

Users can search for previously saved health records by entering a complete date or part of a date (YYYY-MM-DD / DD-MM-YYYY / YYYY-MM / Only MM / Only DD). The application will display the records that match the entered date. 

2.5	Personal Health Goals

Users can set daily step, water intake and calorie goals through the Profile section. The dashboard uses these goals to display their progress.

Example: 
•	Daily Step Goal – 8,000 steps
•	Daily Calories Goal – 500 kcal
•	Daily Water Goal – 2,000 ml

2.6	Sleep Tracking

Users can record the bedtime, wake-up time, sleep duration and sleep quality. Users can also manage saved sleep records.

Features available:
•	Sleep Date
•	Bedtime
•	Wake-up time
•	Total sleep duration
•	Sleep quality

2.7	Exercise Tracking

The application allows users to record their exercise activities, including the activity type, date, duration and additional notes. Users can manage their exercise records when required.

Features available:
•	Walking
•	Running
•	Yoga
•	Cycling 
•	Gym Activities 

2.8	Mood Tracking

Users can record daily mood and add personal notes. This feature helps users keep a record of daily feelings and wellness activities.

Features available:
•	Happy
•	Good
•	Neutral
•	Stressed
•	Tired

2.9	Monthly Health Reports

HealthMate provides monthly summaries of saved health and wellness information. Users can review their steps, calories, water intake, sleep, exercise and mood records.

The reports provide information such as:
•	Activity overview.
•	Average steps.
•	Average calories.
•	Average water.
•	Goal consistency (Logged days).
•	Daily steps this month.
•	Wellness summary.

2.10	Profile Management

Users can update their display name, select a profile photograph and manage their personal health goals. The profile information is stored in the local database.

2.11	Local Database Storage

The application uses SQLite to store user accounts, health records, profile information and wellness activities. The database is managed through Android's SQLiteOpenHelper class.


3.	Application Architecture

3.1	Selected Architecture 

The HealthMate Mobile Application follows a Layered Architecture using the Repository Pattern with the Jetpack Compose State Management. This architecture was selected to separate the responsibilities of User interfaces such as data access operation and database management. It makes the application easier to maintain, understand, test and extend. Rather than implementing a complete MVC, MVP or MVVM architecture and this application uses only three clearly separated layers such as;

•	Presentation Layer: The Presentation Layer is responsible for displaying the user interface and handling user interactions. It is implemented using Kotlin and Jetpack Compose.

These are the following screens and components:

-	Welcome and Login screens
-	Registration and Password Reset screens
-	Dashboard screen
-	Health Records interface
-	Wellness Tracking screen
-	Monthly Report screen
-	Profile management interface

Major interface files include:

MainActivity.kt	- Application entry point and screen setup 
WelcomeScreen.kt - Displays the welcome interface 
LoginScreen.kt - Provides the login interface 
SuccessScreen.kt - Displays successful registration confirmation 
ResetPasswordScreen.kt - Displays the password reset interface 
DashboardScreen.kt - Displays health summaries and manages records 
WellnessScreen.kt - Manages sleep, exercise, and mood records 
MonthlyReportScreen.kt - Displays monthly health summaries 
                                       
•	Repository Layer
-	The Repository Layer is implemented in HealthRepository.kt.
-	It acts in the middle between the Presentation Layer and the database. It provides methods for retrieving, inserting, updating, and deleting health records and wellness information.
-	This stops the user interface from directly managing SQL database operations.

•	Data Layer
-	The Data Layer is implemented in HealthMateDatabase.kt, which covers Android's SQLiteOpenHelper.
-	It manages database creation, table structures, authentication, data validation, and database operations.
-	The database stores information locally using SQLite.

3.4	State Management

HealthMate uses Jetpack Compose state-management functions to maintain and update the user interface.

These are the following Compose state functions are used:
•	remember – retains state during recompositing.
•	rememberSaveable – preserves selected UI state across configuration changes and activity recreation.
•	mutableStateOf – maintains observable state values.
•	remember with keys – refreshes data when relevant values, such as the selected user or revision counter, change.

This application also uses mutableIntStateOf and mutableLongStateOf for integer and long valued state.

Examples from created HealthMate Application:
•	MainActivity – Maintains authentication page, logged in email, user ID and form information.
•	DashboardScreen - Manages the tabs, date filter, record editing, adding records and deleting. Also, profile information.
•	WellnessScreen – Manages sleep, exercise and mood sections, form values and editing states.
•	MontlyReportScreen - Maintains the selected reporting of the month.

4. Project Structure

HealthMate/
-- app/
-- src/
-- main/
-- java/
-- AuthComponents.kt
-- DashboardScreen.kt
-- ForgotPasswordScreen.kt
-- HealthMateColors.kt
-- HealthMateDatabase.kt
-- HealthRepository.kt
-- LoginScreen.kt
-- MainActivity.kt
-- MonthlyReportScreen.kt
-- RegisterScreen.kt
-- ResetPasswordScreen.kt
-- SuccessScreen.kt
-- WelcomeScreen.kt
-- WellnessScreen.kt

-- res/

-- drawable/
-- mipmap/
-- build.gradle.kts
-- gradle/
-- build.gradle.kts
-- settings.gradle.kts
-- gradlew
-- gradlew.bat
-- README.md

5. Installation Guide

5.1 How to install APK?

1. Obtain the HealthMate APK file from the Build and generate the APK.
2. Transfer the APK file to your Android device if necessary.
3. Open the APK file using your device's file manager.
4. Allow installation from this source if Android requests permission.
5. Tap "Install" and wait until the installation is completed.
6. Open the HealthMate application from your device's app menu.

5.2 Running Through Android Studio

1. Download the HealthMate project from the GitHub repository.
2. Open Android Studio.
3. Select "Open" and choose the HealthMate project folder.
4. Wait for Gradle synchronisation to complete.
5. Connect an Android device or start an Android emulator.
6. Click the "Run" button to build and launch the application.

GitHub Repository:  
Version control system	 - Git
Repository hosting platform - GitHub
Repository name -	HealthMate
Repository URL -	https://github.com/Ruvinka00/HealthMate.git 
Main branch -	main

6. User Manual

6.1 Getting Started

Open HealthMate to access the Welcome Screen.

- Tap "Get Started" to create a new account.
- Tap "I already have an account" to access the Login Screen.

6.2 User Registration

1. Open the Registration Screen.
2. Enter your full name, email address and password.
3. Confirm your password.
4. Tap "Register" to create your account.
5. After successful registration, proceed to the Dashboard.

6.3 User Login

1. Open the Login Screen.
2. Enter your registered email address and password.
3. Tap "Login" to access your Dashboard.

If you forget your password, select "Forgot Password?" and follow the instructions on the password reset screen.

6.4 Dashboard

The Dashboard provides an overview of your daily health activities, including:

- Daily steps.
- Calories burned.
- Water intake.
- Health goal progress.
- Seven-day activity charts.

Use the dashboard navigation to access Records, Wellness, Monthly Report and Profile.

6.5 Managing Daily Health Records

1. Open the "Records" section.
2. Select the option to add a new record.
3. Enter the date, steps, calories burned and water intake.
4. Save the record.
5. View previously saved records in the Records section.
6. Select an existing record to edit or delete it when necessary.

6.6 Searching and Filtering Records

1. Open the Records section.
2. Enter a date or supported date format in the search field.
3. The application displays matching health records.
4. Clear the search field to view the records again.

6.7 Wellness Tracking

Open the "Wellness" section to manage your personal wellness information.

Sleep Tracking
- Enter the sleep date.
- Record bedtime and wake-up time.
- Enter sleep duration.
- Select sleep quality.
- Save or manage your sleep records.

Exercise Tracking
- Select the exercise activity.
- Enter the date and exercise duration.
- Add additional notes if required.
- Save or manage your exercise records.

Mood Tracking
- Select your daily mood.
- Add personal notes if necessary.
- Save or manage your mood records.

6.8 Monthly Health Reports

1. Open the "Monthly Report" section.
2. Select the required reporting month.
3. Review your monthly health summary.
4. View average steps, calories, water intake and other available wellness information.
5. Review daily activity and wellness summaries.

6.9 Profile Management

Open the "Profile" section to manage your personal information.

Users can:
- Update their display name.
- Select or change their profile photograph.
- Set daily step goals.
- Set daily water intake goals.
- Set daily calorie goals.
- Change their account password.

Tap the save option after making changes.

6.10 Logout

1. Open the User Profile section.
2. Select "Logout".
3. The current user session ends.
4. The application returns to the Welcome or Login screen.

Note: HealthMate stores account, health and wellness information locally using SQLite on the Android device.

