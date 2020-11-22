package edu.bu.met622.controller;

import edu.bu.met622.database.LuceneSearch;
import edu.bu.met622.database.MongoDB;
import edu.bu.met622.database.MySQL;
import edu.bu.met622.model.ClientMessage;
import edu.bu.met622.model.ServerResponse;
import edu.bu.met622.utils.BFParser;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**********************************************************************************************************************
 * A web request handler
 *
 * @author Michael Lewis
 * @version November 20, 2020 - Kickoff
 *********************************************************************************************************************/
@Controller
public class ResponseController {

    String keyword;                                                  // The user entered keyword

    /**
     * Maps messages to the /keyword endpoint by matching the declared patterns to a destination extracted from the
     * message
     *
     * @param keyword The keyword entered by the user
     */
    @MessageMapping("/keyword")
    public void keyword(ClientMessage keyword) {
        this.keyword = keyword.getKeyword();
    }

    /**
     * Maps messages to the /mysql/search endpoint by matching the declared patterns to a destination extracted from
     * the message. Sends the response to the /query/response endpoint
     */
    @MessageMapping("/mysql/search")
    @SendTo("/query/mysql/response")
    public ServerResponse mySqlResponse() {
        MySQL mySQLDB = MySQL.getInstance();

        double hits = mySQLDB.query(keyword);
        double runtime = mySQLDB.getRunTime();

        return new ServerResponse(keyword, hits, runtime);
    }

    /**
     * Maps messages to the /mongodb/search endpoint by matching the declared patterns to a destination extracted from
     * the message. Sends the response to the /query/response endpoint
     */
    @MessageMapping("/mongodb/search")
    @SendTo("/query/mongodb/response")
    public ServerResponse mongoDBResponse() {
        MongoDB mongoDB = MongoDB.getInstance();

        double hits = mongoDB.query(keyword);
        double runtime = mongoDB.getRunTime();

        return new ServerResponse(keyword, hits, runtime);
    }

    /**
     * Maps messages to the /lucene/search endpoint by matching the declared patterns to a destination extracted from
     * the message. Sends the response to the /query/response endpoint
     */
    @MessageMapping("/lucene/search")
    @SendTo("/query/lucene/response")
    public ServerResponse luceneResponse() {
        LuceneSearch luceneSearch = new LuceneSearch();

        double hits = luceneSearch.search(keyword, 5000);
        double runtime = luceneSearch.getRunTime();

        return new ServerResponse(keyword, hits, runtime);
    }

    /**
     * Maps messages to the /bruteforce/search endpoint by matching the declared patterns to a destination extracted
     * from the message. Sends the response to the /query/response endpoint
     */
    @MessageMapping("/bruteforce/search")
    @SendTo("/query/bruteforce/response")
    public ServerResponse bruteForceSearch() {
        BFParser bfParser = new BFParser();

        double hits = bfParser.parse(keyword);
        double runtime = bfParser.getRunTime();

        return new ServerResponse(keyword, hits, runtime);
    }
}
