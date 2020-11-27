package edu.bu.met622.controller;

import edu.bu.met622.database.LuceneSearch;
import edu.bu.met622.database.MongoDB;
import edu.bu.met622.database.MySQL;
import edu.bu.met622.model.ClientMessage;
import edu.bu.met622.model.QueryResult;
import edu.bu.met622.output.Save;
import edu.bu.met622.resources.ApplicationConfig;
import edu.bu.met622.database.BruteForceSearch;
import edu.bu.met622.output.Graph;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import javax.swing.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**********************************************************************************************************************
 * A web request handler
 *
 * @author Michael Lewis
 * @version November 20, 2020 - Kickoff
 *********************************************************************************************************************/
@Controller
public class ResponseController {

    private String keyword;                                               // The user entered keyword
    private String startYear;                                             // The first year in the range to be searched
    private String endYear;                                               // The last year in the range to be searched
    private Save save;                                              // Persist search history

    /**
     * Initialize a new ResponseController
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this new ResponseController
     */
    public ResponseController() {
        this.save = new Save();
    }

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
        BruteForceSearch bfParser = new BruteForceSearch();

        double hits = bfParser.parse(keyword, startYear, endYear);
        double runtime = bfParser.getRunTime();

        return new QueryResult(keyword, hits, runtime);
    }

    /**
     * Maps messages to the /graph endpoint by matching the declared patterns to a destination extracted from the
     * message. Sends the response to the /query/graph/response endpoint
     */
    @MessageMapping("/graph")
    @SendTo("/query/graph/response")
    public void loadGraph() {
        Graph graph = new Graph();

        graph.build();

        graph.setAlwaysOnTop(true);
        graph.pack();
        graph.setSize(ApplicationConfig.CHART_WIDTH, ApplicationConfig.CHART_HEIGHT);
        graph.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        graph.setVisible(true);
    }

    //*****************************************************************************************************************
    // Helper methods to persist data
    //*****************************************************************************************************************

    /*
     * Store search history in-memory and on disk
     */
    private void save(String keyword) {
        LocalDateTime timestamp = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(ApplicationConfig.DATE_FORMAT);

        save.toMemory(keyword, timestamp.format(dateTimeFormatter));
        try {
            save.toDisk(keyword, timestamp.format(dateTimeFormatter));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
