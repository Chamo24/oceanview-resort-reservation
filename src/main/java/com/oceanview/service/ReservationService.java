package com.oceanview.service;

import com.oceanview.dao.BillDAO;
import com.oceanview.dao.DAOFactory;
import com.oceanview.dao.ReservationDAO;
import com.oceanview.dao.RoomDAO;
import com.oceanview.model.Bill;
import com.oceanview.model.Reservation;
import com.oceanview.model.Room;
import java.util.List;

public class ReservationService {

    protected ReservationDAO reservationDAO;
    protected RoomDAO roomDAO;
    protected BillDAO billDAO;
    protected ValidationService validationService;

    public ReservationService() {
        this.reservationDAO = DAOFactory.createReservationDAO();
        this.roomDAO        = DAOFactory.createRoomDAO();
        this.billDAO        = DAOFactory.createBillDAO();
        this.validationService = new ValidationService();
    }

    /**
     * Create a new reservation with full validation
     * Returns error message if validation fails, null if successful
     */
    public String createReservation(String guestName, String address, String contactNumber,
                                    String guestEmail, String roomType, int roomId,
                                    String checkInDate, String checkOutDate, int createdBy) {

        // Validate guest name
        if (!validationService.isValidGuestName(guestName)) {
            return "Invalid guest name. Only letters and spaces allowed (2-100 characters).";
        }

        // Validate address
        if (!validationService.isValidAddress(address)) {
            return "Invalid address. Minimum 5 characters required.";
        }

        // Validate contact number
        if (!validationService.isValidContactNumber(contactNumber)) {
            return "Invalid contact number. Enter a valid Sri Lankan phone number.";
        }

        // Validate room type
        if (!validationService.isValidRoomType(roomType)) {
            return "Invalid room type. Please select Single, Double, Deluxe, or Suite.";
        }

        // Validate check-in date
        if (!validationService.isValidCheckInDate(checkInDate)) {
            return "Invalid check-in date. Date must be today or a future date.";
        }

        // Validate check-out date
        if (!validationService.isValidCheckOutDate(checkInDate, checkOutDate)) {
            return "Invalid check-out date. Check-out must be after check-in date.";
        }

        // Check room exists
        Room room = roomDAO.getRoomById(roomId);
        if (room == null) {
            return "Selected room not found.";
        }
        if (!"Available".equals(room.getStatus())) {
            return "Selected room is not available. Please choose another room.";
        }

        // Step 6 — Backend overlap check (double booking prevent)
        if (reservationDAO.hasOverlappingReservation(roomId, checkInDate, checkOutDate)) {
            return "Room already booked for selected dates. Please choose different dates or another room.";
        }

        // Generate reservation number using Stored Procedure
        String reservationNumber = reservationDAO.generateReservationNumber();
        if (reservationNumber == null || reservationNumber.isEmpty()) {
            return "Error generating reservation number. Please try again.";
        }

        // Create reservation object
        Reservation reservation = new Reservation(guestName, address, contactNumber,
                roomId, roomType, checkInDate, checkOutDate, createdBy);
        reservation.setReservationNumber(reservationNumber);
        reservation.setGuestEmail(guestEmail);

        // Save to database
        boolean success = reservationDAO.addReservation(reservation);
        if (!success) {
            return "Error saving reservation. Please try again.";
        }

        return null;
    }

    /**
     * Get reservation details by reservation number
     */
    public Reservation getReservationByNumber(String reservationNumber) {
        if (!validationService.isValidReservationNumber(reservationNumber)) {
            return null;
        }
        return reservationDAO.getReservationByNumber(reservationNumber);
    }

    /**
     * Get reservation by ID
     */
    public Reservation getReservationById(int reservationId) {
        if (reservationId <= 0) {
            return null;
        }
        return reservationDAO.getReservationById(reservationId);
    }

    /**
     * Get all reservations
     */
    public List<Reservation> getAllReservations() {
        return reservationDAO.getAllReservations();
    }

    /**
     * Generate bill for a reservation using Stored Procedure
     */
    public String generateBill(int reservationId, int generatedBy) {
        if (reservationId <= 0) {
            return "Invalid reservation ID.";
        }

        Reservation reservation = reservationDAO.getReservationById(reservationId);
        if (reservation == null) {
            return "Reservation not found.";
        }

        if (billDAO.billExists(reservationId)) {
            return "Bill already generated for this reservation.";
        }

        double total = billDAO.calculateBill(reservationId, generatedBy);
        if (total <= 0) {
            return "Error generating bill. Please try again.";
        }

        return null;
    }

    /**
     * Get bill for a reservation
     */
    public Bill getBillByReservationId(int reservationId) {
        if (reservationId <= 0) {
            return null;
        }
        return billDAO.getBillByReservationId(reservationId);
    }

    /**
     * Get all bills
     */
    public List<Bill> getAllBills() {
        return billDAO.getAllBills();
    }
    /**
     * Record bill payment (PAID/UNPAID + CASH/CARD)
     * Returns error message if failed, null if successful
     */
    public String markBillAsPaid(int billId, String method) {

        if (billId <= 0) {
            return "Invalid bill ID.";
        }

        if (method == null || method.trim().isEmpty()) {
            return "Please select a payment method.";
        }

        method = method.trim().toUpperCase();
        if (!method.equals("CASH") && !method.equals("CARD")) {
            return "Invalid payment method.";
        }

        boolean ok = billDAO.markBillAsPaid(billId, method);
        if (!ok) {
            return "Payment update failed or bill already paid.";
        }

        return null; // success
    }
    
    /**
     * Update reservation status
     */
    public boolean updateReservationStatus(int reservationId, String status) {
        if (reservationId <= 0) {
            return false;
        }
        if (status == null || status.trim().isEmpty()) {
            return false;
        }
        return reservationDAO.updateReservationStatus(reservationId, status);
    }

    public List<Reservation> getReservationsByDateRange(String startDate, String endDate) {
        if (startDate == null || startDate.trim().isEmpty()) {
            return new java.util.ArrayList<>();
        }
        if (endDate == null || endDate.trim().isEmpty()) {
            return new java.util.ArrayList<>();
        }
        if (endDate.compareTo(startDate) < 0) {
            return new java.util.ArrayList<>();
        }
        return reservationDAO.getReservationsByDateRange(startDate, endDate);
    }

    public double getRevenueByDateRange(String startDate, String endDate) {
        if (startDate == null || startDate.trim().isEmpty()) {
            return 0;
        }
        if (endDate == null || endDate.trim().isEmpty()) {
            return 0;
        }
        return reservationDAO.getRevenueByDateRange(startDate, endDate);
    }
}