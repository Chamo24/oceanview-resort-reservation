package com.oceanview.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection - Singleton Design Pattern
 * Ensures only ONE database connection instance exists throughout the application.
 * This prevents resource wastage and connection conflicts.
 */
public class DBConnection {

    // Singleton instance - only one object created
    private static DBConnection instance;

    // Database configuration
    private static final String URL = "jdbc:mysql://localhost:3306/oceanview_resort";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";  // Change this to your MySQL password

    // Private constructor - prevents external instantiation
    private DBConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        }
    }

    /**
     * Get the single instance of DBConnection (Singleton)
     * synchronized ensures thread safety
     */
    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    /**
     * Get a new database connection
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}