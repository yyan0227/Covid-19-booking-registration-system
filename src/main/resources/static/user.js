

function connect() {
    var socket = new SockJS('/user-websocket');
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

function cancelBtnAddEventListener() {
    const cancelButton = document.getElementsByName("bookingCancel");
    cancelButton.addEventListener("click", function cancelBooking() {
        let display = document.getElementById("status");
        if (display.innerHTML == "Scheduled") {
            display.innerHTML = "Cancel";
        } else {
            display.innerHTML = "Scheduled"
        }

    });
}


window.addEventListener('load', function(){
    cancelBtn();
});



if ('serviceWorker' in navigator) {
    // https://stackoverflow.com/questions/43813770/how-to-intercept-all-http-requests-including-form-submits   (1st answer)
    // https://developer.mozilla.org/en-US/docs/Web/API/Service_Worker_API/Using_Service_Workers
    window.addEventListener('load', function() {
      navigator.serviceWorker.register('service_worker.js', {scope: "/user"}).then(function(registration) {
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
