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


            let sessionId = getSessionId();
            stompClient.subscribe("/user/queue/errors", function(message) {
                alert("Error " + message.body);
            });

            stompClient.subscribe("/user/queue/reply", function(message) {
                showGreeting(message.body);
            });
            stompClient.subscribe('/topic/greetings', function (greeting) {
                showGreeting(JSON.parse(greeting.body).message);
            });
            stompClient.subscribe('/user/queue/reply', function(msg) {
                console.log('******* /user/queue/reply ' + msg)
                showGreeting(JSON.parse(greeting.body).message);
            });
            stompClient.subscribe('/user/' + sessionId + '/queue/reply', function(msg) {
                console.log('******* /user/' + sessionId + '/queue/reply' + msg)
                showGreeting(JSON.parse(greeting.body).message);
            });
            stompClient.subscribe('user/queue/reply', function(msg) {
                console.log('******* /user/queue/reply ' + msg)
                showGreeting(JSON.parse(greeting.body).message);
            });
            stompClient.subscribe('user/' + sessionId + '/queue/reply', function(msg) {
                console.log('******* /user/' + sessionId + '/queue/reply' + msg)
                showGreeting(JSON.parse(greeting.body).message);
            });
            //user/uxtjc0jh/queue/reply
        }, function(error) {
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