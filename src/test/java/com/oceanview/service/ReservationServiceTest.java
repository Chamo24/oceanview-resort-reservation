package com.oceanview.service;

import com.oceanview.dao.ReservationDAO;
import com.oceanview.dao.RoomDAO;
import com.oceanview.dao.BillDAO;
import com.oceanview.model.Reservation;
import com.oceanview.model.Room;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * ReservationServiceTest - Unit tests for ReservationService
 * Uses Mockito to mock DAOs
 * No database required
 */
public class ReservationServiceTest {

    private ReservationDAO mockReservationDAO;
    private RoomDAO mockRoomDAO;
    private BillDAO mockBillDAO;
    private ReservationService reservationService;

    @Before
    public void setUp() {
        mockReservationDAO = Mockito.mock(ReservationDAO.class);
        mockRoomDAO = Mockito.mock(RoomDAO.class);
        mockBillDAO = Mockito.mock(BillDAO.class);

        reservationService = new ReservationService() {
            {
                this.reservationDAO = mockReservationDAO;
                this.roomDAO = mockRoomDAO;
                this.billDAO = mockBillDAO;
                this.validationService = new ValidationService();
            }
        };
    }

    @Test
    public void testCreateReservation_Success() {
        // Arrange
        Room mockRoom = new Room();
        mockRoom.setRoomId(1);
        mockRoom.setStatus("Available");

        when(mockRoomDAO.getRoomById(1)).thenReturn(mockRoom);
        when(mockReservationDAO.generateReservationNumber())
            .thenReturn("OVR-2025-0001");
        when(mockReservationDAO.addReservation(any(Reservation.class)))
            .thenReturn(true);

        String today = java.time.LocalDate.now().toString();
        String tomorrow = java.time.LocalDate.now()
            .plusDays(2).toString();

        // Act
        String result = reservationService.createReservation(
            "Kamal Silva",
            "45 Galle Road, Colombo",
            "0771234567",
            "kamal@example.com",
            "Double",
            1,
            today,
            tomorrow,
            1
        );

        // Assert
        assertNull(result);
        verify(mockReservationDAO, times(1))
            .addReservation(any(Reservation.class));
    }

    @Test
    public void testCreateReservation_InvalidGuestName() {
        // Act
        String result = reservationService.createReservation(
            "John123",
            "45 Galle Road",
            "0771234567",
            "john@example.com",
            "Double",
            1,
            "2025-07-15",
            "2025-07-18",
            1
        );

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Invalid guest name"));
        verify(mockReservationDAO, never())
            .addReservation(any(Reservation.class));
    }

    @Test
    public void testCreateReservation_InvalidContact() {
        // Act
        String result = reservationService.createReservation(
            "Kamal Silva",
            "45 Galle Road",
            "123",
            "kamal@example.com",
            "Double",
            1,
            "2025-07-15",
            "2025-07-18",
            1
        );

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Invalid contact number"));
    }

    @Test
    public void testCreateReservation_RoomNotAvailable() {
        // Arrange
        Room mockRoom = new Room();
        mockRoom.setRoomId(1);
        mockRoom.setStatus("Occupied");

        when(mockRoomDAO.getRoomById(1)).thenReturn(mockRoom);

        String today = java.time.LocalDate.now().toString();
        String tomorrow = java.time.LocalDate.now()
            .plusDays(2).toString();

        // Act
        String result = reservationService.createReservation(
            "Kamal Silva",
            "45 Galle Road, Colombo",
            "0771234567",
            "kamal@example.com",
            "Double",
            1,
            today,
            tomorrow,
            1
        );

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("not available"));
    }

    @Test
    public void testGetReservationByNumber_Valid() {
        // Arrange
        Reservation mockReservation = new Reservation();
        mockReservation.setReservationNumber("OVR-2025-0001");

        when(mockReservationDAO.getReservationByNumber(
            "OVR-2025-0001")).thenReturn(mockReservation);

        // Act
        Reservation result = reservationService
            .getReservationByNumber("OVR-2025-0001");

        // Assert
        assertNotNull(result);
        assertEquals("OVR-2025-0001",
            result.getReservationNumber());
    }

    @Test
    public void testGetReservationByNumber_Invalid() {
        // Act
        Reservation result = reservationService
            .getReservationByNumber("INVALID");

        // Assert
        assertNull(result);
        verify(mockReservationDAO, never())
            .getReservationByNumber(anyString());
    }
    
    @Test
    public void testMarkBillAsPaid_Success() {
        when(mockBillDAO.markBillAsPaid(1, "CASH")).thenReturn(true);

        String result = reservationService.markBillAsPaid(1, "CASH");

        assertNull(result);
        verify(mockBillDAO, times(1)).markBillAsPaid(1, "CASH");
    }
}