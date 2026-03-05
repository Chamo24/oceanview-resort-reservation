package com.oceanview.model;

import java.sql.Timestamp;

/**
 * Bill Model - Represents generated bills for reservations
 * Maps to 'bills' table in database
 */
public class Bill {

    private int billId;
    private int reservationId;
    private String reservationNumber;
    private String guestName;
    private String roomType;
    private String roomNumber;
    private String checkInDate;
    private String checkOutDate;
    private int numberOfNights;
    private double ratePerNight;
    private double totalAmount;
    private Timestamp billDate;
    private int generatedBy;

    // ✅ Payment fields 
    private String paymentStatus;   // PAID / UNPAID
    private String paymentMethod;   // CASH / CARD
    private Timestamp paidAt;

    // Default Constructor
    public Bill() {}

    // Getters and Setters
    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public int getReservationId() { return reservationId; }
    public void setReservationId(int reservationId) { this.reservationId = reservationId; }

    public String getReservationNumber() { return reservationNumber; }
    public void setReservationNumber(String reservationNumber) { this.reservationNumber = reservationNumber; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getCheckInDate() { return checkInDate; }
    public void setCheckInDate(String checkInDate) { this.checkInDate = checkInDate; }

    public String getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(String checkOutDate) { this.checkOutDate = checkOutDate; }

    public int getNumberOfNights() { return numberOfNights; }
    public void setNumberOfNights(int numberOfNights) { this.numberOfNights = numberOfNights; }

    public double getRatePerNight() { return ratePerNight; }
    public void setRatePerNight(double ratePerNight) { this.ratePerNight = ratePerNight; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public Timestamp getBillDate() { return billDate; }
    public void setBillDate(Timestamp billDate) { this.billDate = billDate; }

    public int getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(int generatedBy) { this.generatedBy = generatedBy; }

    // ✅ Payment getters/setters 
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public Timestamp getPaidAt() { return paidAt; }
    public void setPaidAt(Timestamp paidAt) { this.paidAt = paidAt; }

    @Override
    public String toString() {
        return "Bill [billId=" + billId
                + ", reservationNumber=" + reservationNumber
                + ", guestName=" + guestName
                + ", totalAmount=" + totalAmount
                + ", paymentStatus=" + paymentStatus
                + "]";
    }
}