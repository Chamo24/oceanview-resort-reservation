<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>All Bills - Ocean View Resort</title>
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
            <li><a href="reservation?action=add">New Reservation</a></li>
            <li><a href="reservation?action=list">All Reservations</a></li>
            <li><a href="reservation?action=search">Search</a></li>
            <li><a href="bill?action=list" class="active">Bills</a></li>
            <li><a href="help.jsp">Help</a></li>
            <li class="navbar-user">Welcome, <strong>${sessionScope.fullName}</strong></li>
            <li><a href="logout" style="color: #e74c3c;">Logout</a></li>
        </ul>
    </div>
</nav>

<div class="main-content">

    <div class="page-header">
        <h2>🧾 All Generated Bills</h2>
        <p>View all bills generated for guest reservations</p>
    </div>

    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>

    <div class="card">
        <div class="card-header">Bill Records</div>

        <div class="table-container">
            <table>
                <thead>
                <tr>
                    <th>Bill No.</th>
                    <th>Reservation No.</th>
                    <th>Guest Name</th>
                    <th>Room</th>
                    <th>Type</th>
                    <th>Nights</th>
                    <th>Rate (LKR)</th>
                    <th>Total (LKR)</th>
                    <th>Payment</th>
                    <th>Method</th>
                    <th>Bill Date</th>
                    <th>Actions</th>
                </tr>
                </thead>

                <tbody>
                <c:choose>
                    <c:when test="${not empty bills}">
                        <c:forEach var="b" items="${bills}">
                            <tr>
                                <td><strong>#BILL-${b.billId}</strong></td>
                                <td>${b.reservationNumber}</td>
                                <td>${b.guestName}</td>
                                <td>${b.roomNumber}</td>
                                <td>${b.roomType}</td>
                                <td>${b.numberOfNights}</td>
                                <td>${b.ratePerNight}</td>
                                <td><strong style="color: #27ae60;">${b.totalAmount}</strong></td>

                                <td>
                                    <c:choose>
                                        <c:when test="${b.paymentStatus == 'PAID'}">
                                            <span class="badge badge-confirmed">PAID</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-cancelled">UNPAID</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>

                                <td><c:out value="${b.paymentMethod}" default="-" /></td>

                                <td>${b.billDate}</td>

                                <td>
                                    <c:url var="viewUrl" value="/bill">
                                        <c:param name="action" value="view"/>
                                        <c:param name="reservationId" value="${b.reservationId}"/>
                                    </c:url>
                                    <a href="${viewUrl}" class="btn btn-info btn-sm">View</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>

                    <c:otherwise>
                        <tr>
                            <td colspan="12" style="text-align: center; padding: 30px; color: #95a5a6;">
                                No bills generated yet. Generate a bill from the Reservations page.
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