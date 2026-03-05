<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Ocean View Resort</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>

<!-- Navigation Bar -->
<nav class="navbar">
    <div class="navbar-content">
        <div class="navbar-brand">
            <img src="images/logo.png"
                 style="width:80px; height:auto; vertical-align:middle; margin-right:12px;">
            Ocean View <span>Resort</span>
        </div>
        <ul class="navbar-menu">
            <li><a href="dashboard">Home</a></li>
            <li>
                <a href="#">Reservations <span class="dropdown-arrow">▼</span></a>
                <ul class="dropdown-menu">
                    <li><a href="reservation?action=add">+ New Reservation</a></li>
                    <li><a href="reservation?action=list">All Reservations</a></li>
                    <li><a href="reservation?action=search">Search</a></li>
                </ul>
            </li>
            <li><a href="bill?action=list">Bills</a></li>
            <c:if test="${sessionScope.role == 'admin'}">
                <li><a href="reports">Reports</a></li>
                <li>
                    <a href="#">Staff Management<span class="dropdown-arrow">▼</span></a>
                    <ul class="dropdown-menu">
                        <li><a href="register">Register Staff</a></li>
                    </ul>
                </li>
            </c:if>
            <li><a href="help.jsp">Help</a></li>
            <li>
                <a href="#" style="color: #f39c12;">
                    👤 My Profile <span class="dropdown-arrow">▼</span>
                </a>
                <ul class="dropdown-menu">
                    <li><a href="#">👤 ${sessionScope.fullName}</a></li>
                    <li><a href="#">📋 Role: ${sessionScope.role}</a></li>
                    <li><a href="changepassword">🔒 Change Password</a></li>
                    <li><a href="logout" style="color: #e74c3c;">🚪 Logout</a></li>
                </ul>
            </li>
        </ul>
    </div>
</nav>

<!-- Main Content -->
<div class="main-content">

    <c:if test="${sessionScope.isFirstLogin == true}">
        <div style="background: #e74c3c; color: white;
                    padding: 15px 20px; text-align: center;
                    font-size: 15px; font-weight: 600;">
            ⚠️ You are using a temporary password! Please
            <a href="changepassword" style="color: #f39c12; text-decoration: underline;">
                Change Your Password
            </a>
            immediately for security.
        </div>
    </c:if>

    <div class="page-banner">
        <h2>Welcome to Ocean View Resort</h2>
        <p>Room Reservation Management System - Galle, Sri Lanka</p>
    </div>

    <!-- Room Cards -->
    <div class="room-cards">
        <div class="room-card">
            <img src="images/single-room.jpg" alt="Single Room" class="room-card-img">
            <div class="room-card-body">
                <h3>Single Room</h3>
                <div class="room-rate">LKR 5,000 - 5,500/night</div>
                <div class="room-available" id="count-single">${singleCount}</div>
                <div class="room-label">Available</div>
            </div>
        </div>
        <div class="room-card">
            <img src="images/double-room.jpg" alt="Double Room" class="room-card-img">
            <div class="room-card-body">
                <h3>Double Room</h3>
                <div class="room-rate">LKR 8,000 - 8,500/night</div>
                <div class="room-available" id="count-double">${doubleCount}</div>
                <div class="room-label">Available</div>
            </div>
        </div>
        <div class="room-card">
            <img src="images/deluxe-room.jpg" alt="Deluxe Room" class="room-card-img">
            <div class="room-card-body">
                <h3>Deluxe Room</h3>
                <div class="room-rate">LKR 12,000/night</div>
                <div class="room-available" id="count-deluxe">${deluxeCount}</div>
                <div class="room-label">Available</div>
            </div>
        </div>
        <div class="room-card">
            <img src="images/suite-room.jpg" alt="Suite Room" class="room-card-img">
            <div class="room-card-body">
                <h3>Suite Room</h3>
                <div class="room-rate">LKR 20,000 - 25,000/night</div>
                <div class="room-available" id="count-suite">${suiteCount}</div>
                <div class="room-label">Available</div>
            </div>
        </div>
    </div>

    <!-- Quick Actions -->
    <div class="quick-actions">
        <a href="reservation?action=add" class="quick-action-btn">
            <span class="action-icon">+</span> New Reservation
        </a>
        <a href="reservation?action=search" class="quick-action-btn">
            <span class="action-icon">&#128269;</span> Search Reservation
        </a>
        <a href="bill?action=list" class="quick-action-btn">
            <span class="action-icon">&#128203;</span> View Bills
        </a>
        <a href="help.jsp" class="quick-action-btn">
            <span class="action-icon">?</span> Help Guide
        </a>
    </div>

    <!-- Dashboard Charts — FIX: charts-grid class use කළා -->
    <div class="charts-grid">
        <div class="card">
            <div class="card-header">Room Occupancy Overview</div>
            <div style="text-align: center; padding: 20px;">
                <canvas id="occupancyChart" width="300" height="300"></canvas>
            </div>
        </div>
        <div class="card">
            <div class="card-header">Room Availability by Type</div>
            <div style="text-align: center; padding: 20px;">
                <canvas id="roomTypeChart" width="300" height="300"></canvas>
            </div>
        </div>
    </div>

    <!-- Recent Reservations -->
    <div class="card">
        <div class="card-header">Recent Reservations</div>
        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th>Reservation No.</th>
                        <th>Guest Name</th>
                        <th>Room Type</th>
                        <th>Check-In</th>
                        <th>Check-Out</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty reservations}">
                            <c:forEach var="res" items="${reservations}">
                                <tr>
                                    <td><strong>${res.reservationNumber}</strong></td>
                                    <td>${res.guestName}</td>
                                    <td>${res.roomType}</td>
                                    <td>${res.checkInDate}</td>
                                    <td>${res.checkOutDate}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${res.status == 'Confirmed'}">
                                                <span class="badge badge-confirmed">${res.status}</span>
                                            </c:when>
                                            <c:when test="${res.status == 'Checked-Out'}">
                                                <span class="badge badge-checked-out">${res.status}</span>
                                            </c:when>
                                            <c:when test="${res.status == 'Cancelled'}">
                                                <span class="badge badge-cancelled">${res.status}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge">${res.status}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <a href="reservation?action=view&id=${res.reservationId}"
                                           class="btn btn-info btn-sm">View</a>
                                        <a href="bill?action=generate&reservationId=${res.reservationId}"
                                           class="btn btn-warning btn-sm">Bill</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="7" style="text-align:center; padding:30px; color:#95a5a6;">
                                    No reservations found. Click "New Reservation" to create one.
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>

</div>

<script>
    // FIX: Parameters use 
    function drawOccupancyChart(totalAvailable) {
        var canvas = document.getElementById('occupancyChart');
        var ctx = canvas.getContext('2d');

        var total = totalAvailable;
        var totalRooms = 10;
        var occupied = totalRooms - total;
        var available = total;
        var occupancyRate = totalRooms > 0 ? Math.round((occupied / totalRooms) * 100) : 0;

        ctx.clearRect(0, 0, canvas.width, canvas.height);

        ctx.beginPath();
        ctx.arc(150, 150, 100, 0, 2 * Math.PI);
        ctx.fillStyle = '#eafaf1';
        ctx.fill();
        ctx.strokeStyle = '#27ae60';
        ctx.lineWidth = 3;
        ctx.stroke();

        if (occupied > 0) {
            var occupiedAngle = (occupied / totalRooms) * 2 * Math.PI;
            ctx.beginPath();
            ctx.moveTo(150, 150);
            ctx.arc(150, 150, 100, -Math.PI/2, -Math.PI/2 + occupiedAngle);
            ctx.fillStyle = '#e74c3c';
            ctx.fill();
        }

        if (available > 0) {
            var startAngle = occupied > 0
                ? -Math.PI/2 + (occupied / totalRooms) * 2 * Math.PI
                : -Math.PI/2;
            var availableAngle = (available / totalRooms) * 2 * Math.PI;
            ctx.beginPath();
            ctx.moveTo(150, 150);
            ctx.arc(150, 150, 100, startAngle, startAngle + availableAngle);
            ctx.fillStyle = '#27ae60';
            ctx.fill();
        }

        ctx.beginPath();
        ctx.arc(150, 150, 60, 0, 2 * Math.PI);
        ctx.fillStyle = '#ffffff';
        ctx.fill();

        ctx.fillStyle = '#2c3e50';
        ctx.font = 'bold 28px Segoe UI';
        ctx.textAlign = 'center';
        ctx.fillText(occupancyRate + '%', 150, 155);
        ctx.font = '12px Segoe UI';
        ctx.fillStyle = '#7f8c8d';
        ctx.fillText('Occupancy', 150, 175);

        ctx.font = '13px Segoe UI';
        ctx.textAlign = 'left';
        ctx.fillStyle = '#27ae60';
        ctx.fillRect(60, 260, 15, 15);
        ctx.fillStyle = '#2c3e50';
        ctx.fillText('Available: ' + available, 80, 273);
        ctx.fillStyle = '#e74c3c';
        ctx.fillRect(170, 260, 15, 15);
        ctx.fillStyle = '#2c3e50';
        ctx.fillText('Occupied: ' + occupied, 190, 273);
    }

    // FIX: Parameters use කරනවා
    function drawRoomTypeChart(single, doubleRoom, deluxe, suite) {
        var canvas = document.getElementById('roomTypeChart');
        var ctx = canvas.getContext('2d');

        var data = [
            {label: 'Single', value: single,    color: '#3498db'},
            {label: 'Double', value: doubleRoom, color: '#2ecc71'},
            {label: 'Deluxe', value: deluxe,     color: '#e67e22'},
            {label: 'Suite',  value: suite,      color: '#9b59b6'}
        ];

        ctx.clearRect(0, 0, canvas.width, canvas.height);
        var barWidth  = 50;
        var gap       = 20;
        var startX    = 30;
        var maxHeight = 200;
        var baseY     = 240;

        var maxVal = 0;
        for (var i = 0; i < data.length; i++) {
            if (data[i].value > maxVal) maxVal = data[i].value;
        }
        if (maxVal === 0) maxVal = 1;

        for (var i = 0; i < data.length; i++) {
            var barHeight = (data[i].value / maxVal) * maxHeight;
            var x = startX + i * (barWidth + gap);
            var y = baseY - barHeight;

            ctx.fillStyle = 'rgba(0,0,0,0.1)';
            ctx.fillRect(x + 3, y + 3, barWidth, barHeight);
            ctx.fillStyle = data[i].color;
            ctx.fillRect(x, y, barWidth, barHeight);

            ctx.fillStyle = '#2c3e50';
            ctx.font = 'bold 16px Segoe UI';
            ctx.textAlign = 'center';
            ctx.fillText(data[i].value, x + barWidth/2, y - 8);

            ctx.font = '12px Segoe UI';
            ctx.fillStyle = '#7f8c8d';
            ctx.fillText(data[i].label, x + barWidth/2, baseY + 18);
        }

        ctx.font = '13px Segoe UI';
        ctx.fillStyle = '#7f8c8d';
        ctx.textAlign = 'center';
        ctx.fillText('Available Rooms by Type', 150, 285);
    }

    
    var singleCount = parseInt('${singleCount}') || 0;
    var doubleCount = parseInt('${doubleCount}') || 0;
    var deluxeCount = parseInt('${deluxeCount}') || 0;
    var suiteCount  = parseInt('${suiteCount}')  || 0;

    drawOccupancyChart(singleCount + doubleCount + deluxeCount + suiteCount);
    drawRoomTypeChart(singleCount, doubleCount, deluxeCount, suiteCount);

   
    function refreshDashboardStats() {
        var xhr = new XMLHttpRequest();
        xhr.open('GET', 'api/dashboard?action=rooms', true);
        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4 && xhr.status === 200) {
                var data = JSON.parse(xhr.responseText);

                document.getElementById('count-single').textContent = data.single;
                document.getElementById('count-double').textContent = data.double;
                document.getElementById('count-deluxe').textContent = data.deluxe;
                document.getElementById('count-suite').textContent  = data.suite;

                var total = data.single + data.double + data.deluxe + data.suite;
                drawOccupancyChart(total);
                drawRoomTypeChart(data.single, data.double, data.deluxe, data.suite);
            }
        };
        xhr.send();
    }

    setInterval(refreshDashboardStats, 30000);
</script>

</body>
</html>