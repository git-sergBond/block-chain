let stompClient = null;

const endpoint = '/gs-guide-websocket';
const sendPath = "/app/send";
const broadcastPath = "/topic/broadcast";
const errorPath = "/user/{sessionId}/queue/errors";
const messagesPath = "/user/{sessionId}/queue/messages";

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#sendMessage").prop("disabled", !connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#messages").html("");
}

function connect() {
    let userName = getUserName();
    if (typeof (userName) !== "string" || userName.length <= 0) {
        alert("You need to field user name!")
        return;
    }

    let socket = new SockJS(endpoint);
    stompClient = Stomp.over(socket);
    stompClient.connect({ 'user_name' : userName }, onConnectSuccess, onConnectError);
}

function getUserName() {
    return $("#name").val();
}

function onConnectSuccess(frame) {
    setConnected(true);
    console.log('Connected: ' + frame);

    stompClient.subscribe(broadcastPath, broadcastMessageHandler);
    stompClient.subscribe(createSessionPath(messagesPath), privateMessageHandler);
    stompClient.subscribe(createSessionPath(errorPath), errorMessageHandler);
}

function createSessionPath(path) {
    const sessionId = getSessionId();
    const sessionPath = path.replace("{sessionId}", sessionId);
    console.log("sessionPath=" + sessionPath)
    return sessionPath;
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

function errorMessageHandler(msg) {
    showGreeting(msg.body);
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

function sendMessage() {
    stompClient.send(sendPath, {}, JSON.stringify({'message': $("#message").val()}));
}

function showGreeting(message) {
    $("#messages").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("#conversation").hide();
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#sendMessage" ).click(function() { sendMessage(); });
});