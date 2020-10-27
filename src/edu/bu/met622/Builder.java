package edu.bu.met622;

import edu.bu.met622.database.MySQL;
import edu.bu.met622.resources.SearchTypes;
import edu.bu.met622.searchlib.Indexer;
import edu.bu.met622.searchlib.SearchEngine;
import edu.bu.met622.resources.Config;
import edu.bu.met622.utils.BFParser;
import edu.bu.met622.utils.FileMerger;
import edu.bu.met622.utils.XMLParser;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**********************************************************************************************************************
 * Mediator class to build the simulation
 *
 * @author Michael Lewis
 *********************************************************************************************************************/
public class Builder {
    private ArrayList<Long> bfRunTimes;
    private ArrayList<Long> luceneRunTimes;

    /**
     * Initialize a new Builder
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this Builder
     */
    public Builder() {
        bfRunTimes = new ArrayList<>();
        luceneRunTimes = new ArrayList<>();
    }

    /**
     * Write a welcome message to the console
     */
    public void startMessage() {
        System.out.println(Config.START_MESSAGE);
    }

    /**
     * Gets files using a file chooser dialog box and builds the simulation. This method ensures that the user has
     * selected more than one file to merge
     */
    public void build() {
        JFileChooser jFileChooser = new JFileChooser(FileSystemView.getFileSystemView());
        jFileChooser.setMultiSelectionEnabled(true);

        File[] selectedFiles;
        while (true) {
            int returnValue = jFileChooser.showOpenDialog(null);

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                selectedFiles = jFileChooser.getSelectedFiles();

                if (selectedFiles.length == 1) {                         // Only merge if more than one was selected
                    System.out.println(Config.FILE_SELECTION_ERROR);
                } else {
                    mergeXML(selectedFiles);
                    parseXML();
                    break;
                }
            } else {                                                     // No files selected
                System.out.println(Config.FILE_SELECTION_ERROR);

                if (returnValue == JFileChooser.CANCEL_OPTION) {         // Break out of the selection process
                    break;
                }
            }

        }
    }

    public void cleanup() {
        System.out.print(Config.CLOSING_CONNECTIONS);                   // Notify client about closing connections
        MySQL.closeConnection();                                          // Close the database connection
        MySQL.closeStatement();                                           // Close the SQL statement object
        MySQL.closePreparedStatement();                                   // Close the PreparedStatement object
        System.out.print(Config.CLOSED_CONNECTIONS);                      // Notify client that connections are closed
    }

    /**
     * Write an ending message to the console
     */
    public void endMessage() {
        System.out.print(Config.END_MESSAGE);                            // Terminate ChatBot message for the client
    }

    //*****************************************************************************************************************
    // Helper methods for merging and parsing
    //*****************************************************************************************************************

    /*
     * Merge multiple XML documents into one
     */
    private void mergeXML(File[] selectedFiles) {
        ArrayList<String> fileContainer = new ArrayList<>();
        for (File file : selectedFiles) {
            fileContainer.add(file.getPath());
        }

        FileMerger fileMerger = new FileMerger(fileContainer, Config.OUTPUT_FILE);
        try {
            fileMerger.merge();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Search an XML document for a search parameter specified by the user
     */
    private void parseXML() {
        Scanner scanner = new Scanner(System.in);
        BFParser bfParser = new BFParser();                               // Parse and match at same time
        XMLParser parser = new XMLParser();                               // Parse entire document and then search
        MySQL db = MySQL.getInstance();
        SearchEngine searchEngine = new SearchEngine();

        do {

            SearchTypes searchType = getSearchType(scanner);
            switch (searchType) {
                case BRUTE_FORCE:
                    bfRunTimes.add(bfParser.parse(getSearchParam(scanner), getNumOfDocs(scanner)));    // Brute force search
                    if ("y".equalsIgnoreCase(displaySearchHistory(scanner))) { parser.print(); }       // Display search history
                    break;
                case LUCENE:
                    if (!Indexer.exists()) {                                  // If document hasn't been parsed then...
                        parser.parse();                                       // Parse the entire document
                        parser.createIndex();                                 // Build Lucene Index
                    }
                    luceneRunTimes.add(searchEngine.search(getSearchParam(scanner), getNumOfDocs(scanner)));  // Lucene search
                    if ("y".equalsIgnoreCase(displayDocuments(scanner))) { searchEngine.displayHits(); }      // Display search history
                    break;
                case SQL_DB:
                    if (!MySQL.exists()) {                                    // If database hasn't been built then...
                        parser.parse();                                       // Parse the entire XML document
                        parser.createSQLDB();                                 // Build the database and insert content
                    }

                    int hits = db.query(getSearchParam(scanner), getYear(scanner));
                    System.out.println("keyword found " + hits + " times");
//                    luceneRunTimes.add(searchEngine.search(getSearchParam(scanner), getNumOfDocs(scanner)));  // Lucene search
//                    if ("y".equalsIgnoreCase(displayDocuments(scanner))) { searchEngine.displayHits(); }      // Display search history
                    break;
                case MONGO_DB:
//                    if (!isParsed) {                                          // If document hasn't been parsed then...
//                        isParsed = true;                                      // Ensure index is only built once
//                        parser.parse();                                       // Parse the entire document
//                        parser.createMongoDB();                                // Build Lucene Index
//                    }
//                    luceneRunTimes.add(searchEngine.search(getSearchParam(scanner), getNumOfDocs(scanner)));  // Lucene search
//                    if ("y".equalsIgnoreCase(displayDocuments(scanner))) { searchEngine.displayHits(); }      // Display search history
                    break;
            }

            if ("y".equalsIgnoreCase(displaySearchTimes(scanner))) { printSearchTimes(); }    // Display search times
        } while ("y".equalsIgnoreCase(performSearch(scanner)));                               // Perform another search?
    }

    /*
     * Gets the search type from the user
     */
    private SearchTypes getSearchType(Scanner scanner) {

        while (true) {
            System.out.print(Config.BF_SEARCH);
            if (scanner.nextLine().equalsIgnoreCase("y")) { return SearchTypes.BRUTE_FORCE; }

            System.out.print(Config.LUCENE_SEARCH);
            if (scanner.nextLine().equalsIgnoreCase("y")) { return  SearchTypes.LUCENE; }

            System.out.print(Config.SQL_DB_SEARCH);
            if (scanner.nextLine().equalsIgnoreCase("y")) { return  SearchTypes.SQL_DB; }

            System.out.print(Config.MONGODB_SEARCH);
            if (scanner.nextLine().equalsIgnoreCase("y")) { return  SearchTypes.MONGO_DB; }

            System.out.println(Config.NO_SELECTION);            // Try again. No selection made
        }
    }

    /*
     * Gets the search parameter from the user
     */
    private String getSearchParam(Scanner scanner) {
        System.out.print(Config.ENTER_KEYWORD);
        return scanner.nextLine();
    }

    private String getYear(Scanner scanner) {
        System.out.print(Config.ENTER_YEAR);
        return scanner.nextLine();
    }

    /*
     * Gets the number of documents to return from the document search
     */
    private int getNumOfDocs(Scanner scanner) {
        while (true) {
            System.out.print(Config.ENTER_NUM_OF_DOCS);
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print(Config.INVALID_INPUT);
            }
        }
    }

    /*
     * Ask the user whether or not to display documents from the Lucene search
     */
    private String displayDocuments(Scanner scanner) {
        System.out.print(Config.DISPLAY_DOCUMENTS);
        return scanner.nextLine();
    }

    private String displaySearchTimes(Scanner scanner) {
        System.out.print(Config.DISPLAY_RUNTIMES);
        return scanner.nextLine();
    }

    private void printSearchTimes() {

        System.out.println("Run time results for Brute force: ");
        for (Long runTime : bfRunTimes) {
            System.out.println(runTime/ Config.MILLIS_TO_SECONDS + " seconds");
        }

        System.out.println("Run time results for Lucene search: ");
        for (Long runTime : luceneRunTimes) {
            System.out.println(runTime/ Config.MILLIS_TO_SECONDS + " seconds");
        }
    }

    /*
     * Ask the user whether or not to display search history
     */
    private String displaySearchHistory(Scanner scanner) {
        System.out.print(Config.DISPLAY_SEARCH_HISTORY);
        return scanner.nextLine();
    }

    /*
     * Ask the user whether or not to do another search
     */
    private String performSearch(Scanner scanner) {
        System.out.print(Config.SEARCH_AGAIN);
        return scanner.nextLine();
    }
}
