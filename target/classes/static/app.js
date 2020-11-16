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
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
            showResults(JSON.parse(greeting.body));
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function search() {
    stompClient.send("/app/search", {}, JSON.stringify({'name': $("#name").val()}));
}

function sendKeyword() {
    stompClient.send("/app/keyword", {}, JSON.stringify({'keyword': $("#keyword").val()}));
}

function sendYear() {
    stompClient.send("/app/year", {}, JSON.stringify({'year': $("#year").val()}));
}

function showResults(message) {
    $("#greetings").append("<tr><td>" + message.keyword + " was found " + message.hits + " times in " + message.year + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#sendKeyword" ).click(function() { sendKeyword(); });
    $( "#sendYear" ).click(function() { sendYear(); });
    $( "#search" ).click(function() { search(); });
});






// var botui = new BotUI('botui-app');                                  // id of container
// //var wss;
// var searchType;
// var keyword;
// var time;
// //botui.send("test");
//
// //function connect() {
// //ws = new WebSocket('ws://localhost:8080/user');
// //wss = new WebSocket('wss://localhost:8080/');
//
// botui.message.add({                                                  // first message
//     delay: 200,
//     content: 'Welcome to ChatBot'
// }).then(() => {
//     return botui.message.add({                                       // second message
//         delay: 1000,                                                 // wait 1 sec.
//         content: "I'll help you search through PubMed! Let's start with this...",
//     })
// }).then(() => {
//     return botui.message.add({                                       // third message
//         delay: 1000,                                                 // wait 1 sec.
//         content: "What type of search do you want to use?"
//     })
// }).then(() => {
//     return botui.action.select({
//         action: {
//             placeholder: "Select the search type",
//             searchselect: true,                                      // Default: true, false for standard dropdown
//             label: 'text',                                           // dropdown label variable
//             options: [
//                 {value: "BF", text: "BruteForce"},
//                 {value: "LI", text: "Lucene Index"},
//                 {value: "SQL", text: "MySQL"},
//                 {value: "MG", text: "MongoDB"},
//             ],
//             button: {
//                 icon: 'check',
//                 label: 'OK'
//             }
//         }
//     })
// }).then(function (res) {                                   // Get the result and continue conversation
//     searchType = res.value;
//     return botui.message.add({
//         delay: 1000,                                                 // wait 1 sec.
//         content: "Cool! Thanks for the info. Just a few more questions..."
//     });
// }).then(() => {                                                      // Bot continues the conversation
//     return botui.message.add( {
//         delay: 1000,                                                 // wait 1 sec.
//         content: "Do you want to do a range query or search within a specific year?"
//     })
// }).then(() => {
//     return time = botui.action.select({
//         action: {
//             placeholder: "Your choice...",
//             searchselect: true,                                      // Default: true, false for standard dropdown
//             label: 'text',                                           // dropdown label variable
//             options: [
//                 {value: "Range", text: "Range of years"},
//                 {value: "Single", text: "Specific year"},
//             ],
//             button: {
//                 icon: 'check',
//                 label: 'OK'
//             }
//         }
//     })
// }).then(() => {                                                      // Bot continues the conversation
//     return botui.message.add( {
//         delay: 1000,                                                 // wait 1 sec.
//         content: "Sounds good! Just one more question...",
//     })
// }).then(() => {                                                      // Bot continues the conversation
//     return botui.message.add( {
//         delay: 1000,                                                 // wait 1 sec
//         content: "What do you want to search for? You can enter a keyword like 'Flu'?"
//     })
// }).then(() => {
//     return keyword = botui.action.text({
//         action: {
//             placeholder: "Enter a keyword"
//         }
//     })
// }).then(() => {                                                      // Called after user enters a keyword
//     return botui.message.add( {
//         delay: 1000,                                                 // wait 1 sec
//         content: "Ok, I'll search for " + keyword + " in " + time.value + " using " + searchType.value
//     })
// }).then(res => {
//     return botui.message.bot({
//         delay: 1000,
//         content: `You are feeling ${res.text}!`
//     })
// });
//
//
//
//
//
//
//
// // var ws;
// // function setConnected(connected) {
// //     $("#connect").prop("disabled", connected);
// //     $("#disconnect").prop("disabled", !connected);
// // }
// //
// // function connect() {
// //     ws = new WebSocket('ws://localhost:8080/user');
// //     ws.onmessage = function(data) {
// //         helloWorld(data.data);
// //     }
// //     setConnected(true);
// // }
// //
// // function disconnect() {
// //     if (ws != null) {
// //         ws.close();
// //     }
// //     setConnected(false);
// //     console.log("Websocket is in disconnected state");
// // }
// //
// // function sendData() {
// //     var data = JSON.stringify({
// //         'user' : $("#user").val()
// //     })
// //     ws.send(data);
// // }
// //
// // function helloWorld(message) {
// //     $("#helloworldmessage").append(" " + message + "");
// // }
// //
// // $(function() {
// //     $("form").on('submit', function(e) {
// //         e.preventDefault();
// //     });
// //     $("#connect").click(function() {
// //         connect();
// //     });
// //     $("#disconnect").click(function() {
// //         disconnect();
// //     });
// //     $("#send").click(function() {
// //         sendData();
// //     });
// // });
