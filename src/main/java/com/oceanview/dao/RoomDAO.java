package com.oceanview.dao;

import com.oceanview.model.Room;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * RoomDAO - Data Access Object for Room operations
 * Handles all database operations related to rooms table
 */
public class RoomDAO {

    private DBConnection dbConnection;

    public RoomDAO() {
        this.dbConnection = DBConnection.getInstance();
    }

    /**
     * Get all rooms from database
     */
    public List<Room> getAllRooms() {
        String sql = "SELECT * FROM rooms ORDER BY room_number";
        List<Room> rooms = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Room room = extractRoomFromResultSet(rs);
                rooms.add(room);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all rooms: " + e.getMessage());
        }

        return rooms;
    }

    /**
     * Get available rooms by room type
     */
    public List<Room> getAvailableRoomsByType(String roomType) {
        String sql = "SELECT * FROM rooms WHERE room_type = ? AND status = 'Available' ORDER BY room_number";
        List<Room> rooms = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, roomType);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Room room = extractRoomFromResultSet(rs);
                    rooms.add(room);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting available rooms: " + e.getMessage());
        }

        return rooms;
    }

    /**
     * Get room by room ID
     */
    public Room getRoomById(int roomId) {
        String sql = "SELECT * FROM rooms WHERE room_id = ?";
        Room room = null;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    room = extractRoomFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting room: " + e.getMessage());
        }

        return room;
    }

    /**
     * Get available room count using MySQL Function
     */
    public int getAvailableRoomCount(String roomType) {
        String sql = "SELECT GetAvailableRoomCount(?) AS count";
        int count = 0;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, roomType);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting room count: " + e.getMessage());
        }

        return count;
    }

    /**
     * Get all distinct room types
     */
    public List<String> getRoomTypes() {
        String sql = "SELECT DISTINCT room_type FROM rooms ORDER BY room_type";
        List<String> types = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                types.add(rs.getString("room_type"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting room types: " + e.getMessage());
        }

        return types;
    }

    /**
     * Helper method to extract Room from ResultSet
     */
    private Room extractRoomFromResultSet(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setRoomId(rs.getInt("room_id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setRoomType(rs.getString("room_type"));
        room.setRatePerNight(rs.getDouble("rate_per_night"));
        room.setStatus(rs.getString("status"));
        room.setDescription(rs.getString("description"));
        return room;
    }
    /**
     * Get room occupancy summary for reports
     * Returns count of rooms by status for each type
     */
    public List<Room> getRoomOccupancyReport() {
        String sql = "SELECT room_type, status, COUNT(*) as room_count, " +
                     "SUM(rate_per_night) as total_rate FROM rooms " +
                     "GROUP BY room_type, status ORDER BY room_type";
        List<Room> report = new ArrayList<>();

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Room room = new Room();
                room.setRoomType(rs.getString("room_type"));
                room.setStatus(rs.getString("status"));
                room.setRoomId(rs.getInt("room_count"));
                room.setRatePerNight(rs.getDouble("total_rate"));
                report.add(room);
            }
        } catch (SQLException e) {
            System.err.println("Error getting occupancy report: " + e.getMessage());
        }

        return report;
    }

    /**
     * Get total room count
     */
    public int getTotalRoomCount() {
        String sql = "SELECT COUNT(*) as total FROM rooms";
        int count = 0;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error getting total room count: " + e.getMessage());
        }

        return count;
    }

    /**
     * Get occupied room count
     */
    public int getOccupiedRoomCount() {
        String sql = "SELECT COUNT(*) as total FROM rooms WHERE status = 'Occupied'";
        int count = 0;

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("Error getting occupied room count: " + e.getMessage());
        }

        return count;
    }
}