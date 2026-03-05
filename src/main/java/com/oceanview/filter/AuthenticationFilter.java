package com.oceanview.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * AuthenticationFilter - Intercepts all requests to check user authentication
 * Implements the Filter design pattern for security
 * Redirects unauthenticated users to login page
 *
 * This filter ensures that only logged-in users can access protected pages.
 * Public resources like login page, CSS, JS, and API endpoints are excluded.
 * Role-based access control restricts admin-only pages from staff users.
 */
@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("AuthenticationFilter initialized - Ocean View Resort Security Active");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String uri = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = uri.substring(contextPath.length());

        // Allow public resources without authentication
        if (isPublicResource(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Check if user is logged in
        HttpSession session = httpRequest.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("user") != null);

        if (isLoggedIn) {
            // Check role-based access
            String role = (String) session.getAttribute("role");

            // Admin-only pages
            if (isAdminOnly(path) && !"admin".equals(role)) {
                httpResponse.sendRedirect(contextPath + "/dashboard");
                return;
            }

            chain.doFilter(request, response);
        } else {
            // User is NOT authenticated - redirect to login
            httpResponse.sendRedirect(contextPath + "/login");
        }
    }

    /**
     * Check if the requested resource is public (no login required)
     */
    private boolean isPublicResource(String path) {
    return path.equals("/login")
            || path.equals("/")
            || path.endsWith(".css")
            || path.endsWith(".js")
            || path.endsWith(".png")
            || path.endsWith(".jpg")
            || path.endsWith(".jpeg")
            || path.endsWith(".ico")
            || path.startsWith("/api/");
}

/**
     * Check if the requested page is admin-only
     */
    private boolean isAdminOnly(String path) {
        return path.equals("/reports")
                || path.equals("/register")
                || path.equals("/staff");
    }

    @Override
    public void destroy() {
        System.out.println("AuthenticationFilter destroyed");
    }
}