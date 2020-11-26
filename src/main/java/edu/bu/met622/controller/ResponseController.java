package edu.bu.met622.controller;

import edu.bu.met622.database.LuceneSearch;
import edu.bu.met622.database.MongoDB;
import edu.bu.met622.database.MySQL;
import edu.bu.met622.model.ClientMessage;
import edu.bu.met622.model.ServerResponse;
import edu.bu.met622.resources.ApplicationConfig;
import edu.bu.met622.utils.BFParser;
import edu.bu.met622.utils.Grapher;
import javafx.application.Application;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import javax.swing.*;

/**********************************************************************************************************************
 * A web request handler
 *
 * @author Michael Lewis
 * @version November 20, 2020 - Kickoff
 *********************************************************************************************************************/
@Controller
public class ResponseController {

    String keyword;                                                  // The user entered keyword
    String startYear;                                                // The first year in the range to be searched
    String endYear;                                                  // The last year in the range to be searched

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
    public ServerResponse mySqlResponse() {
        MySQL mySQLDB = MySQL.getInstance();

        double hits = mySQLDB.query(keyword, startYear, endYear);
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

    /**
     * Maps messages to the /graph endpoint by matching the declared patterns to a destination extracted from the
     * message. Sends the response to the /query/graph/response endpoint
     */
    @MessageMapping("/graph")
    @SendTo("/query/graph/response")
    public void loadGraph() {
        Grapher grapher = new Grapher();

        grapher.graph();

        grapher.setAlwaysOnTop(true);
        grapher.pack();
        grapher.setSize(ApplicationConfig.CHART_WIDTH, ApplicationConfig.CHART_HEIGHT);
        grapher.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        grapher.setVisible(true);
    }
}
