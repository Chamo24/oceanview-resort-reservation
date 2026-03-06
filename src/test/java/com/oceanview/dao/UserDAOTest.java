package com.oceanview.dao;

import com.oceanview.model.User;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * UserDAOTest - Integration test for UserDAO
 * Tests real database connection and queries
 * Requires MySQL database running with sample data
 */
public class UserDAOTest {

    private UserDAO userDAO;

    @Before
    public void setUp() {
        userDAO = new UserDAO();
    }

   
    // @Test
    public void testAuthenticateUser_ValidCredentials() {
        User user = userDAO.authenticateUser(
            "admin", "admin123");

        assertNotNull("Admin user should exist", user);
        assertEquals("admin", user.getUsername());
        assertEquals("System Administrator",
            user.getFullName());
        assertEquals("admin", user.getRole());
    }

    @Test
    public void testAuthenticateUser_InvalidCredentials() {
        User user = userDAO.authenticateUser(
            "admin", "wrongpassword");

        assertNull("Invalid credentials should return null",
            user);
    }

    
    // @Test
    public void testUsernameExists_True() {
        boolean exists = userDAO.usernameExists("admin");

        assertTrue("Admin username should exist", exists);
    }
}
