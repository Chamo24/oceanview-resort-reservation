package com.oceanview.service;

import com.oceanview.dao.DAOFactory;
import com.oceanview.dao.RoomDAO;
import com.oceanview.model.Room;
import java.util.List;

public class RoomService {

    protected RoomDAO roomDAO;
    protected ValidationService validationService;

    public RoomService() {
        this.roomDAO = DAOFactory.createRoomDAO();
        this.validationService = new ValidationService();
    }

    //Get all rooms
     
    public List<Room> getAllRooms() {
        return roomDAO.getAllRooms();
    }

    // Get available rooms by type with validation
     
    public List<Room> getAvailableRoomsByType(String roomType) {
        if (!validationService.isValidRoomType(roomType)) {
            return null;
        }
        return roomDAO.getAvailableRoomsByType(roomType);
    }

    //Get available rooms by type and date range with validation
  
    public List<Room> getAvailableRoomsByTypeAndDateRange(String roomType, String checkIn, String checkOut) {
        if (!validationService.isValidRoomType(roomType)) return null;
        if (!validationService.isValidDate(checkIn) || !validationService.isValidDate(checkOut)) return null;
        if (!validationService.isValidCheckOutDate(checkIn, checkOut)) return null;

        return roomDAO.getAvailableRoomsByTypeAndDateRange(roomType, checkIn, checkOut);
    }

    //Get room by ID
    
    public Room getRoomById(int roomId) {
        if (roomId <= 0) {
            return null;
        }
        return roomDAO.getRoomById(roomId);
    }

    //Get available room count by type using MySQL Function
     
    public int getAvailableRoomCount(String roomType) {
        if (!validationService.isValidRoomType(roomType)) {
            return 0;
        }
        return roomDAO.getAvailableRoomCount(roomType);
    }

    //Get all room types
     
    public List<String> getRoomTypes() {
        return roomDAO.getRoomTypes();
    }
}