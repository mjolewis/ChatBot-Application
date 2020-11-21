
/**********************************************************************************************************************
 * Conversational ChatBot managed by BotUI and full duplex communication with WebSockets
 *
 * @author Michael Lewis
 *********************************************************************************************************************/

var socket = new SockJS('/chatbot');
var stompClient = Stomp.over(socket);
var botui = new BotUI('botui-app');
var keyword;
var year;

/**
 * Establish a websocket connection. Upon successful connection, subscribe to /query/response destination where the
 * server will publish the query results.
 */
stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);
    stompClient.subscribe('/query/response', function (response) {
        window.result = JSON.parse(response.body);
    });
});

/**
 * Send the keyword to endpoint
 */
function sendKeyword(keyword) {
    stompClient.send("/app/keyword", {}, JSON.stringify({'keyword': keyword}));
}

/**
 * Send the year to endpoint
 */
function sendYear(year) {
    stompClient.send("/app/year", {}, JSON.stringify({'year': year}));
}

/**
 * Search a MySQL database for the keyword
 */
function mySqlSearch() {
    stompClient.send("/app/mysql/search", {});
}

/**
 * Search a MongoDB database for the keyword
 */
function mongoDBSearch() {
    stompClient.send("/app/mongodb/search", {});
}

/**
 * Search a Lucene Index for the keyword
 */
function luceneIndexSearch() {
    stompClient.send("/app/lucene/search", {});
}

/**
 * A conversation with the user managed by BotUI
 */
botui.message.add({                                                  // first message
    delay: 200,
    content: 'Welcome to ChatBot'
}).then(() => {
    return botui.message.add({                                       // second message
        delay: 2000,
        loading: true,
        content: "I'll help you search through PubMed! Let's start with this...",
    })
}).then(() => {                                                      // third message
    return botui.message.add( {
        delay: 2000,
        loading: true,
        content: "What year should I search in?"
    })
}).then(() => {                                                      // fourth message
    return botui.action.text({
        action: {
            placeholder: "Enter a year"
        }
    })
}).then(function(res) {                                   // fifth message
    year = res.value;
    sendYear(year);

    return botui.message.add( {
        delay: 2000,
        loading: true,
        content: "Sounds good! Just one more question...",
    })
}).then(() => {                                                      // sixth message
    return botui.message.add( {
        delay: 2000,
        loading: true,
        content: "What do you want to search for? You can enter a keyword like 'Flu'?"
    })
}).then(() => {
    return botui.action.text({                                       // seventh message
        action: {
            placeholder: "Enter a keyword"
        }
    })
}).then(function (res) {                                   // eighth message
    keyword = res.value;
    sendKeyword(keyword);

    return botui.message.add( {
        delay: 2000,
        loading: true,
        content: "Ok, I'll search for " + keyword + " in " + year + "..."
    })
}).then(() => {                                                      // ninth message
    return botui.message.add({
        delay: 2000,
        loading: true,
        content: "For simulation purposes, I am going to search multiple databases so you can compare the runtime " +
            "performance. I will search a MySQL database, a MongoDB database, a Lucene Index, and using Brute Force..."
    })
}).then(() => {                                                      // tenth message
    mySqlSearch();

    return botui.message.add({
        delay: 6000,
        loading: true,
        content: "Searching with MySQL...",
    })
}).then(() => {                                                      // eleventh message
    return botui.message.add( {
        delay: 2000,
        loading: true,
        content: "It took me " + window.result.runtime + " milliseconds" + " to find the keyword '" + window.result.keyword + "' "
            + window.result.hits + " times in " + window.result.year
    })
}).then(() => {                                                      // twelfth message
    mongoDBSearch();

    return botui.message.add({
        delay: 3000,
        loading: true,
        content: "Searching with MongoDB...",
    })
}).then(() => {                                                      // thirteenth message
    return botui.message.add({
        delay: 2000,
        loading: true,
        content: "It took me " + window.result.runtime + " milliseconds" + " to find the keyword '" + window.result.keyword + "' "
            + window.result.hits + " times in " + window.result.year
    })
}).then(() => {                                                      // fourteenth message
    luceneIndexSearch();

    return botui.message.add({
        delay: 3000,
        loading: true,
        content: "Searching with Lucene Index...",
    })
}).then(() => {                                                      // fifteenth message
    return botui.message.add({
        delay: 2000,
        loading: true,
        content: "It took me " + window.result.runtime + " milliseconds" + " to find the keyword '" + window.result.keyword + "' "
            + window.result.hits + " times in " + window.result.year
    })
});
