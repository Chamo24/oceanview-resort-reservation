package com.oceanview.model;

import java.sql.Timestamp;

/**
 * User Model - Represents system users (admin, receptionist)
 * Maps to 'users' table in database
 */
public class User {

    private int userId;
    private String username;
    private String password;
    private String fullName;
    private String role;
    private Timestamp createdAt;

    // Default Constructor
    public User() {
    }

    // Parameterized Constructor - For Login
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Full Constructor
    public User(int userId, String username, String password, String fullName, String role, Timestamp createdAt) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", username=" + username + ", fullName=" + fullName + ", role=" + role + "]";
    }
}