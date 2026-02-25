# Ocean View Resort - Online Room Reservation System

A web-based hotel room reservation system developed for Ocean View Resort, Galle, Sri Lanka.

## Project Information

| Item | Details |
|------|---------|
| **Module** | CIS6003 - Advanced Programming |
| **University** | Cardiff Metropolitan University |
| **Student** | M.S.L.C.Wimalasinghe |
| **Year** | Final Year - Semester 1 |

## Technology Stack

| Layer | Technology |
|-------|------------|
| **Language** | Java 11 |
| **Server** | Apache Tomcat 9 |
| **Database** | MySQL 8.0 |
| **Build Tool** | Maven |
| **IDE** | Eclipse |
| **Testing** | JUnit 4 |
| **Version Control** | Git & GitHub |
| **CI/CD** | GitHub Actions |

## Architecture

- **3-Tier Architecture** (Presentation, Business Logic, Data Access)
- **Design Patterns Used:**
  - MVC (Model-View-Controller)
  - Singleton (Database Connection)
  - DAO (Data Access Object)

## Features

- User Authentication (Login/Logout)
- Add New Reservation
- Display Reservation Details
- Calculate and Print Bill
- Help Section
- Exit System

## Project Structure
OceanViewResort/
├── .github/workflows/ci.yml
├── src/
│ ├── main/
│ │ ├── java/com/oceanview/
│ │ │ ├── model/
│ │ │ ├── dao/
│ │ │ ├── service/
│ │ │ ├── controller/
│ │ │ ├── filter/
│ │ │ └── listener/
│ │ ├── resources/
│ │ └── webapp/
│ └── test/java/com/oceanview/
├── pom.xml
└── database_setup.sql

## Database Features

- **Stored Procedures:** GenerateReservationNumber, CalculateBill, GetReservationDetails
- **Functions:** GetAvailableRoomCount
- **Triggers:** after_reservation_insert, after_reservation_update, before_reservation_insert

## Setup Instructions

### Prerequisites
- Java JDK 11
- Apache Tomcat 9
- MySQL 8.0
- Maven

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/Chamo24/oceanview-resort-reservation.git

Import into Eclipse as Maven project

Configure MySQL database:

Bash

mysql -u root -p < database_setup.sql
Update database credentials in DBConnection.java

Deploy to Tomcat and run

Testing
Test-Driven Development (TDD) approach
JUnit 4 unit tests
Test automation implemented

License
This project is developed for academic purposes.
