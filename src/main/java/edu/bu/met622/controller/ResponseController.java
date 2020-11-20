package edu.bu.met622.controller;

import edu.bu.met622.Builder;
import edu.bu.met622.database.LuceneIndex;
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

    String keyword;
    String year;

    @MessageMapping("/keyword")
    public void keyword(ClientMessage keyword) {
        this.keyword = keyword.getKeyword();
    }

    @MessageMapping("/year")
    public void year(ClientMessage year) {
        this.year = year.getYear();
    }

    @MessageMapping("/mysql/search")
    @SendTo("/query/response")
    public ServerResponse mySqlResponse() {
        MySQL mySQLDB = MySQL.getInstance();

        double hits = mySQLDB.query(keyword, year);
        double runtime = mySQLDB.getRunTime();

        return new ServerResponse(keyword, year, hits, runtime);
    }

    @MessageMapping("/mongodb/search")
    @SendTo("/query/response")
    public ServerResponse mongoDBResponse() {
        MongoDB mongoDB = MongoDB.getInstance();

        double hits = mongoDB.query(keyword, year);
        double runtime = mongoDB.getRunTime();

        return new ServerResponse(keyword, year, hits, runtime);
    }

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
