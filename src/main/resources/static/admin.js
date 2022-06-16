
const BOOKING_TABLE_BODY_ID = "allBookingsBody";
const BOOKING_TABLE_ID = "allBookingsTable";
var stompClient = null;

function connect() {
    var socket = new SockJS('/admin-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/subscribers/bookings', function (bookingMsg) {
            let bookingMsgJSON = JSON.parse(bookingMsg.body);
            if (bookingMsgJSON.actionType === "create") {
                showNewBooking(bookingMsgJSON);
            } 
            else if (bookingMsgJSON.actionType === "delete") {
                deleteBooking(bookingMsgJSON);
            }
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

function sendBooking() {
//    stompClient.send("/app/booking", {}, JSON.stringify({'name': $("#name").val()}));
    void(0);
}

function showNewBooking(bookingJSON) {
    let table = document.getElementById('allBookingsTable');
    let tableRows = table.rows.length;
    let newRow = table.insertRow(-1);
    let indexCell = newRow.insertCell(0);
    let bookingIdCell = newRow.insertCell(1);
    let userIdCell = newRow.insertCell(2);
    let verificationCell = newRow.insertCell(3);
    let deleteBtnCell = newRow.insertCell(4);
    indexCell.innerHTML = tableRows;
    bookingIdCell.innerHTML = bookingJSON.BOOKING_ID;
    userIdCell.innerHTML = bookingJSON.USER_ID;
    verificationCell.innerHTML = bookingJSON.verification;
    deleteBtnCell.innerHTML = `<button id="delete_${bookingJSON.BOOKING_ID}" name="bookingDelete" type="button" class="btn btn-danger">Delete</button>`;
    window.location.reload();
}

function deleteBooking(bookingJSON) {
    let deleteRowId = "booking_".concat(bookingJSON.BOOKING_ID);
    let deleteRow = document.getElementById(deleteRowId);
    deleteRow.remove();

    let bookingTable = document.getElementById(BOOKING_TABLE_ID);
    let bookingRows = bookingTable.rows;
    let bookingRowsLength = bookingRows.length;
    for (let i = 1; i < bookingRowsLength; i++) {
        let bookingRow = bookingRows[i];
        let rowCells = bookingRow.cells;
        rowCells[0].innerHTML = i;
    }
}

function deleteBtnAddEventListener() {
    const bookingDeleteBtns = document.getElementsByName("bookingDelete");
    for (let i = 0; i < bookingDeleteBtns.length; i++) {
        let deleteBtn = bookingDeleteBtns[i];
        deleteBtn.addEventListener("click", function () {
            let deleteId = deleteBtn.id;
            const DELETE_ID_PREFIX = "delete_";
            const SUBSTRING_START_POSITION = DELETE_ID_PREFIX.length;
            let deleteBookingId = deleteId.substring(SUBSTRING_START_POSITION);
            let deleteURL = '/admin/deletebooking/'.concat(deleteBookingId);
            // using jQuery to make an Ajax Http DELETE call to the admin page's DELETE endpoint URL
            $.ajax({
                url : deleteURL,
                method : 'delete',
                data : {
                bookingId : deleteBookingId
                }
            });
        });
    }
}

window.addEventListener('load', function(){
    deleteBtnAddEventListener();
});

if ('serviceWorker' in navigator) {
    // https://stackoverflow.com/questions/43813770/how-to-intercept-all-http-requests-including-form-submits   (1st answer)
    // https://developer.mozilla.org/en-US/docs/Web/API/Service_Worker_API/Using_Service_Workers
    window.addEventListener('load', function() {
      navigator.serviceWorker.register('service_worker.js', {scope: "/admin"}).then(function(registration) {
        console.log('Service worker registered with scope: ', registration.scope);
      }, function(err) {
        console.log('ServiceWorker registration failed: ', err);
      });

      // https://developer.mozilla.org/en-US/docs/Web/API/Worker/postMessage
      // https://developer.mozilla.org/en-US/docs/Web/API/ServiceWorkerGlobalScope/message_event
      navigator.serviceWorker.ready.then(registration => {
        let message = {"jwt": window.localStorage.getItem("jwt")};
        registration.active.postMessage(message);
      });
    });
  }

