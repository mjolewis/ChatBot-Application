package edu.bu.met622.model;

public class ServerResponse {

    private String keyword;
    private String year;
    private double hits;
    private double runtime;

    public ServerResponse() {}

    public ServerResponse(String keyword, String year, double hits, double runtime) {

        this.keyword = keyword;
        this.year = year;
        this.hits = hits;
        this.runtime = runtime;
    }

    public String getKeyword() {
        return keyword;
    }

    public String getYear() {
        return year;
    }

    public double getHits() {
        return hits;
    }

    public double getRuntime() { return  runtime; }
}
