package edu.bu.met622.controller;

import edu.bu.met622.Builder;
import edu.bu.met622.database.MySQL;
import edu.bu.met622.model.ClientMessage;
import edu.bu.met622.model.ServerResponse;
import edu.bu.met622.utils.XMLParser;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ResponseController {

    String keyword;
    String year;

    @MessageMapping("/keyword")
    public void keyword(ClientMessage keyword) throws Exception {
        this.keyword = keyword.getKeyword();
    }

    @MessageMapping("/year")
    public void serverResponse(ClientMessage year) throws Exception {
        this.year = year.getYear();
    }

    @MessageMapping("/search")
    @SendTo("/query/response")
    public ServerResponse serverResponse() throws Exception {
        MySQL mySQLDB = MySQL.getInstance();

        int hits = mySQLDB.query(keyword, year);
        return new ServerResponse(keyword, year, hits);
    }

    @MessageMapping("/disconnect")
    public void disconnect() throws Exception {
        Builder builder = new Builder();
        builder.cleanup();
        builder.endMessage();
    }

}
