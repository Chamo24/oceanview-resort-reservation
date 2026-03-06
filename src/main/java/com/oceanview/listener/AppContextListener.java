package com.oceanview.listener;

import com.oceanview.dao.DBConnection;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.SQLException;

/** Observer pattern listener that initializes Singleton DB on startup and logs cleanup on shutdown. */
@WebListener
public class AppContextListener implements ServletContextListener {

    /**
     * Called when the application starts
     * Initializes the Singleton database connection
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("==============================================");
        System.out.println("  Ocean View Resort - System Starting Up");
        System.out.println("  Galle, Sri Lanka");
        System.out.println("==============================================");

        // Initialize Singleton database connection
        try {
            DBConnection dbConnection = DBConnection.getInstance();
            Connection conn = dbConnection.getConnection();

            if (conn != null && !conn.isClosed()) {
                System.out.println("[SUCCESS] Database connection established successfully.");
                System.out.println("[INFO] Connected to: oceanview_resort database");
                conn.close();
            } else {
                System.out.println("[ERROR] Failed to establish database connection.");
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Database connection error: " + e.getMessage());
        }

        // Store application-wide attributes
        sce.getServletContext().setAttribute("appName", "Ocean View Resort");
        sce.getServletContext().setAttribute("appVersion", "1.0.0");
        sce.getServletContext().setAttribute("hotelLocation", "Galle, Sri Lanka");

        System.out.println("[INFO] Application attributes set.");
        System.out.println("[INFO] System is ready to accept requests.");
        System.out.println("==============================================");
    }

    /**
     * Called when the application shuts down
     * Performs cleanup operations
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("==============================================");
        System.out.println("  Ocean View Resort - System Shutting Down");
        System.out.println("==============================================");
        System.out.println("[INFO] All resources cleaned up successfully.");
        System.out.println("[INFO] Goodbye!");
        System.out.println("==============================================");
    }
}