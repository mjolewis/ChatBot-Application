package edu.bu.met622.model;

/**********************************************************************************************************************
 * Represents the message from the client
 *
 * @author Michael Lewis
 * @version November 20, 2020 - Kickoff
 * @version November 25, 2020 - Add range query support
 *********************************************************************************************************************/
public class ClientMessage {

    private String keyword;
    private String startYear;
    private String endYear;

    /**
     * Initialize a new ClientMessage
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this new ClientMessage
     */
    public ClientMessage() {
    }

    /**
     * Initialize a new ClientMessage
     *
     * @param keyword   The keyword that the user wants to search for. This is submitted to the client
     * @param startYear The first year in which the keyword will be searched. This is submitted to the client
     * @param endYear   The last year in which the keyword will be searched. This is submitted to the client
     * @throws OutOfMemoryError Indicates insufficient memory for this new ClientMessage
     */
    public ClientMessage(String keyword, String startYear, String endYear) {

        this.keyword = keyword;
        this.startYear = startYear;
        this.endYear = endYear;
    }

    /**
     * Accessor method that returns the keyword
     *
     * @return The keyword being searched for
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Mutator method that updates the keyword in this ClientMessage
     *
     * @param keyword The new keyword for this ClientMessage
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Accessor method that returns the first year in which the keyword was searched
     *
     * @return The string representation of the year being searched
     */
    public String getStartYear() {
        return startYear;
    }

    /**
     * Mutator method that updates the startYear in this ClientMessage
     *
     * @param startYear The new startYear for this ClientMessage
     */
    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }

    /**
     * Accessor method that returns the last year in which the keyword was searched
     *
     * @return The string representation of the year being searched
     */
    public String getEndYear() {
        return endYear;
    }

    /**
     * Mutator method that updates the endYear in this ClientMessage
     *
     * @param endYear The new endYear for this ClientMessage
     */
    public void setEndYear(String endYear) {
        this.endYear = endYear;
    }
}
