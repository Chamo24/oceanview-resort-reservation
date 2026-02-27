package com.oceanview.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * ValidationService - Handles all input validation
 * Ensures data integrity before saving to database
 * Part of the 3-Tier Architecture (Business Logic Layer)
 */
public class ValidationService {

    /**
     * Validate guest name - must not be empty, only letters and spaces allowed
     */
    public boolean isValidGuestName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return name.matches("^[a-zA-Z\\s]{2,100}$");
    }

    /**
     * Validate address - must not be empty, minimum 5 characters
     */
    public boolean isValidAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return false;
        }
        return address.trim().length() >= 5 && address.trim().length() <= 255;
    }

    /**
     * Validate Sri Lankan contact number
     * Accepts: 0771234567, 077-1234567, +94771234567
     */
    public boolean isValidContactNumber(String contact) {
        if (contact == null || contact.trim().isEmpty()) {
            return false;
        }
        return contact.matches("^(\\+94|0)?[0-9]{9,10}$");
    }

    /**
     * Validate room type - must be one of the allowed types
     */
    public boolean isValidRoomType(String roomType) {
        if (roomType == null || roomType.trim().isEmpty()) {
            return false;
        }
        return roomType.equals("Single") || roomType.equals("Double")
                || roomType.equals("Deluxe") || roomType.equals("Suite");
    }

    /**
     * Validate date format (yyyy-MM-dd)
     */
    public boolean isValidDate(String date) {
        if (date == null || date.trim().isEmpty()) {
            return false;
        }
        try {
            LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Validate check-in date is today or future date
     */
    public boolean isValidCheckInDate(String checkInDate) {
        if (!isValidDate(checkInDate)) {
            return false;
        }
        LocalDate checkIn = LocalDate.parse(checkInDate);
        LocalDate today = LocalDate.now();
        return !checkIn.isBefore(today);
    }

    /**
     * Validate check-out date is after check-in date
     */
    public boolean isValidCheckOutDate(String checkInDate, String checkOutDate) {
        if (!isValidDate(checkInDate) || !isValidDate(checkOutDate)) {
            return false;
        }
        LocalDate checkIn = LocalDate.parse(checkInDate);
        LocalDate checkOut = LocalDate.parse(checkOutDate);
        return checkOut.isAfter(checkIn);
    }

    /**
     * Validate username - minimum 3 characters, alphanumeric
     */
    public boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return username.matches("^[a-zA-Z0-9]{3,50}$");
    }

    /**
     * Validate password - minimum 5 characters
     */
    public boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        return password.length() >= 5;
    }

    /**
     * Validate reservation number format (OVR-YYYY-NNNN)
     */
    public boolean isValidReservationNumber(String reservationNumber) {
        if (reservationNumber == null || reservationNumber.trim().isEmpty()) {
            return false;
        }
        return reservationNumber.matches("^OVR-\\d{4}-\\d{4}$");
    }

    /**
     * Calculate number of nights between two dates
     */
    public int calculateNights(String checkInDate, String checkOutDate) {
        if (!isValidDate(checkInDate) || !isValidDate(checkOutDate)) {
            return 0;
        }
        LocalDate checkIn = LocalDate.parse(checkInDate);
        LocalDate checkOut = LocalDate.parse(checkOutDate);
        return (int) java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
    }
}