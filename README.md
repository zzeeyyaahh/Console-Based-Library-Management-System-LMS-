

# 📚 Library Management System (LMS)

## Project Overview

This is a **console-based Library Management System (LMS)** developed in **Java** using **JDBC (Java Database Connectivity)** to interact with a **MySQL** database. This project was created as part of the *KTU B.Tech 2024 Scheme curriculum*.

The system provides a robust, menu-driven interface for managing the core operations of a college library.

-----

## ✨ Key Features

| Feature Area | Functionality |
| :--- | :--- |
| **Book Management** | Add, Search, Update, Delete, and Display books. Crucially, it tracks **total copies** and **available copies**. |
| **Member Management** | Add new patrons, Update information, **Deactivate** membership, and Display members. |
| **Transaction Management** | Handles the **Issue** and **Return** of books, automatically calculating fines for late returns (₹10/day). |
| **Reports & Analytics** | Generate essential administrative reports for Books, Members, and Transactions (filterable by All, Daily, Weekly, Monthly). |

-----

## 🛠️ Prerequisites

To successfully compile and run this project, you must have the following installed:

  * **Java Development Kit (JDK) 17** or higher.
  * **MySQL Server** (or equivalent MySQL-compatible database).
  * **MySQL Connector/J JAR file** (The required JDBC Driver).
  * An Integrated Development Environment (IDE) like **IntelliJ IDEA** or **Eclipse** is *highly recommended*.

-----

## ⚙️ Setup and Installation

### 1\. Database Setup

You must create the database schema before running the Java application.

#### A. MySQL Credentials

Note the default database credentials used in the `DBConnection.java` file:

  * **Database Name:** `library`
  * **Username:** `root`
  * **Password:** `password` *(Change this in your `DBConnection.java` file if your local MySQL configuration is different.)*

#### B. SQL Commands (Schema Creation)

Use a MySQL client (Workbench, CLI, etc.) to execute the following SQL commands to create the database and tables:

```sql
-- 1. Create the Database
CREATE DATABASE IF NOT EXISTS library;
USE library;

-- 2. Create the Books Table
CREATE TABLE Books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(13) UNIQUE NOT NULL,
    category VARCHAR(100),
    total_copies INT DEFAULT 1,
    available_copies INT DEFAULT 1
);

-- 3. Create the Members Table
CREATE TABLE Members (
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    member_type VARCHAR(50), -- e.g., student, teacher, staff
    contact_info VARCHAR(255),
    status VARCHAR(10) DEFAULT 'active' -- active or inactive
);

-- 4. Create the Transactions Table
CREATE TABLE Transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    member_id INT NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    fine INT DEFAULT 0,
    FOREIGN KEY (book_id) REFERENCES Books(book_id),
    FOREIGN KEY (member_id) REFERENCES Members(member_id)
);
```

### 2\. Project Setup (Java)

1.  **Clone the Repository:**
    ```bash
    git clone [Your-Repo-URL]
    cd [Your-Project-Folder]
    ```
2.  **Add JDBC Driver:**
      * Download the **MySQL Connector/J JAR** file.
      * In your IDE (IntelliJ/Eclipse), **add this JAR file to the project's build path** (as an external library/dependency). This establishes the crucial link for your Java application to communicate with MySQL.

### 3\. Compiling and Running

You can compile and run the project either via your IDE or using the command line.

#### Command Line Execution

If running via the terminal, you must include the JDBC JAR in the classpath. Assume `[PATH_TO_JDBC_JAR]` is the path to your downloaded file (e.g., `mysql-connector-j-8.0.32.jar`).

**A. Compile all files:**

```bash
# Use a semicolon (;) for Windows paths
javac -cp ".;[PATH_TO_JDBC_JAR]" *.java
```

**B. Run the application:**

```bash
# Use a semicolon (;) for Windows paths
java -cp ".;[PATH_TO_JDBC_JAR]" LibrarySystem
```

-----

## 💻 Running the Application

Once successfully launched, the console will display the main menu:

```
===== Library Management System =====
1. Book Management
2. Member Management
3. Transaction Management
4. Reports & Analytics
5. Exit
Enter your choice:
```


      
