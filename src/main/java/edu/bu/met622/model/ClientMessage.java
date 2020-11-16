package edu.bu.met622.model;

/**********************************************************************************************************************
 * Represents the message from the client
 *
 *********************************************************************************************************************/
public class ClientMessage {
    private String keyword;
    private String year;

    /**
     * Initialize a new ClientMessage
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this new ClientMessage
     */
    public ClientMessage() {}

    public ClientMessage(String keyword, String year) {

        this.keyword = keyword;
        this.year = year;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
