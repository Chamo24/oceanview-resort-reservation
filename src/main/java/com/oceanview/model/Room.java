package com.oceanview.model;

/**
 * Room Model - Represents hotel rooms
 * Maps to 'rooms' table in database
 */
public class Room {

    private int roomId;
    private String roomNumber;
    private String roomType;
    private double ratePerNight;
    private String status;
    private String description;

    // Default Constructor
    public Room() {
    }

    // Full Constructor
    public Room(int roomId, String roomNumber, String roomType, double ratePerNight, String status, String description) {
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.ratePerNight = ratePerNight;
        this.status = status;
        this.description = description;
    }

    // Getters and Setters
    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public double getRatePerNight() {
        return ratePerNight;
    }

    public void setRatePerNight(double ratePerNight) {
        this.ratePerNight = ratePerNight;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Room [roomId=" + roomId + ", roomNumber=" + roomNumber + ", roomType=" + roomType
                + ", ratePerNight=" + ratePerNight + ", status=" + status + "]";
    }
}