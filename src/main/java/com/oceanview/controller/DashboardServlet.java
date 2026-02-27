package com.oceanview.controller;

import com.oceanview.model.Reservation;
import com.oceanview.service.ReservationService;
import com.oceanview.service.RoomService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * DashboardServlet - Displays main dashboard with summary
 * URL: /dashboard
 * Shows room availability and recent reservations
 */
@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private RoomService roomService;
    private ReservationService reservationService;

    @Override
    public void init() throws ServletException {
        roomService = new RoomService();
        reservationService = new ReservationService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get room availability counts using MySQL Function
        int singleCount = roomService.getAvailableRoomCount("Single");
        int doubleCount = roomService.getAvailableRoomCount("Double");
        int deluxeCount = roomService.getAvailableRoomCount("Deluxe");
        int suiteCount = roomService.getAvailableRoomCount("Suite");

        request.setAttribute("singleCount", singleCount);
        request.setAttribute("doubleCount", doubleCount);
        request.setAttribute("deluxeCount", deluxeCount);
        request.setAttribute("suiteCount", suiteCount);

        // Get all reservations for dashboard table
        List<Reservation> reservations = reservationService.getAllReservations();
        request.setAttribute("reservations", reservations);

        request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
    }
}