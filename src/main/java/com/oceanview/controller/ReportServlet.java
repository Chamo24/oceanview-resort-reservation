package com.oceanview.controller;

import com.oceanview.dao.BillDAO;
import com.oceanview.dao.ReservationDAO;
import com.oceanview.dao.RoomDAO;
import com.oceanview.model.Bill;
import com.oceanview.model.Room;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * ReportServlet - Generates management reports for decision-making
 * URL: /reports
 * Provides Occupancy Report and Revenue Report
 * These reports help management make informed decisions about
 * room pricing, staffing, and resource allocation
 */
@WebServlet("/reports")
public class ReportServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private RoomDAO roomDAO;
    private BillDAO billDAO;
    private ReservationDAO reservationDAO;

    @Override
    public void init() throws ServletException {
        roomDAO = new RoomDAO();
        billDAO = new BillDAO();
        reservationDAO = new ReservationDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // --- Occupancy Report Data ---
        List<Room> occupancyReport = roomDAO.getRoomOccupancyReport();
        int totalRooms = roomDAO.getTotalRoomCount();
        int occupiedRooms = roomDAO.getOccupiedRoomCount();
        int availableRooms = totalRooms - occupiedRooms;
        double occupancyRate = totalRooms > 0 ? ((double) occupiedRooms / totalRooms) * 100 : 0;

        request.setAttribute("occupancyReport", occupancyReport);
        request.setAttribute("totalRooms", totalRooms);
        request.setAttribute("occupiedRooms", occupiedRooms);
        request.setAttribute("availableRooms", availableRooms);
        request.setAttribute("occupancyRate", String.format("%.1f", occupancyRate));

        // --- Revenue Report Data ---
        double totalRevenue = billDAO.getTotalRevenue();
        List<Bill> revenueByType = billDAO.getRevenueByRoomType();
        int totalBills = billDAO.getTotalBillCount();

        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("revenueByType", revenueByType);
        request.setAttribute("totalBills", totalBills);

        // --- Reservation Statistics ---
        int totalReservations = reservationDAO.getTotalReservationCount();
        int activeReservations = reservationDAO.getActiveReservationCount();

        request.setAttribute("totalReservations", totalReservations);
        request.setAttribute("activeReservations", activeReservations);

        // --- Room Availability by Type ---
        int singleAvail = roomDAO.getAvailableRoomCount("Single");
        int doubleAvail = roomDAO.getAvailableRoomCount("Double");
        int deluxeAvail = roomDAO.getAvailableRoomCount("Deluxe");
        int suiteAvail = roomDAO.getAvailableRoomCount("Suite");

        request.setAttribute("singleAvail", singleAvail);
        request.setAttribute("doubleAvail", doubleAvail);
        request.setAttribute("deluxeAvail", deluxeAvail);
        request.setAttribute("suiteAvail", suiteAvail);

        request.getRequestDispatcher("/reports.jsp").forward(request, response);
    }
}
