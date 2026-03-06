package com.oceanview.service;

import com.oceanview.dao.DAOFactory;
import com.oceanview.dao.UserDAO;
import com.oceanview.model.User;

import java.util.List;

/**
 * UserService - Business logic for User operations
 * Validates input before passing to DAO layer
 * Part of the 3-Tier Architecture (Business Logic Layer)
 */
public class UserService {

    protected UserDAO userDAO;
    protected ValidationService validationService;

    public UserService() {
        this.userDAO = DAOFactory.createUserDAO();
        this.validationService = new ValidationService();
    }

    public User login(String username, String password) {
        if (!validationService.isValidUsername(username)) {
            return null;
        }
        if (!validationService.isValidPassword(password)) {
            return null;
        }
        return userDAO.authenticateUser(username, password);
    }

    public User getUserById(int userId) {
        if (userId <= 0) {
            return null;
        }
        return userDAO.getUserById(userId);
    }

    public String register(String username, String password,
                           String fullName, String role) {

        if (!validationService.isValidUsername(username)) {
            return "Invalid username. Min 3 characters, " +
                   "letters and numbers only.";
        }

        if (!validationService.isValidPassword(password)) {
            return "Invalid password. Minimum 5 characters required.";
        }

        if (!validationService.isValidGuestName(fullName)) {
            return "Invalid name. Only letters and spaces allowed.";
        }

        if (userDAO.usernameExists(username)) {
            return "Username already exists. " +
                   "Please choose a different one.";
        }

        boolean success = userDAO.registerUser(username, password,
                                               fullName, role);
        if (!success) {
            return "Registration failed. Please try again.";
        }

        return null;
    }

    // Staff Management (Read-only) support
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }
}