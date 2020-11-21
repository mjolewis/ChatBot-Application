package edu.bu.met622.controller;

import edu.bu.met622.database.LuceneSearch;
import edu.bu.met622.database.MongoDB;
import edu.bu.met622.database.MySQL;
import edu.bu.met622.model.ClientMessage;
import edu.bu.met622.model.ServerResponse;
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
    String year;                                                     // The user entered year to search within

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
     * Maps messages to the /year endpoint by matching the declared patterns to a destination extracted from the
     * message
     *
     * @param year The keyword entered by the user
     */
    @MessageMapping("/year")
    public void year(ClientMessage year) {
        this.year = year.getYear();
    }

    /**
     * Maps messages to the /mysql/search endpoint by matching the declared patterns to a destination extracted from
     * the message. Sends the response to the /query/response endpoint
     */
    @MessageMapping("/mysql/search")
    @SendTo("/query/response")
    public ServerResponse mySqlResponse() {
        MySQL mySQLDB = MySQL.getInstance();

        double hits = mySQLDB.query(keyword, year);
        double runtime = mySQLDB.getRunTime();

        return new ServerResponse(keyword, year, hits, runtime);
    }

    /**
     * Maps messages to the /mongodb/search endpoint by matching the declared patterns to a destination extracted from
     * the message. Sends the response to the /query/response endpoint
     */
    @MessageMapping("/mongodb/search")
    @SendTo("/query/response")
    public ServerResponse mongoDBResponse() {
        MongoDB mongoDB = MongoDB.getInstance();

        double hits = mongoDB.query(keyword, year);
        double runtime = mongoDB.getRunTime();

        return new ServerResponse(keyword, year, hits, runtime);
    }

    /**
     * Maps messages to the /lucene/search endpoint by matching the declared patterns to a destination extracted from
     * the message. Sends the response to the /query/response endpoint
     */
    @MessageMapping("/lucene/search")
    @SendTo("/query/response")
    public ServerResponse luceneResponse() {
        LuceneSearch luceneSearch = new LuceneSearch();

        // TODO: 11/20/20 fix number of docs to all docs
        double hits = luceneSearch.search(keyword, 5000);
        double runtime = luceneSearch.getRunTime();

        return new ServerResponse(keyword, year, hits, runtime);
    }
}
