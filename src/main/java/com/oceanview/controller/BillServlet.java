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
 * BillServlet - Handles bill generation, payment recording and display
 * URL: /bill
 * Actions: generate, view, list
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
        if (action == null) action = "list";

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
     * ✅ NEW: Handle payment recording (POST)
     * action=pay
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) action = "";

        if ("pay".equals(action)) {
            String billIdStr = request.getParameter("billId");
            String reservationIdStr = request.getParameter("reservationId");
            String method = request.getParameter("paymentMethod");

            try {
                int billId = Integer.parseInt(billIdStr);
                int reservationId = Integer.parseInt(reservationIdStr);

                String error = reservationService.markBillAsPaid(billId, method);

                HttpSession session = request.getSession();
                if (error != null) {
                    session.setAttribute("error", error);
                } else {
                    session.setAttribute("success",
                            "Payment recorded successfully (" + method + ").");
                }

                response.sendRedirect(request.getContextPath()
                        + "/bill?action=view&reservationId=" + reservationId);
                return;

            } catch (Exception e) {
                request.getSession().setAttribute("error",
                        "Invalid payment request.");
                response.sendRedirect(request.getContextPath()
                        + "/bill?action=list");
                return;
            }
        }

        response.sendRedirect(request.getContextPath() + "/bill?action=list");
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

                String error = reservationService.generateBill(reservationId, userId);

                if (error != null) {
                    request.setAttribute("error", error);
                } else {
                    request.setAttribute("success", "Bill generated successfully!");
                }

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

        // ✅ show session messages (after redirect from pay)
        HttpSession session = request.getSession(false);
        if (session != null) {
            String success = (String) session.getAttribute("success");
            String error = (String) session.getAttribute("error");
            if (success != null) {
                request.setAttribute("success", success);
                session.removeAttribute("success");
            }
            if (error != null) {
                request.setAttribute("error", error);
                session.removeAttribute("error");
            }
        }

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