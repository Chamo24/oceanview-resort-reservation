-- ============================================
-- Ocean View Resort - Database Setup
-- Online Room Reservation System
-- Galle, Sri Lanka
-- ============================================

-- --------------------------------------------
-- Step 1: Create Database
-- --------------------------------------------
DROP DATABASE IF EXISTS oceanview_resort;
CREATE DATABASE oceanview_resort;
USE oceanview_resort;

-- --------------------------------------------
-- Step 2: Create Tables
-- --------------------------------------------

-- Users Table - Stores system login credentials
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'receptionist',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Rooms Table - Stores all room information
CREATE TABLE rooms (
    room_id INT PRIMARY KEY AUTO_INCREMENT,
    room_number VARCHAR(10) NOT NULL UNIQUE,
    room_type VARCHAR(30) NOT NULL,
    rate_per_night DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Available',
    description TEXT
);

-- Reservations Table - Stores all booking details
CREATE TABLE reservations (
    reservation_id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_number VARCHAR(20) NOT NULL UNIQUE,
    guest_name VARCHAR(100) NOT NULL,
    address VARCHAR(255) NOT NULL,
    contact_number VARCHAR(15) NOT NULL,
    room_id INT NOT NULL,
    room_type VARCHAR(30) NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    number_of_nights INT NOT NULL DEFAULT 0,
    total_cost DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'Confirmed',
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id),
    FOREIGN KEY (created_by) REFERENCES users(user_id)
);

-- Bills Table - Stores generated bills
CREATE TABLE bills (
    bill_id INT PRIMARY KEY AUTO_INCREMENT,
    reservation_id INT NOT NULL,
    reservation_number VARCHAR(20) NOT NULL,
    guest_name VARCHAR(100) NOT NULL,
    room_type VARCHAR(30) NOT NULL,
    room_number VARCHAR(10) NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    number_of_nights INT NOT NULL,
    rate_per_night DECIMAL(10,2) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    bill_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    generated_by INT,
    FOREIGN KEY (reservation_id) REFERENCES reservations(reservation_id),
    FOREIGN KEY (generated_by) REFERENCES users(user_id)
);

-- --------------------------------------------
-- Step 3: Stored Procedures
-- --------------------------------------------

-- Procedure 1: Generate Unique Reservation Number
-- Format: OVR-2025-0001, OVR-2025-0002, etc.
DELIMITER //
CREATE PROCEDURE GenerateReservationNumber(OUT new_reservation_number VARCHAR(20))
BEGIN
    DECLARE next_id INT;
    DECLARE current_year VARCHAR(4);
    
    SET current_year = YEAR(CURDATE());
    
    SELECT IFNULL(MAX(reservation_id), 0) + 1 INTO next_id FROM reservations;
    
    SET new_reservation_number = CONCAT('OVR-', current_year, '-', LPAD(next_id, 4, '0'));
END //
DELIMITER ;

-- Procedure 2: Calculate Bill for a Reservation
DELIMITER //
CREATE PROCEDURE CalculateBill(
    IN p_reservation_id INT,
    IN p_generated_by INT
)
BEGIN
    DECLARE v_reservation_number VARCHAR(20);
    DECLARE v_guest_name VARCHAR(100);
    DECLARE v_room_type VARCHAR(30);
    DECLARE v_room_number VARCHAR(10);
    DECLARE v_check_in DATE;
    DECLARE v_check_out DATE;
    DECLARE v_nights INT;
    DECLARE v_rate DECIMAL(10,2);
    DECLARE v_total DECIMAL(10,2);
    
    SELECT r.reservation_number, r.guest_name, r.room_type,
           rm.room_number, r.check_in_date, r.check_out_date,
           r.number_of_nights, rm.rate_per_night
    INTO v_reservation_number, v_guest_name, v_room_type,
         v_room_number, v_check_in, v_check_out,
         v_nights, v_rate
    FROM reservations r
    JOIN rooms rm ON r.room_id = rm.room_id
    WHERE r.reservation_id = p_reservation_id;
    
    SET v_total = v_nights * v_rate;
    
    INSERT INTO bills (reservation_id, reservation_number, guest_name,
                      room_type, room_number, check_in_date, check_out_date,
                      number_of_nights, rate_per_night, total_amount, generated_by)
    VALUES (p_reservation_id, v_reservation_number, v_guest_name,
            v_room_type, v_room_number, v_check_in, v_check_out,
            v_nights, v_rate, v_total, p_generated_by);
    
    SELECT v_total AS total_amount;
END //
DELIMITER ;

-- Procedure 3: Get Reservation Details by Reservation Number
DELIMITER //
CREATE PROCEDURE GetReservationDetails(IN p_reservation_number VARCHAR(20))
BEGIN
    SELECT r.reservation_id, r.reservation_number, r.guest_name,
           r.address, r.contact_number, r.room_type,
           rm.room_number, r.check_in_date, r.check_out_date,
           r.number_of_nights, r.total_cost, r.status,
           rm.rate_per_night, r.created_at
    FROM reservations r
    JOIN rooms rm ON r.room_id = rm.room_id
    WHERE r.reservation_number = p_reservation_number;
END //
DELIMITER ;

-- --------------------------------------------
-- Step 4: Functions
-- --------------------------------------------

-- Function: Get Available Room Count by Room Type
DELIMITER //
CREATE FUNCTION GetAvailableRoomCount(p_room_type VARCHAR(30))
RETURNS INT
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE available_count INT;
    
    SELECT COUNT(*) INTO available_count
    FROM rooms
    WHERE room_type = p_room_type AND status = 'Available';
    
    RETURN available_count;
END //
DELIMITER ;

-- --------------------------------------------
-- Step 5: Triggers
-- --------------------------------------------

-- Trigger 1: Before Insert - Auto calculate nights and total cost
DELIMITER //
CREATE TRIGGER before_reservation_insert
BEFORE INSERT ON reservations
FOR EACH ROW
BEGIN
    DECLARE v_rate DECIMAL(10,2);
    
    SET NEW.number_of_nights = DATEDIFF(NEW.check_out_date, NEW.check_in_date);
    
    SELECT rate_per_night INTO v_rate FROM rooms WHERE room_id = NEW.room_id;
    
    SET NEW.total_cost = NEW.number_of_nights * v_rate;
END //
DELIMITER ;

-- Trigger 2: After Insert - Update room status to Occupied
DELIMITER //
CREATE TRIGGER after_reservation_insert
AFTER INSERT ON reservations
FOR EACH ROW
BEGIN
    IF NEW.status = 'Confirmed' THEN
        UPDATE rooms SET status = 'Occupied' WHERE room_id = NEW.room_id;
    END IF;
END //
DELIMITER ;

-- Trigger 3: After Update - Update room status based on reservation status
DELIMITER //
CREATE TRIGGER after_reservation_update
AFTER UPDATE ON reservations
FOR EACH ROW
BEGIN
    IF NEW.status = 'Checked-Out' OR NEW.status = 'Cancelled' THEN
        UPDATE rooms SET status = 'Available' WHERE room_id = NEW.room_id;
    ELSEIF NEW.status = 'Confirmed' THEN
        UPDATE rooms SET status = 'Occupied' WHERE room_id = NEW.room_id;
    END IF;
END //
DELIMITER ;

-- --------------------------------------------
-- Step 6: Insert Sample Data
-- --------------------------------------------

-- Admin User (username: admin, password: admin123)
INSERT INTO users (username, password, full_name, role) VALUES
('admin', 'admin123', 'System Administrator', 'admin'),
('reception1', 'rec123', 'Nimal Perera', 'receptionist');

-- Rooms - Ocean View Resort Room Inventory
INSERT INTO rooms (room_number, room_type, rate_per_night, status, description) VALUES
('101', 'Single', 5000.00, 'Available', 'Standard single room with garden view'),
('102', 'Single', 5000.00, 'Available', 'Standard single room with garden view'),
('103', 'Single', 5500.00, 'Available', 'Single room with partial sea view'),
('201', 'Double', 8000.00, 'Available', 'Spacious double room with sea view'),
('202', 'Double', 8000.00, 'Available', 'Spacious double room with sea view'),
('203', 'Double', 8500.00, 'Available', 'Double room with balcony and sea view'),
('301', 'Deluxe', 12000.00, 'Available', 'Deluxe room with panoramic ocean view'),
('302', 'Deluxe', 12000.00, 'Available', 'Deluxe room with private balcony'),
('401', 'Suite', 20000.00, 'Available', 'Luxury suite with living area and ocean view'),
('402', 'Suite', 25000.00, 'Available', 'Presidential suite with jacuzzi and terrace');

-- Test Reservation
INSERT INTO reservations (reservation_number, guest_name, address, contact_number,
                         room_id, room_type, check_in_date, check_out_date, created_by)
VALUES ('OVR-2025-0001', 'Kamal Silva', '45 Galle Road, Colombo', '0771234567',
        4, 'Double', '2025-07-15', '2025-07-18', 1);

-- ============================================
-- Database Setup Complete
-- ============================================
-- Tables: users, rooms, reservations, bills
-- Stored Procedures: GenerateReservationNumber, CalculateBill, GetReservationDetails
-- Functions: GetAvailableRoomCount
-- Triggers: before_reservation_insert, after_reservation_insert, after_reservation_update
-- ============================================