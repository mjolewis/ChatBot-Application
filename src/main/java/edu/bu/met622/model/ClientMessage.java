package edu.bu.met622.model;

/**********************************************************************************************************************
 * Represents the message from the client
 *
 * @author Michael Lewis
 * @version November 20, 2020 - Kickoff
 *********************************************************************************************************************/
public class ClientMessage {
    private String keyword;
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
     * @param keyword The keyword that the user wants to search for. This is submitted to the client in BotUI interface
     * @throws OutOfMemoryError Indicates insufficient memory for this new ClientMessage
     */
    public ClientMessage(String keyword) {

        this.keyword = keyword;
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
     * Mutator method the updates the keyword in this ClientMessage
     *
     * @param keyword The new keyword for this ClientMessage
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
