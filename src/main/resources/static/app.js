var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect(
        {
            'user_name' : $("#name").val() //TODO hide connect button before user not fill user_name, Add new text input with user name
        },
        function (frame) {
            setConnected(true);
            console.log('Connected: ' + frame);

            stompClient.subscribe('/topic/broadcast', function (greeting) {
                showGreeting(JSON.parse(greeting.body).message);
            });

            let sessionId = getSessionId();
            let privatePath = "/user/" + sessionId + "/queue/messages";
            stompClient.subscribe(privatePath, function(msg) {
                console.log(">>>" + privatePath + msg)
                showGreeting(JSON.parse(greeting.body).message);
            });
        },
        function(error) {
            alert("STOMP error " + error);
        }
        );
}

function getSessionId() {
    let url = stompClient.ws._transport.url;
    console.log('URL: ' + url);

    url = url.replace("ws://localhost:8080/gs-guide-websocket/", "");
    url = url.replace("/websocket", "");
    url = url.replace(/^[0-9]+\//, "");
    console.log("SESSION_ID: " + url);

    return url;
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/hello", {}, JSON.stringify({'message': $("#name").val()}));
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});