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

    /**
     * Initialize a new ServerResponse instance
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this new ServerResponse
     */
    public ServerResponse() {
    }

    /**
     * Initialize a new ServerResponse instance
     *
     * @param keyword The keyword that the user has searched for
     * @param hits    The number of times the keyword was found
     * @param runtime The time in milliseconds it took to complete the search
     */
    public ServerResponse(String keyword, double hits, double runtime) {

        this.keyword = keyword;
        this.hits = hits;
        this.runtime = runtime;
    }

    /**
     * Accessor method that returns the keyword that will be sent back to the client
     *
     * @return The keyword being sent back to the client
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Accessor method that returns the number of times the keyword was found
     *
     * @return The number of times the keyword was found during the search
     */
    public double getHits() {
        return hits;
    }

    /**
     * Accessor method that returns the time in milliseconds it took to complete the search
     *
     * @return The search time in milliseconds
     */
    public double getRuntime() {
        return runtime;
    }
}
