package com.oceanview.service;

import com.oceanview.dao.UserDAO;
import com.oceanview.model.User;

/**
 * UserService - Business logic for User operations
 * Validates input before passing to DAO layer
 * Part of the 3-Tier Architecture (Business Logic Layer)
 */
public class UserService {

    private UserDAO userDAO;
    private ValidationService validationService;

    public UserService() {
        this.userDAO = new UserDAO();
        this.validationService = new ValidationService();
    }

    /**
     * Authenticate user with validation
     * Returns User if credentials are valid, null otherwise
     */
    public User login(String username, String password) {
        // Validate inputs
        if (!validationService.isValidUsername(username)) {
            return null;
        }
        if (!validationService.isValidPassword(password)) {
            return null;
        }

        // Pass to DAO for database authentication
        return userDAO.authenticateUser(username, password);
    }

    /**
     * Get user by ID
     */
    public User getUserById(int userId) {
        if (userId <= 0) {
            return null;
        }
        return userDAO.getUserById(userId);
    }
    /**
     * Register new user with validation
     * Returns error message if failed, null if successful
     */
    public String register(String username, String password, String fullName) {
        // Validate username
        if (!validationService.isValidUsername(username)) {
            return "Invalid username. Minimum 3 characters, letters and numbers only.";
        }

        // Validate password
        if (!validationService.isValidPassword(password)) {
            return "Invalid password. Minimum 5 characters required.";
        }

        // Validate full name
        if (!validationService.isValidGuestName(fullName)) {
            return "Invalid name. Only letters and spaces allowed.";
        }

        // Check if username already exists
        if (userDAO.usernameExists(username)) {
            return "Username already exists. Please choose a different one.";
        }

        // Register user as receptionist
        boolean success = userDAO.registerUser(username, password, fullName, "receptionist");
        if (!success) {
            return "Registration failed. Please try again.";
        }

        return null;
    }
}