Project Overview
This is a console-based Library Management System developed in Java using JDBC (Java Database Connectivity) to interact with a MySQL database. This project was created as part of the KTU B.Tech 2024 Scheme curriculum.

✨ Features
Book Management: Add, Search, Update, Delete, and Display books. Track total and available copies.

Member Management: Add, Update, Deactivate, and Display library members.

Transaction Management: Issue and Return books, automatically calculating fines for late returns (₹10/day).

Reports & Analytics: Generate reports for Books, Members, and Transactions (All, Daily, Weekly, Monthly).

🛠️ Prerequisites
To run this project, you need to have the following installed:

Java Development Kit (JDK) 17 or higher

MySQL Server (or equivalent MySQL-compatible database)

MySQL Connector/J JAR file (The JDBC Driver)

An Integrated Development Environment (IDE) like IntelliJ IDEA or Eclipse (recommended).

⚙️ Setup and Installation

1. Database Setup
You need to create the database and the necessary tables.

A. MySQL Credentials:

First, note the database credentials used in DBConnection.java:

Database Name: library

Username: root

Password: *password*
