let stompClient = null;

let endpoint = '/gs-guide-websocket';

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
    let socket = new SockJS(endpoint);
    stompClient = Stomp.over(socket);
    //TODO hide connect button before user not fill user_name, Add new text input with user name
    stompClient.connect({ 'user_name' : getUserName() }, onConnectSuccess, onConnectError);
}

function getUserName() {
    return $("#name").val();
}

function onConnectSuccess(frame) {
    setConnected(true);
    console.log('Connected: ' + frame);

    const broadcastPath = '/topic/broadcast';
    stompClient.subscribe('/topic/broadcast', broadcastMessageHandler);

    const sessionId = getSessionId();
    const privatePath = "/user/" + sessionId + "/queue/messages";
    stompClient.subscribe(privatePath, privateMessageHandler);
}

function onConnectError(error) {
    alert("STOMP error " + error);
}

function privateMessageHandler(msg) {
    showGreeting(JSON.parse(msg.body).message);
}

function broadcastMessageHandler(msg) {
    showGreeting(JSON.parse(msg.body).message);
}

function getSessionId() {
    let url = stompClient.ws._transport.url;
    console.log('URL: ' + url);

    url = url.replace("ws://localhost:8080" + endpoint + "/", "");
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