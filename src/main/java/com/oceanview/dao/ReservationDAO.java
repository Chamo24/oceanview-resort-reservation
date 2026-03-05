package com.oceanview.dao;

import com.oceanview.model.Reservation;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    private DBConnection dbConnection;

    public ReservationDAO() {
        this.dbConnection = DBConnection.getInstance();
    }

    public String generateReservationNumber() {
        String reservationNumber = "";
        try (Connection conn = dbConnection.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL GenerateReservationNumber(?)}")) {
            stmt.registerOutParameter(1, java.sql.Types.VARCHAR);
            stmt.execute();
            reservationNumber = stmt.getString(1);
        } catch (SQLException e) {
            System.err.println("Error generating reservation number: " + e.getMessage());
        }
        return reservationNumber;
    }

    public boolean addReservation(Reservation reservation) {
        String sql = "INSERT INTO reservations (reservation_number, guest_name, address, " +
                     "contact_number, guest_email, room_id, room_type, check_in_date, check_out_date, created_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reservation.getReservationNumber());
            stmt.setString(2, reservation.getGuestName());
            stmt.setString(3, reservation.getAddress());
            stmt.setString(4, reservation.getContactNumber());
            stmt.setString(5, reservation.getGuestEmail());
            stmt.setInt(6, reservation.getRoomId());
            stmt.setString(7, reservation.getRoomType());
            stmt.setString(8, reservation.getCheckInDate());
            stmt.setString(9, reservation.getCheckOutDate());
            stmt.setInt(10, reservation.getCreatedBy());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding reservation: " + e.getMessage());
            return false;
        }
    }

    /**
     * Backend overlap check — double booking prevent කරනවා
     * UI filter කළත් backend guarantee එකත් තියෙනවා
     */
    public boolean hasOverlappingReservation(int roomId, String checkIn, String checkOut) {
        String sql = "SELECT COUNT(*) as count FROM reservations " +
                     "WHERE room_id = ? " +
                     "AND status = 'Confirmed' " +
                     "AND check_in_date < ? " +
                     "AND check_out_date > ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            stmt.setString(2, checkOut);
            stmt.setString(3, checkIn);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking overlapping reservation: " + e.getMessage());
        }
        return false;
    }

    public Reservation getReservationByNumber(String reservationNumber) {
        Reservation reservation = null;
        try (Connection conn = dbConnection.getConnection();
             CallableStatement stmt = conn.prepareCall("{CALL GetReservationDetails(?)}")) {
            stmt.setString(1, reservationNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    reservation = extractReservation(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting reservation: " + e.getMessage());
        }
        return reservation;
    }

    public List<Reservation> getAllReservations() {
        String sql = "SELECT r.*, rm.room_number, rm.rate_per_night " +
                     "FROM reservations r JOIN rooms rm ON r.room_id = rm.room_id " +
                     "ORDER BY r.created_at DESC";
        List<Reservation> reservations = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                reservations.add(extractReservation(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all reservations: " + e.getMessage());
        }
        return reservations;
    }

    public boolean updateReservationStatus(int reservationId, String status) {
        String sql = "UPDATE reservations SET status = ? WHERE reservation_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, reservationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating reservation status: " + e.getMessage());
            return false;
        }
    }

    public Reservation getReservationById(int reservationId) {
        String sql = "SELECT r.*, rm.room_number, rm.rate_per_night " +
                     "FROM reservations r JOIN rooms rm ON r.room_id = rm.room_id " +
                     "WHERE r.reservation_id = ?";
        Reservation reservation = null;
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reservationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    reservation = extractReservation(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting reservation by ID: " + e.getMessage());
        }
        return reservation;
    }

    public int getTotalReservationCount() {
        String sql = "SELECT COUNT(*) as total FROM reservations";
        int count = 0;
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error getting reservation count: " + e.getMessage());
        }
        return count;
    }

    public int getActiveReservationCount() {
        String sql = "SELECT COUNT(*) as total FROM reservations WHERE status = 'Confirmed'";
        int count = 0;
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error getting active reservation count: " + e.getMessage());
        }
        return count;
    }

    public List<Reservation> getReservationsByDateRange(String startDate, String endDate) {
        String sql = "SELECT r.*, rm.room_number, rm.rate_per_night " +
                     "FROM reservations r " +
                     "JOIN rooms rm ON r.room_id = rm.room_id " +
                     "WHERE r.check_in_date BETWEEN ? AND ? " +
                     "ORDER BY r.check_in_date ASC";
        List<Reservation> reservations = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(extractReservation(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting reservations by date range: " + e.getMessage());
        }
        return reservations;
    }

    public double getRevenueByDateRange(String startDate, String endDate) {
        String sql = "SELECT IFNULL(SUM(b.total_amount), 0) AS total " +
                     "FROM bills b " +
                     "JOIN reservations r ON b.reservation_id = r.reservation_id " +
                     "WHERE r.check_in_date BETWEEN ? AND ?";
        double revenue = 0;
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    revenue = rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting revenue by date range: " + e.getMessage());
        }
        return revenue;
    }

    private Reservation extractReservation(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setReservationId(rs.getInt("reservation_id"));
        reservation.setReservationNumber(rs.getString("reservation_number"));
        reservation.setGuestName(rs.getString("guest_name"));
        reservation.setAddress(rs.getString("address"));
        reservation.setContactNumber(rs.getString("contact_number"));
        reservation.setRoomType(rs.getString("room_type"));
        reservation.setRoomNumber(rs.getString("room_number"));
        reservation.setCheckInDate(rs.getString("check_in_date"));
        reservation.setCheckOutDate(rs.getString("check_out_date"));
        reservation.setNumberOfNights(rs.getInt("number_of_nights"));
        reservation.setTotalCost(rs.getDouble("total_cost"));
        reservation.setStatus(rs.getString("status"));
        reservation.setRatePerNight(rs.getDouble("rate_per_night"));
        reservation.setCreatedAt(rs.getTimestamp("created_at"));
        return reservation;
    }
}