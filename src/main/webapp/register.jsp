<%@ page language="java" contentType="text/html; charset=UTF-8" 
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register Staff - Ocean View Resort</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<nav class="navbar">
    <div class="navbar-content">
        <div class="navbar-brand">
            <img src="images/logo.png" style="width:80px; height:auto; 
            vertical-align:middle; margin-right:12px;">
            Ocean View <span>Resort</span>
        </div>
        <ul class="navbar-menu">
            <li><a href="dashboard">Dashboard</a></li>
            <li><a href="reservation?action=add">New Reservation</a></li>
            <li><a href="reservation?action=list">All Reservations</a></li>
            <li><a href="reservation?action=search">Search</a></li>
            <li><a href="bill?action=list">Bills</a></li>
            <c:if test="${sessionScope.role == 'admin'}">
                <li><a href="reports">Reports</a></li>
                <li>
                    <a href="#">Staff 
                        <span class="dropdown-arrow">▼</span>
                    </a>
                    <ul class="dropdown-menu">
                        <li><a href="register">Register Staff</a></li>
                    </ul>
                </li>
            </c:if>
            <li><a href="changepassword">Change Password</a></li>
            <li><a href="help.jsp">Help</a></li>
            <li class="navbar-user">
                Welcome, <strong>${sessionScope.fullName}</strong>
            </li>
            <li><a href="logout" style="color: #e74c3c;">Logout</a></li>
        </ul>
    </div>
</nav>

<div class="main-content">

   <div class="page-header">
    <h2>👤 Register New Staff Member</h2>
    <p>Create login credentials for a new staff member</p>
</div>

<div style="margin-bottom: 15px;">
    <a href="staff?action=list" class="btn btn-info" style="width:auto;">
        👥 View Staff List
    </a>
</div>

<c:if test="${not empty success}">
    <div class="alert alert-success">${success}</div>
</c:if>
<c:if test="${not empty error}">
    <div class="alert alert-error">${error}</div>
</c:if>

<div class="card" style="max-width: 700px; margin: 0 auto;">
    <div class="card-header">New Staff Account Details</div>

        <form action="register" method="POST">

            <div class="form-group">
                <label for="fullName">Full Name *</label>
                <input type="text" id="fullName" name="fullName"
                       placeholder="Enter staff member full name"
                       value="${fullName}" required>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="username">Username *</label>
                    <input type="text" id="username" name="username"
                           placeholder="Choose username (min 3 chars)"
                           value="${username}" required>
                </div>
                <div class="form-group">
                    <label for="role">Role *</label>
                    <select id="role" name="role" required>
                        <option value="receptionist">Receptionist</option>
                        <option value="admin">Admin</option>
                    </select>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="password">Temporary Password *</label>
                    <input type="password" id="password" 
                           name="password"
                           placeholder="Set temporary password (min 5 chars)"
                           required>
                </div>
                <div class="form-group">
                    <label for="confirmPassword">
                        Confirm Password *
                    </label>
                    <input type="password" id="confirmPassword"
                           name="confirmPassword"
                           placeholder="Re-enter password" required>
                </div>
            </div>

            <div class="alert alert-info">
                <strong>Note:</strong> Share the username and 
                temporary password with the staff member. 
                They should change their password after first login
                using <strong>Change Password</strong> option.
            </div>

            <button type="submit" class="btn btn-success"
                    style="width: 200px;">
                Create Staff Account
            </button>
            <a href="dashboard" class="btn btn-info"
               style="margin-left: 10px;">Cancel</a>

        </form>
    </div>

</div>
</body>
</html>