package com.oceanview.controller;

import com.oceanview.model.Bill;
import com.oceanview.service.ReservationService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * BillServlet - Handles bill generation and display
 * URL: /bill
 * Actions: generate, view, list
 * Uses Stored Procedure for bill calculation
 */
@WebServlet("/bill")
public class BillServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ReservationService reservationService;

    @Override
    public void init() throws ServletException {
        reservationService = new ReservationService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "generate":
                generateBill(request, response);
                break;
            case "view":
                viewBill(request, response);
                break;
            case "list":
            default:
                listBills(request, response);
                break;
        }
    }

    /**
     * Generate bill for a reservation using Stored Procedure
     */
    private void generateBill(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("reservationId");
        if (idStr != null) {
            try {
                int reservationId = Integer.parseInt(idStr);
                HttpSession session = request.getSession();
                int userId = (int) session.getAttribute("userId");

                // Generate bill through Service layer
                String error = reservationService.generateBill(reservationId, userId);

                if (error != null) {
                    request.setAttribute("error", error);
                } else {
                    request.setAttribute("success", "Bill generated successfully!");
                }

                // Show the generated bill
                Bill bill = reservationService.getBillByReservationId(reservationId);
                if (bill != null) {
                    request.setAttribute("bill", bill);
                    request.getRequestDispatcher("/viewBill.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                // Invalid ID
            }
        }

        request.setAttribute("error", "Invalid reservation.");
        listBills(request, response);
    }

    /**
     * View existing bill
     */
    private void viewBill(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("reservationId");
        if (idStr != null) {
            try {
                int reservationId = Integer.parseInt(idStr);
                Bill bill = reservationService.getBillByReservationId(reservationId);
                if (bill != null) {
                    request.setAttribute("bill", bill);
                    request.getRequestDispatcher("/viewBill.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                // Invalid ID
            }
        }

        request.setAttribute("error", "Bill not found.");
        listBills(request, response);
    }

    /**
     * List all generated bills
     */
    private void listBills(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Bill> bills = reservationService.getAllBills();
        request.setAttribute("bills", bills);
        request.getRequestDispatcher("/listBills.jsp").forward(request, response);
    }
}