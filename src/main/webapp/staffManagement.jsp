<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Staff List - Ocean View Resort</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<nav class="navbar">
    <div class="navbar-content">
        <div class="navbar-brand">
            <img src="images/logo.png" style="width:80px; height:auto; vertical-align:middle; margin-right:12px;">
            Ocean View <span>Resort</span>
        </div>
        <ul class="navbar-menu">
            <li><a href="dashboard">Dashboard</a></li>
            <li><a href="register">Register Staff</a></li>
            <li><a href="staff?action=list" class="active">Staff List</a></li>
            <li><a href="help.jsp">Help</a></li>
            <li class="navbar-user">Welcome, <strong>${sessionScope.fullName}</strong></li>
            <li><a href="logout" style="color:#e74c3c;">Logout</a></li>
        </ul>
    </div>
</nav>

<div class="main-content">

    <div class="page-header">
        <h2>👥 Staff List (Read-only)</h2>
        <p>View staff accounts registered in the system</p>
    </div>

    <div style="margin-bottom: 15px;">
        <a href="register" class="btn btn-primary" style="width:auto;">⬅ Back to Register</a>
    </div>

    <div class="card">
        <div class="card-header">Staff Accounts</div>

        <div class="table-container">
            <table>
                <thead>
                <tr>
                    <th>User ID</th>
                    <th>Username</th>
                    <th>Full Name</th>
                    <th>Role</th>
                    <th>Created At</th>
                    <th>First Login?</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${not empty users}">
                        <c:forEach var="u" items="${users}">
                            <tr>
                                <td>${u.userId}</td>
                                <td><strong>${u.username}</strong></td>
                                <td>${u.fullName}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${u.role == 'admin'}">
                                            <span class="badge badge-checked-out">ADMIN</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-confirmed">RECEPTIONIST</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>${u.createdAt}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${u.firstLogin}">Yes</c:when>
                                        <c:otherwise>No</c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="6" style="text-align:center; padding:25px; color:#95a5a6;">
                                No staff accounts found.
                            </td>
                        </tr>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>

    </div>

</div>

</body>
</html>