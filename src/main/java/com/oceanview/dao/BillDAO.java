package com.oceanview.dao;

import com.oceanview.model.Bill;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * BillDAO - Data Access Object for Bill operations
 * Handles all database operations related to bills table
 * Uses Stored Procedure for bill calculation
 */
public class BillDAO {

    private DBConnection dbConnection;

    public BillDAO() {
        this.dbConnection = DBConnection.getInstance();
    }

    /**
     * Calculate and generate bill using Stored Procedure
     */
    public double calculateBill(int reservationId, int generatedBy) {
        double totalAmount = 0;

        try (Connection conn = dbConnection.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL CalculateBill(?, ?)}")) {

            stmt.setInt(1, reservationId);
            stmt.setInt(2, generatedBy);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    totalAmount = rs.getDouble("total_amount");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error calculating bill: " + e.getMessage());
        }

        return totalAmount;
    }

    /**
     * Get bill by reservation ID
     */
    public Bill getBillByReservationId(int reservationId) {
        String sql = "SELECT * FROM bills WHERE reservation_id = ? ORDER BY bill_date DESC LIMIT 1";
        Bill bill = null;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reservationId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    bill = extractBillFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting bill: " + e.getMessage());
        }

        return bill;
    }

    /**
     * Get all bills
     */
    public List<Bill> getAllBills() {
        String sql = "SELECT * FROM bills ORDER BY bill_date DESC";
        List<Bill> bills = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Bill bill = extractBillFromResultSet(rs);
                bills.add(bill);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all bills: " + e.getMessage());
        }

        return bills;
    }

    /**
     * Check if bill already exists for a reservation
     */
    public boolean billExists(int reservationId) {
        String sql = "SELECT COUNT(*) AS count FROM bills WHERE reservation_id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, reservationId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking bill: " + e.getMessage());
        }

        return false;
    }

    /**
     * Helper method to extract Bill from ResultSet
     */
    private Bill extractBillFromResultSet(ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setBillId(rs.getInt("bill_id"));
        bill.setReservationId(rs.getInt("reservation_id"));
        bill.setReservationNumber(rs.getString("reservation_number"));
        bill.setGuestName(rs.getString("guest_name"));
        bill.setRoomType(rs.getString("room_type"));
        bill.setRoomNumber(rs.getString("room_number"));
        bill.setCheckInDate(rs.getString("check_in_date"));
        bill.setCheckOutDate(rs.getString("check_out_date"));
        bill.setNumberOfNights(rs.getInt("number_of_nights"));
        bill.setRatePerNight(rs.getDouble("rate_per_night"));
        bill.setTotalAmount(rs.getDouble("total_amount"));
        bill.setBillDate(rs.getTimestamp("bill_date"));
        bill.setGeneratedBy(rs.getInt("generated_by"));
        return bill;
    }
    /**
     * Get total revenue from all bills
     */
    public double getTotalRevenue() {
        String sql = "SELECT IFNULL(SUM(total_amount), 0) as total_revenue FROM bills";
        double revenue = 0;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                revenue = rs.getDouble("total_revenue");
            }
        } catch (SQLException e) {
            System.err.println("Error getting total revenue: " + e.getMessage());
        }

        return revenue;
    }

    /**
     * Get revenue breakdown by room type
     */
    public List<Bill> getRevenueByRoomType() {
        String sql = "SELECT room_type, COUNT(*) as bill_count, " +
                     "SUM(number_of_nights) as total_nights, " +
                     "SUM(total_amount) as total_revenue " +
                     "FROM bills GROUP BY room_type ORDER BY total_revenue DESC";
        List<Bill> report = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Bill bill = new Bill();
                bill.setRoomType(rs.getString("room_type"));
                bill.setBillId(rs.getInt("bill_count"));
                bill.setNumberOfNights(rs.getInt("total_nights"));
                bill.setTotalAmount(rs.getDouble("total_revenue"));
                report.add(bill);
            }
        } catch (SQLException e) {
            System.err.println("Error getting revenue by room type: " + e.getMessage());
        }

        return report;
    }

    /**
     * Get total number of bills
     */
    public int getTotalBillCount() {
        String sql = "SELECT COUNT(*) as total FROM bills";
        int count = 0;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error getting bill count: " + e.getMessage());
        }

        return count;
    }
}