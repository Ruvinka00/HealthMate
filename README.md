&#x20;

\# HealthMate – Personal Health \& Wellness Tracker



\*\*HealthMate\*\* is an Android-based personal health and wellness tracking application developed as part of the Mobile Application Development coursework for the BSc (Hons) Computing Science in Software Engineering programme.



The application allows users to record, manage, and monitor their daily health activities and wellness information through a user-friendly mobile interface. It provides a centralised platform for tracking physical activity, water consumption, calorie intake, sleep, exercise, and mood.



\## Project Overview



HealthMate aims to encourage users to maintain a healthier lifestyle by helping them monitor their daily health activities and review their wellness progress over time.



The application uses Kotlin and Jetpack Compose for Android development and SQLite for local data storage. It supports individual user accounts, allowing users to manage their personal health information.



\## Main Features



\### 1. User Authentication

\- User registration with input validation.

\- User login with credential verification.

\- Password hashing before storing credentials.

\- Logout functionality.

\- Password reset interface.



\### 2. Health Dashboard

\- Displays daily health summaries.

\- Tracks daily step count.

\- Monitors calorie intake.

\- Records daily water consumption.

\- Displays activity trends and health statistics.

\- Provides an overview of daily health progress.



\### 3. Health Records Management

\- Add daily health records.

\- View previously recorded health information.

\- Update existing records.

\- Delete unwanted records.

\- Filter records by date.

\- Validate user inputs before saving.



\### 4. Wellness Tracking

The application provides additional wellness tracking features:



\*\*Sleep Tracking\*\*

\- Record bedtime and wake-up time.

\- Calculate sleep duration.

\- Select sleep quality.



\*\*Exercise Tracking\*\*

\- Record exercise activities.

\- Select activity types such as walking, running, cycling, yoga, and gym workouts.

\- Record exercise duration.

\- Add additional notes.



\*\*Mood Tracking\*\*

\- Record daily moods.

\- Select moods such as happy, good, neutral, stressed, and tired.

\- Add personal notes.



\### 5. Monthly Reports

\- View monthly health summaries.

\- Calculate average daily steps, calories, and water consumption.

\- Review recorded health activity.

\- Monitor goal consistency.

\- View sleep and exercise summaries.

\- Analyse mood records.



\### 6. Profile Management

\- View and update personal profile information.

\- Update display name.

\- Manage profile photograph.

\- Set daily health goals for steps, water, and calories.



\## Technologies Used



| Technology | Purpose |

|---|---|

| Kotlin | Main programming language |

| Android Studio | Android development environment |

| Jetpack Compose | User interface development |

| Material 3 | UI components and styling |

| SQLite | Local database management |

| SQLiteOpenHelper | Database creation and management |

| Repository Pattern | Data access organisation |

| Git | Version control |

| GitHub | Source code hosting |



\## Application Architecture



HealthMate follows a layered architecture using the Repository Pattern.



The application is organised into three main layers:



\### 1. Presentation Layer

Responsible for displaying the user interface and handling user interactions. The application screens are developed using Jetpack Compose.



\### 2. Repository Layer

The `HealthRepository.kt` class acts as an intermediary between the presentation layer and the database layer. It provides methods for accessing and managing health records and wellness information.



\### 3. Data Layer

The `HealthMateDatabase.kt` class manages the local SQLite database using Android's `SQLiteOpenHelper`. It handles database creation, authentication, data insertion, retrieval, updating, and deletion.



\### Architecture Diagram



```text

+--------------------------------------+

|          Presentation Layer          |

|                                      |

|       Jetpack Compose Screens        |

|                                      |

| Welcome | Login | Dashboard          |

| Records | Wellness | Reports         |

| Profile                               |

+------------------+-------------------+

&#x20;                  |

&#x20;                  v

+--------------------------------------+

|           Repository Layer           |

|                                      |

|          HealthRepository.kt         |

+------------------+-------------------+

&#x20;                  |

&#x20;                  v

+--------------------------------------+

|              Data Layer              |

|                                      |

|         HealthMateDatabase.kt         |

|          SQLiteOpenHelper             |

+------------------+-------------------+

&#x20;                  |

&#x20;                  v

+--------------------------------------+

|            SQLite Database           |

|                                      |

| users                                |

| health\_records                       |

| user\_profiles                        |

| sleep\_logs                           |

| exercise\_logs                        |

| mood\_logs                            |

+--------------------------------------+

```



\## Database Design



HealthMate uses a local SQLite database named `healthmate.db`.



The database contains the following main tables:



| Table | Description |

|---|---|

| `users` | Stores registered user account information |

| `health\_records` | Stores daily steps, calories, and water consumption |

| `user\_profiles` | Stores user profile information and health goals |

| `sleep\_logs` | Stores sleep records |

| `exercise\_logs` | Stores exercise activities |

| `mood\_logs` | Stores daily mood information |



Each user's health and wellness records are associated with their user account through the `user\_id` field.



\## Installation and Setup



\### Prerequisites

\- Android Studio

\- Android SDK

\- Kotlin support

\- An Android emulator or physical Android device



\### Steps to Run the Project



1\. Clone the repository:



&#x20;  ```bash

&#x20;  git clone https://github.com/YOUR-USERNAME/HealthMate.git

&#x20;  ```



2\. Open Android Studio.



3\. Select \*\*Open\*\* and choose the cloned HealthMate project folder.



4\. Allow Android Studio to complete the Gradle synchronisation.



5\. Connect an Android device or start an emulator.



6\. Click \*\*Run\*\* to build and launch the application.



\## Version Control



Git is used to manage the source code and track development progress.



GitHub is used to host the project repository and maintain version history.



The repository includes the Android application source code and project configuration files.



\## Testing



The application should be tested using different scenarios to verify its functionality, including:



\- User registration and login validation.

\- Adding new health records.

\- Updating existing health records.

\- Deleting health records.

\- Filtering records by date.

\- Saving and retrieving wellness records.

\- Calculating monthly health summaries.

\- Validating user input.



Actual test results and supporting screenshots are documented in the coursework technical report.



\## Project Limitations



\- Health measurements are entered manually by users; automatic sensor-based tracking is not implemented.

\- Password reset currently provides an interface and navigation flow; external email delivery is not integrated.

\- Google authentication is not fully integrated.

\- Health data is stored locally on the device rather than synchronised with a cloud service.



\## Future Enhancements



\- Integration with Google Fit or Health Connect.

\- Cloud-based data backup and synchronisation.

\- Email-based password recovery.

\- Notifications and daily health reminders.

\- More detailed health analytics and visual reports.

\- Integration with wearable fitness devices.



\## Academic Information



\*\*Module:\*\* Mobile Application Development  

\*\*Project:\*\* HealthMate – Personal Health \& Wellness Tracker  

\*\*Programme:\*\* BSc (Hons) Computing Science in Software Engineering  

\*\*Development Platform:\*\* Android  



\## Author



Developed as an academic coursework project.



\*\*GitHub Repository:\*\*  

https://github.com/YOUR-USERNAME/HealthMate



\---



\*HealthMate – Supporting healthier habits through everyday tracking.\*



