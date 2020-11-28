package edu.bu.met622;

import edu.bu.met622.daos.MongoDB;
import edu.bu.met622.daos.MySQL;
import edu.bu.met622.daos.LuceneIndex;
import edu.bu.met622.resources.ApplicationConfig;
import edu.bu.met622.utils.FileMerger;
import edu.bu.met622.utils.XMLParser;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
        System.out.println(ApplicationConfig.START_MESSAGE);
    }

    /**
     * Gets files using a file chooser dialog box and builds the simulation. This method ensures that the user has
     * selected more than one file to merge
     */
    public void build() {
        String fileName;

        JFileChooser jFileChooser = new JFileChooser(FileSystemView.getFileSystemView());
        jFileChooser.setMultiSelectionEnabled(true);

        File[] selectedFiles;
        while (true) {
            int returnValue = jFileChooser.showOpenDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                selectedFiles = jFileChooser.getSelectedFiles();
                if (selectedFiles.length == 1) {                          // Only merge if more than one was selected
                    initDatabases(selectedFiles[0].getPath());
                } else {
                    mergeXML(selectedFiles);
                    initDatabases(ApplicationConfig.MERGED_XML_FILE);
                }
                break;
            } else {                                                      // No files selected
                System.out.println(ApplicationConfig.FILE_SELECTION_ERROR);

                if (returnValue == JFileChooser.CANCEL_OPTION) {          // Break out of the selection process
                    break;
                }
            }

        }
    }

    /**
     * Close all database resources
     */
    public void cleanup() {
        System.out.print(ApplicationConfig.CLOSING_CONNECTIONS);          // Notify user that connections are closing

        MySQL.closeConnection();                                          // Close the database connection
        MySQL.closeStatement();                                           // Close the SQL statement object
        MySQL.closePreparedStatement();                                   // Close the PreparedStatement object
        MongoDB.closeConnection();                                                  // Close the MongoDB client

        System.out.print(ApplicationConfig.CLOSED_CONNECTIONS);           // Notify user that connections are closed
    }

    /**
     * Notify the user that the ChatBot session has terminated
     */
    public void endMessage() {
        System.out.print(ApplicationConfig.END_MESSAGE);                  // Terminate ChatBot message for the client
    }

    //*****************************************************************************************************************
    // Helper methods
    //*****************************************************************************************************************

    /*
     * Merge multiple XML documents into one
     */
    private void mergeXML(File[] files) {

        ArrayList<String> fileCollection = new ArrayList<>();
        for (File file : files) {
            fileCollection.add(file.getPath());
        }

        FileMerger fileMerger = new FileMerger(fileCollection, ApplicationConfig.MERGED_XML_FILE);
        try {
            fileMerger.merge();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Build up the databases if they don't exist
     */
    private void initDatabases(String filename) {
        XMLParser parser = new XMLParser(filename);

        if (!LuceneIndex.exists()) {                            // If document hasn't been parsed then...
            parser.parse();                                     // Parse the entire document
            parser.createLuceneIndex();                         // Build Lucene Index
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
