package edu.bu.met622.controller;

import edu.bu.met622.daos.LuceneSearch;
import edu.bu.met622.daos.MongoDB;
import edu.bu.met622.daos.MySQL;
import edu.bu.met622.entities.ClientMessage;
import edu.bu.met622.entities.QueryResult;
import edu.bu.met622.daos.BruteForce;
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
public class QueryController {

    private String keyword;                                               // The user entered keyword
    private String startYear;                                             // The first year in the range to be searched
    private String endYear;                                               // The last year in the range to be searched

    /**
     * Initialize a new ResponseController
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this new ResponseController
     */
    public QueryController() {}

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
     * Maps messages to the /keyword endpoint by matching the declared patterns to a destination extracted from the
     * message
     *
     * @param startYear The first year in the range to be searched provided by the user
     */
    @MessageMapping("/startYear")
    public void startYear(ClientMessage startYear) {
        this.startYear = startYear.getStartYear();
    }

    /**
     * Maps messages to the /endYear endpoint by matching the declared patterns to a destination extracted from the
     * message
     *
     * @param endYear The last year in the range to be searched provided by the user
     */
    @MessageMapping("/endYear")
    public void endYear(ClientMessage endYear) {
        this.endYear = endYear.getEndYear();
    }

    /**
     * Maps messages to the /mysql/search endpoint by matching the declared patterns to a destination extracted from
     * the message. Sends the response to the /query/response endpoint
     */
    @MessageMapping("/mysql/search")
    @SendTo("/query/mysql/response")
    public QueryResult mySqlResponse() {
        MySQL mySQLDB = MySQL.getInstance();

        double hits = mySQLDB.query(keyword, startYear, endYear);
        double runtime = mySQLDB.getRunTime();

        return new QueryResult(keyword, hits, runtime);
    }

    /**
     * Maps messages to the /mongodb/search endpoint by matching the declared patterns to a destination extracted from
     * the message. Sends the response to the /query/response endpoint
     */
    @MessageMapping("/mongodb/search")
    @SendTo("/query/mongodb/response")
    public QueryResult mongoDBResponse() {
        MongoDB mongoDB = MongoDB.getInstance();

        double hits = mongoDB.query(keyword, startYear, endYear);
        double runtime = mongoDB.getRunTime();

        return new QueryResult(keyword, hits, runtime);
    }

    /**
     * Maps messages to the /lucene/search endpoint by matching the declared patterns to a destination extracted from
     * the message. Sends the response to the /query/response endpoint
     */
    @MessageMapping("/lucene/search")
    @SendTo("/query/lucene/response")
    public QueryResult luceneResponse() {
        LuceneSearch luceneSearch = new LuceneSearch();

        double hits = luceneSearch.search(keyword, startYear, endYear, Integer.MAX_VALUE);
        double runtime = luceneSearch.getRunTime();

        return new QueryResult(keyword, hits, runtime);
    }

    /**
     * Maps messages to the /bruteforce/search endpoint by matching the declared patterns to a destination extracted
     * from the message. Sends the response to the /query/response endpoint
     */
    @MessageMapping("/bruteforce/search")
    @SendTo("/query/bruteforce/response")
    public QueryResult bruteForceResponse() {
        BruteForce bfParser = new BruteForce();

        double hits = bfParser.parse(keyword, startYear, endYear);
        double runtime = bfParser.getRunTime();

        return new QueryResult(keyword, hits, runtime);
    }
}
