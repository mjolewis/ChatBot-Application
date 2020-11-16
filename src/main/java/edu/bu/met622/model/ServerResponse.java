package edu.bu.met622.model;

public class ServerResponse {

    private String keyword;
    private String year;
    private int hits;

    public ServerResponse() {}

    public ServerResponse(String keyword, String year, int hits) {

        this.keyword = keyword;
        this.year = year;
        this.hits = hits;
    }

    public String getKeyword() {
        return keyword;
    }

    public int getHits() {
        return hits;
    }

    public String getYear() {
        return year;
    }
}
