package com.oceanview.controller;

import com.oceanview.model.Reservation;
import com.oceanview.model.Room;
import com.oceanview.service.ReservationService;
import com.oceanview.service.RoomService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * ReservationServlet - Handles all reservation operations
 * URL: /reservation
 * Actions: add, view, search, list
 * MVC Pattern: Controller component
 */
@WebServlet("/reservation")
public class ReservationServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ReservationService reservationService;
    private RoomService roomService;

    @Override
    public void init() throws ServletException {
        reservationService = new ReservationService();
        roomService = new RoomService();
    }

    /**
     * GET - Display reservation forms and pages
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
        case "add":
            showAddForm(request, response);
            break;
        case "view":
            viewReservation(request, response);
            break;
        case "search":
            request.getRequestDispatcher("/searchReservation.jsp").forward(request, response);
            break;
        case "checkout":
            updateStatus(request, response, "Checked-Out");
            break;
        case "cancel":
            updateStatus(request, response, "Cancelled");
            break;
        case "list":
        default:
            listReservations(request, response);
            break;
    }
    }

    /**
     * POST - Process reservation form submissions
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) {
            action = "add";
        }

        switch (action) {
            case "add":
                addReservation(request, response);
                break;
            case "search":
                searchReservation(request, response);
                break;
            default:
                response.sendRedirect("reservation?action=list");
                break;
        }
    }

    /**
     * Show add reservation form with available rooms
     */
    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Room> allRooms = roomService.getAllRooms();
        List<String> roomTypes = roomService.getRoomTypes();
        request.setAttribute("allRooms", allRooms);
        request.setAttribute("roomTypes", roomTypes);
        request.getRequestDispatcher("/addReservation.jsp").forward(request, response);
    }

    /**
     * Process add reservation form
     */
    private void addReservation(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userId");

        // Get form parameters
        String guestName = request.getParameter("guestName");
        String address = request.getParameter("address");
        String contactNumber = request.getParameter("contactNumber");
        String roomType = request.getParameter("roomType");
        String roomIdStr = request.getParameter("roomId");
        String checkInDate = request.getParameter("checkInDate");
        String checkOutDate = request.getParameter("checkOutDate");

        int roomId = 0;
        try {
            roomId = Integer.parseInt(roomIdStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Please select a valid room.");
            showAddForm(request, response);
            return;
        }

        // Create reservation through Service layer (with validation)
        String error = reservationService.createReservation(guestName, address, contactNumber,
                roomType, roomId, checkInDate, checkOutDate, userId);

        if (error != null) {
            // Validation failed - show error
            request.setAttribute("error", error);
            request.setAttribute("guestName", guestName);
            request.setAttribute("address", address);
            request.setAttribute("contactNumber", contactNumber);
            request.setAttribute("roomType", roomType);
            request.setAttribute("checkInDate", checkInDate);
            request.setAttribute("checkOutDate", checkOutDate);
            showAddForm(request, response);
        } else {
            // Success - redirect to list with success message
            request.getSession().setAttribute("success", "Reservation created successfully!");
            response.sendRedirect("reservation?action=list");
        }
    }

    /**
     * View single reservation details
     */
    private void viewReservation(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        if (idStr != null) {
            try {
                int reservationId = Integer.parseInt(idStr);
                Reservation reservation = reservationService.getReservationById(reservationId);
                if (reservation != null) {
                    request.setAttribute("reservation", reservation);
                    request.getRequestDispatcher("/viewReservation.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                // Invalid ID
            }
        }

        request.setAttribute("error", "Reservation not found.");
        listReservations(request, response);
    }

    /**
     * Search reservation by reservation number
     */
    private void searchReservation(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String reservationNumber = request.getParameter("reservationNumber");

        Reservation reservation = reservationService.getReservationByNumber(reservationNumber);

        if (reservation != null) {
            request.setAttribute("reservation", reservation);
            request.getRequestDispatcher("/viewReservation.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "No reservation found with number: " + reservationNumber);
            request.getRequestDispatcher("/searchReservation.jsp").forward(request, response);
        }
    }

    /**
     * List all reservations
     */
    private void listReservations(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Reservation> reservations = reservationService.getAllReservations();
        request.setAttribute("reservations", reservations);

        // Check for success message from session
        HttpSession session = request.getSession();
        String success = (String) session.getAttribute("success");
        if (success != null) {
            request.setAttribute("success", success);
            session.removeAttribute("success");
        }

        request.getRequestDispatcher("/listReservations.jsp").forward(request, response);
    }
    /**
     * Update reservation status (Check-Out / Cancel)
     * Triggers will auto-update room status
     */
    private void updateStatus(HttpServletRequest request, HttpServletResponse response,
                              String newStatus) throws ServletException, IOException {

        String idStr = request.getParameter("id");
        if (idStr != null) {
            try {
                int reservationId = Integer.parseInt(idStr);
                boolean success = reservationService.updateReservationStatus(reservationId, newStatus);

                if (success) {
                    request.getSession().setAttribute("success",
                            "Reservation status updated to: " + newStatus);
                } else {
                    request.getSession().setAttribute("error",
                            "Failed to update reservation status.");
                }
            } catch (NumberFormatException e) {
                request.getSession().setAttribute("error", "Invalid reservation ID.");
            }
        }

        response.sendRedirect("reservation?action=list");
    }
}