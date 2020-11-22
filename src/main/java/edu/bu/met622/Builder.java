package edu.bu.met622;

import edu.bu.met622.database.MongoDB;
import edu.bu.met622.database.MySQL;
import edu.bu.met622.resources.DbSelector;
import edu.bu.met622.resources.QuerySelector;
import edu.bu.met622.database.LuceneIndex;
import edu.bu.met622.resources.Config;
import edu.bu.met622.utils.FileMerger;
import edu.bu.met622.utils.XMLParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**********************************************************************************************************************
 * Mediator class to build the simulation
 *
 * @author Michael Lewis
 *********************************************************************************************************************/
public class Builder {

    /**
     * Initialize a new Builder
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this Builder
     */
    public Builder() {
    }

    /**
     * Notify the user that the ChatBot session is initiating
     */
    public void startMessage() {
        System.out.println(Config.START_MESSAGE);
    }

    /**
     * Gets files using a file chooser dialog box and builds the simulation. This method ensures that the user has
     * selected more than one file to merge
     */
    public void build() {
        ArrayList<String> selectedFiles = new ArrayList<>();
        selectedFiles.add(Config.FILE_1);
        selectedFiles.add(Config.FILE_2);

        mergeXML(selectedFiles);
        initDabases();
    }

    /**
     * Close all database resources
     */
    public void cleanup() {
        System.out.print(Config.CLOSING_CONNECTIONS);                     // Notify user that connections are closing

        MySQL.closeConnection();                                          // Close the database connection
        MySQL.closeStatement();                                           // Close the SQL statement object
        MySQL.closePreparedStatement();                                   // Close the PreparedStatement object
        MongoDB.closeConnection();                                                  // Close the MongoDB client

        System.out.print(Config.CLOSED_CONNECTIONS);                      // Notify user that connections are closed
    }

    /**
     * Notify the user that the ChatBot session has terminated
     */
    public void endMessage() {
        System.out.print(Config.END_MESSAGE);                            // Terminate ChatBot message for the client
    }

    //*****************************************************************************************************************
    // Helper methods
    //*****************************************************************************************************************

    /*
     * Merge multiple XML documents into one
     */
    private void mergeXML(ArrayList<String> selectedFiles) {

        FileMerger fileMerger = new FileMerger(selectedFiles, Config.OUTPUT_FILE);
        try {
            fileMerger.merge();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Build up the databases if they don't exist
     */
    private void initDabases() {
        XMLParser parser = new XMLParser();

        if (!LuceneIndex.exists()) {                            // If document hasn't been parsed then...
            parser.parse();                                     // Parse the entire document
            parser.createIndex();                               // Build Lucene Index
        }

        if (!MySQL.exists()) {                                  // If database hasn't been built then...
            parser.parse();                                     // Parse the entire XML document
            parser.createSQLDB();                               // Build the database and insert content
        }

        MongoDB mongoDB = MongoDB.getInstance();
        if (!mongoDB.exists()) {                                // If document hasn't been parsed then...
            parser.parse();                                     // Parse the entire XML document
            parser.createMongoDB();                             // Build the database and insert content
        }
    }
}
