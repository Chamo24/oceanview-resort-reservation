/**
 * reservation.js - Room Loading and Cost Estimation
 * Ocean View Resort Reservation System
 * REST API Web Service AJAX calls
 */

function loadAvailableRooms() {
    var roomType    = document.getElementById('roomType').value;
    var roomSelect  = document.getElementById('roomId');

    roomSelect.innerHTML =
        '<option value="">-- Loading rooms... --</option>';

    if (!roomType || roomType === '') {
        roomSelect.innerHTML =
            '<option value="">-- Select Room Type First --</option>';
        return;
    }

    // checkIn/checkOut dates 
    var checkInDate  = document.getElementById('checkInDate').value;
    var checkOutDate = document.getElementById('checkOutDate').value;

    // Base URL
    var url = 'api/rooms?type=' + encodeURIComponent(roomType);

    
    if (checkInDate && checkOutDate &&
            calculateNights(checkInDate, checkOutDate) > 0) {
        url += '&checkIn='  + encodeURIComponent(checkInDate) +
               '&checkOut=' + encodeURIComponent(checkOutDate);
    }

    // AJAX call to REST API Web Service (RoomApiServlet)
    var xhr = new XMLHttpRequest();
    xhr.open('GET', url, true);

    xhr.onreadystatechange = function() {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                var rooms = JSON.parse(xhr.responseText);

                if (rooms.length === 0) {
                    roomSelect.innerHTML =
                        '<option value="">-- No rooms available --</option>';
                } else {
                    roomSelect.innerHTML =
                        '<option value="">-- Select a Room --</option>';
                    for (var i = 0; i < rooms.length; i++) {
                        var option = document.createElement('option');
                        option.value = rooms[i].roomId;
                        option.textContent = 'Room ' +
                            rooms[i].roomNumber +
                            ' - LKR ' +
                            rooms[i].ratePerNight.toLocaleString() +
                            '/night (' + rooms[i].description + ')';
                        option.setAttribute('data-rate',
                            rooms[i].ratePerNight);
                        roomSelect.appendChild(option);
                    }
                }
            } else {
                roomSelect.innerHTML =
                    '<option value="">-- Error loading rooms --</option>';
            }
            updateCostEstimate();
        }
    };

    xhr.send();
}

function updateCostEstimate() {
    var checkInDate  = document.getElementById('checkInDate').value;
    var checkOutDate = document.getElementById('checkOutDate').value;
    var roomSelect   = document.getElementById('roomId');
    var costEstimate = document.getElementById('costEstimate');
    var nightsDisplay = document.getElementById('nightsDisplay');
    var costDisplay   = document.getElementById('costDisplay');

    if (!checkInDate || !checkOutDate || !roomSelect.value) {
        costEstimate.style.display = 'none';
        return;
    }

    var nights = calculateNights(checkInDate, checkOutDate);
    if (nights <= 0) {
        costEstimate.style.display = 'none';
        return;
    }

    var selectedOption = roomSelect.options[roomSelect.selectedIndex];
    var rate = parseFloat(selectedOption.getAttribute('data-rate'));

    if (isNaN(rate) || rate <= 0) {
        costEstimate.style.display = 'none';
        return;
    }

    var totalCost = nights * rate;

    nightsDisplay.textContent = nights;
    costDisplay.textContent   = totalCost.toLocaleString('en-US', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
    costEstimate.style.display = 'block';
}

document.addEventListener('DOMContentLoaded', function() {
    var roomSelect = document.getElementById('roomId');
    if (roomSelect) {
        roomSelect.addEventListener('change', function() {
            updateCostEstimate();
        });
    }
});