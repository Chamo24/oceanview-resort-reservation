package com.oceanview.service;

import com.oceanview.dao.RoomDAO;
import com.oceanview.model.Room;
import java.util.List;

/**
 * RoomService - Business logic for Room operations
 * Part of the 3-Tier Architecture (Business Logic Layer)
 */
public class RoomService {

    private RoomDAO roomDAO;
    private ValidationService validationService;

    public RoomService() {
        this.roomDAO = new RoomDAO();
        this.validationService = new ValidationService();
    }

    /**
     * Get all rooms
     */
    public List<Room> getAllRooms() {
        return roomDAO.getAllRooms();
    }

    /**
     * Get available rooms by type with validation
     */
    public List<Room> getAvailableRoomsByType(String roomType) {
        if (!validationService.isValidRoomType(roomType)) {
            return null;
        }
        return roomDAO.getAvailableRoomsByType(roomType);
    }

    /**
     * Get room by ID
     */
    public Room getRoomById(int roomId) {
        if (roomId <= 0) {
            return null;
        }
        return roomDAO.getRoomById(roomId);
    }

    /**
     * Get available room count by type using MySQL Function
     */
    public int getAvailableRoomCount(String roomType) {
        if (!validationService.isValidRoomType(roomType)) {
            return 0;
        }
        return roomDAO.getAvailableRoomCount(roomType);
    }

    /**
     * Get all room types
     */
    public List<String> getRoomTypes() {
        return roomDAO.getRoomTypes();
    }
}