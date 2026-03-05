/**
 * validation.js - Client-Side Validation
 * Ocean View Resort Reservation System
 */

function validateGuestName(name) {
    var pattern = /^[a-zA-Z\s]{2,100}$/;
    return pattern.test(name);
}

function validateAddress(address) {
    return address.trim().length >= 5 &&
           address.trim().length <= 255;
}

function validateContactNumber(contact) {
    var pattern = /^(\+94|0)?[0-9]{9,10}$/;
    return pattern.test(contact.trim());
}

function validateCheckInDate(checkInDate) {
    if (!checkInDate) return false;
    var today = new Date();
    today.setHours(0, 0, 0, 0);
    var checkIn = new Date(checkInDate);
    return checkIn >= today;
}

function validateCheckOutDate(checkInDate, checkOutDate) {
    if (!checkInDate || !checkOutDate) return false;
    var checkIn  = new Date(checkInDate);
    var checkOut = new Date(checkOutDate);
    return checkOut > checkIn;
}

function calculateNights(checkInDate, checkOutDate) {
    if (!checkInDate || !checkOutDate) return 0;
    var checkIn  = new Date(checkInDate);
    var checkOut = new Date(checkOutDate);
    var diffTime = checkOut - checkIn;
    var diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays > 0 ? diffDays : 0;
}

function showError(elementId, message) {
    var errorElement = document.getElementById(elementId);
    if (errorElement) {
        errorElement.textContent = message;
        errorElement.style.display = 'block';
    }
}

function hideError(elementId) {
    var errorElement = document.getElementById(elementId);
    if (errorElement) {
        errorElement.style.display = 'none';
    }
}

document.addEventListener('DOMContentLoaded', function() {

    var guestNameInput = document.getElementById('guestName');
    if (guestNameInput) {
        guestNameInput.addEventListener('blur', function() {
            if (!validateGuestName(this.value)) {
                showError('guestNameError',
                    'Invalid name. Only letters and spaces (2-100 chars).');
                this.style.borderColor = '#e74c3c';
            } else {
                hideError('guestNameError');
                this.style.borderColor = '#27ae60';
            }
        });
    }

    var contactInput = document.getElementById('contactNumber');
    if (contactInput) {
        contactInput.addEventListener('blur', function() {
            if (!validateContactNumber(this.value)) {
                showError('contactError',
                    'Invalid number. Use format: 0771234567.');
                this.style.borderColor = '#e74c3c';
            } else {
                hideError('contactError');
                this.style.borderColor = '#27ae60';
            }
        });
    }

    var addressInput = document.getElementById('address');
    if (addressInput) {
        addressInput.addEventListener('blur', function() {
            if (!validateAddress(this.value)) {
                showError('addressError',
                    'Address must be 5-255 characters.');
                this.style.borderColor = '#e74c3c';
            } else {
                hideError('addressError');
                this.style.borderColor = '#27ae60';
            }
        });
    }

    var checkInInput = document.getElementById('checkInDate');
    if (checkInInput) {
        var today = new Date().toISOString().split('T')[0];
        checkInInput.setAttribute('min', today);

        checkInInput.addEventListener('change', function() {
            if (!validateCheckInDate(this.value)) {
                showError('checkInError',
                    'Check-in must be today or future date.');
                this.style.borderColor = '#e74c3c';
            } else {
                hideError('checkInError');
                this.style.borderColor = '#27ae60';

                var checkOutInput = document.getElementById('checkOutDate');
                if (checkOutInput) {
                    checkOutInput.setAttribute('min', this.value);
                }

                if (typeof updateCostEstimate === 'function') {
                    updateCostEstimate();
                }

               
                if (typeof loadAvailableRooms === 'function') {
                    loadAvailableRooms();
                }
            }
        });
    }

    var checkOutInput = document.getElementById('checkOutDate');
    if (checkOutInput) {
        checkOutInput.addEventListener('change', function() {
            var checkInValue = document.getElementById('checkInDate').value;
            if (!validateCheckOutDate(checkInValue, this.value)) {
                showError('checkOutError',
                    'Check-out must be after check-in.');
                this.style.borderColor = '#e74c3c';
            } else {
                hideError('checkOutError');
                this.style.borderColor = '#27ae60';

                if (typeof updateCostEstimate === 'function') {
                    updateCostEstimate();
                }

                
                if (typeof loadAvailableRooms === 'function') {
                    loadAvailableRooms();
                }
            }
        });
    }

    var reservationForm = document.getElementById('reservationForm');
    if (reservationForm) {
        reservationForm.addEventListener('submit', function(event) {
            var isValid = true;

            var guestName = document.getElementById('guestName').value;
            if (!validateGuestName(guestName)) {
                showError('guestNameError',
                    'Invalid name. Only letters and spaces.');
                isValid = false;
            }

            var contact = document.getElementById('contactNumber').value;
            if (!validateContactNumber(contact)) {
                showError('contactError', 'Invalid contact number.');
                isValid = false;
            }

            var address = document.getElementById('address').value;
            if (!validateAddress(address)) {
                showError('addressError',
                    'Address must be 5-255 characters.');
                isValid = false;
            }

            var roomId = document.getElementById('roomId').value;
            if (!roomId || roomId === '') {
                alert('Please select a room.');
                isValid = false;
            }

            var checkIn  = document.getElementById('checkInDate').value;
            var checkOut = document.getElementById('checkOutDate').value;
            if (!validateCheckInDate(checkIn)) {
                showError('checkInError', 'Invalid check-in date.');
                isValid = false;
            }
            if (!validateCheckOutDate(checkIn, checkOut)) {
                showError('checkOutError', 'Invalid check-out date.');
                isValid = false;
            }

            if (!isValid) {
                event.preventDefault();
            }
        });
    }
});