package edu.bu.met622.model;

/**********************************************************************************************************************
 * A data model for the response message
 *
 * @author Michael Lewis
 * @version November 20, 2020 - Kickoff
 *********************************************************************************************************************/
public class ServerResponse {

    private String keyword;
    private double hits;
    private double runtime;

    public ServerResponse() {}

    public ServerResponse(String keyword, double hits, double runtime) {

        this.keyword = keyword;
        this.hits = hits;
        this.runtime = runtime;
    }

    public String getKeyword() {
        return keyword;
    }

    public double getHits() {
        return hits;
    }

    public double getRuntime() { return  runtime; }
}
