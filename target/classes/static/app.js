
/**********************************************************************************************************************
 * Conversational ChatBot managed by BotUI and full duplex communication with WebSockets
 *
 * @author Michael Lewis
 *********************************************************************************************************************/

var socket = new SockJS('/chatbot');
var stompClient = Stomp.over(socket);
var botui = new BotUI('botui-app');
var keyword;
var startYear;
var endYear;

/**
 * Establish a websocket connection. Upon successful connection, subscribe to /query/response destination where the
 * server will publish the query results.
 */
stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);

    // Register the MySQL response
    stompClient.subscribe('/query/mysql/response', function (response) {
        window.mySqlResponse = JSON.parse(response.body);
    });

    // Register the MongoDB response
    stompClient.subscribe('/query/mongodb/response', function (response) {
        window.mongoDBResponse = JSON.parse(response.body);
    });

    // Register the Lucene Index response
    stompClient.subscribe('/query/lucene/response', function (response) {
        window.luceneResponse = JSON.parse(response.body);
    });

    // Register the Brute Force response
    stompClient.subscribe('/query/bruteforce/response', function (response) {
        window.bruteForeceResponse = JSON.parse(response.body);
    });

    // Register the Graph response
    stompClient.subscribe('/query/graph/response', function (response) {
    });
});

/**
 * Send the keyword to endpoint
 */
function sendKeyword(keyword) {
    stompClient.send("/app/keyword", {}, JSON.stringify({'keyword': keyword}));
}

/**
 * Send the startYear to endpoint. The startYear is the first year that will be searched. This support range queries
 */
function sendStartYear(startYear) {
    stompClient.send("/app/startYear", {}, JSON.stringify({"startYear": startYear}));
}

/**
 * Send the endYear to endpoint. The endYear is the last year that will be searched. This support range queries
 */
function sendEndYear(endYear) {
    stompClient.send("/app/endYear", {}, JSON.stringify({"endYear": endYear}))
}

/**
 * Search for the keyword using MySQL
 */
function mySqlSearch() {
    stompClient.send("/app/mysql/search", {});
}

/**
 * Search for the keyword using MongoDB
 */
function mongoDBSearch() {
    stompClient.send("/app/mongodb/search", {});
}

/**
 * Search for the keyword using Lucene Index
 */
function luceneIndexSearch() {
    stompClient.send("/app/lucene/search", {});
}

/**
 * Search for the keyword using Brute Force
 */
function bruteForceSearch() {
    stompClient.send("/app/bruteforce/search", {});
}

/**
 * Read the runtime CSV file and plot the results
 */
function loadGraph() {
    stompClient.send("/app/graph", {});
}

// var substringMatcher = function(strs) {
//     return function findMatches(q, cb) {
//         var matches, substringRegex;
//
//         // an array that will be populated with substring matches
//         matches = [];
//
//         // regex used to determine if a string contains the substring `q`
//         substrRegex = new RegExp(q, 'i');
//
//         // iterate through the pool of strings and for any string that
//         // contains the substring `q`, add it to the `matches` array
//         $.each(strs, function(i, str) {
//             if (substrRegex.test(str)) {
//                 matches.push(str);
//             }
//         });
//
//         cb(matches);
//     };
// };
//
// var keywords = ['flu', 'brain', 'cancer', 'lungs', 'heart',
//     'eyes', 'cough', 'covid-19', 'cold', 'influenza', 'stomach',
// ];
//
// $('#autocomplete .typeahead').typeahead({
//         hint: true,
//         highlight: true,
//         minLength: 1
//     },
//     {
//         name: 'keywords',
//         source: substringMatcher(keywords)
//     });

//*********************************************************************************************************************
// Start the BotUI Conversation
//*********************************************************************************************************************

/**
 * A conversation with the user managed by BotUI
 */
botui.message.add({                                                  // first message
    delay: 500,
    content: 'Welcome to ChatBot'
}).then(() => {
    return botui.message.add({                                       // second message
        delay: 2000,
        loading: true,
        content: "If you enter a keyword, I'll search across PubMed for it...",
    })
}).then(() => {                                                      // third message
    return botui.message.add({
        delay: 2000,
        loading: true,
        content: "For simulation purposes, I am going to search multiple databases and compare the runtime " +
            "performance. I will search with MySQL, MongoDB, Lucene Index, and Brute Force..."
    })
}).then(() => {
    getUserInput();
});

/**
 * Enables repeated searching within a single run of the application
 */
function getUserInput() {
    botui.action.text({                                       // User inputs the keyword
        action: {
            delay: 3000,
            placeholder: "Enter a keyword"
        }
    }).then(function (res) {                                   // fourth message
        keyword = res.value;
        sendKeyword(keyword);

        return botui.action.text({
            action: {
                delay: 3000,
                placeholder: "Start search in year:"
            }
        })
    }).then(function (res) {
        startYear = res.value;
        sendStartYear(startYear);

        return botui.action.text({
            action: {
                delay: 3000,
                placeholder: "End search in year:"
            }
        })
    }).then(function (res) {
        endYear = res.value;
        sendEndYear(endYear); // fifth message

        // Pre-perform the searches for more efficient conversation flow
        mySqlSearch();
        mongoDBSearch();
        luceneIndexSearch();
        bruteForceSearch();

        return botui.message.add({
            delay: 2200,
            loading: true,
            content: "Okay, I'll search for " + keyword + "from " + startYear + " to " + endYear
        })

    }).then(() => {                                                      // sixth message

        return botui.message.add({
            delay: 2200,
            loading: true,
            content: "Searching with MySQL...",
        })
    }).then(() => {                                                      // seventh message
        return botui.message.add( {
            delay: 2200,
            loading: true,
            content: "It took me " + window.mySqlResponse.runtime + " milliseconds" + " to find the keyword '" +
                window.mySqlResponse.keyword + "' " + window.mySqlResponse.hits + " times"
        })
    }).then(() => {                                                      // eighth message

        return botui.message.add({
            delay: 4500,
            loading: true,
            content: "Searching with MongoDB...",
        })
    }).then(() => {                                                      // ninth message
        return botui.message.add({
            delay: 2200,
            loading: true,
            content: "It took me " + window.mongoDBResponse.runtime + " milliseconds" + " to find the keyword '" +
                window.mongoDBResponse.keyword + "' " + window.mongoDBResponse.hits + " times"
        })
    }).then(() => {                                                      // tenth message

        return botui.message.add({
            delay: 4500,
            loading: true,
            content: "Searching with Lucene Index...",
        })
    }).then(() => {                                                      // eleventh message
        return botui.message.add({
            delay: 2200,
            loading: true,
            content: "It took me " + window.luceneResponse.runtime + " milliseconds" + " to find the keyword '" +
                window.luceneResponse.keyword + "' " + window.luceneResponse.hits + " times"
        })
    }).then(() => {                                                      // twelth message
        return botui.message.add({
            delay: 4500,
            loading: true,
            content: "Searching with Brute Force...",
        })
    }).then(() => {                                                      // thirteenth message
        return botui.message.add({
            delay: 2200,
            loading: true,
            content: "It took me " + window.bruteForeceResponse.runtime + " milliseconds" + " to find the keyword '" +
                window.bruteForeceResponse.keyword + "' " + window.bruteForeceResponse.hits + " times"
        })
    }).then(() => {
        return botui.message.add({
            delay: 4500,
            loading: true,
            content: "I am done searching. Would you like to search again?"
        })
    }).then(() => {
        return botui.action.text({
            action: {
                delay: 2000,
                placeholder: "Enter yes or no"
            }
        })
    }).then(function (res) {
        if (res.value === "yes" || res.value === "Yes") {
            getUserInput();
        } else {
            loadGraph();
            return botui.message.add({
                delay: 2000,
                loading: true,
                content: "Alright, we're done searching. I'll plot the results now..."
            })
        }
    })
}