package edu.bu.met622;

import edu.bu.met622.database.MongoDB;
import edu.bu.met622.database.MySQL;
import edu.bu.met622.resources.DatabaseOptions;
import edu.bu.met622.resources.QueryOptions;
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
        MySQL mySQLDB = MySQL.getInstance();
        MongoDB mongoDB = MongoDB.getInstance();
        SearchEngine searchEngine = new SearchEngine();
        double runTime = 0.0;

        do {

            DatabaseOptions searchType = getSearchType(scanner);
            switch (searchType) {
                case BRUTE_FORCE:
                    runTime = bfParser.parse(getSearchParam(scanner), getNumOfDocs(scanner));            // Brute force search
                    if ("y".equalsIgnoreCase(displaySearchHistory(scanner))) { bfParser.print(); }       // Display search history
                    if ("y".equalsIgnoreCase(displaySearchTimes(scanner))) { printSearchTimes(runTime); }    // Display search times
                    break;
                case LUCENE:
                    if (!Indexer.exists()) {                                  // If document hasn't been parsed then...
                        parser.parse();                                       // Parse the entire document
                        parser.createIndex();                                 // Build Lucene Index
                    }
                    runTime = searchEngine.search(getSearchParam(scanner), getNumOfDocs(scanner));            // Lucene search
                    if ("y".equalsIgnoreCase(displayDocuments(scanner))) { searchEngine.displayHits(); }      // Display search history
                    if ("y".equalsIgnoreCase(displaySearchTimes(scanner))) { printSearchTimes(runTime); }     // Display search times
                    break;
                case SQL_DB:
                    if (!MySQL.exists()) {                                    // If database hasn't been built then...
                        parser.parse();                                       // Parse the entire XML document
                        parser.createSQLDB();                                 // Build the database and insert content
                    }

                    String keyword = getSearchParam(scanner);
                    QueryOptions queryOptions = getQueryType(scanner);
                    int hits;
                    switch (queryOptions) {
                        case IN_YEAR_QUERY:
                            String year = getYear(scanner);
                            hits = mySQLDB.query(keyword, year);
                            System.out.println(year + ":" + hits);
                            break;
                        case RANGE_QUERY:
                            System.out.println(Config.RANGE_NOTIFICATION);
                            String startMonth = getStartMonth(scanner);
                            String startYear = getStartYear(scanner);
                            String endMonth = getEndMonth(scanner);
                            String endYear = getEndYear(scanner);
                            hits = mySQLDB.query(keyword, startMonth, startYear, endMonth, endYear);
                            System.out.println(startMonth + "/" + startYear + "-" + endMonth + "/" + endYear + ": " + hits);
                            break;
                    }

                    runTime = mySQLDB.getRunTime();
                    if ("y".equalsIgnoreCase(displaySearchTimes(scanner))) { printSearchTimes(runTime); }     // Display search times
                    break;
                case MONGO_DB:
                    if (!MongoDB.exists()) {                                   // If document hasn't been parsed then...
                        parser.parse();                                        // Parse the entire document
                        parser.createMongoDB();                                // Build Lucene Index
                    }

                    runTime = mongoDB.getRunTime();
                    if ("y".equalsIgnoreCase(displaySearchTimes(scanner))) { printSearchTimes(runTime); }     // Display search times
                    break;
            }
        } while ("y".equalsIgnoreCase(performSearch(scanner)));                // Perform another search?
    }

    /*
     * Gets the search type from the user
     */
    private DatabaseOptions getSearchType(Scanner scanner) {

        while (true) {
            System.out.print(Config.BF_SEARCH);
            if (scanner.nextLine().equalsIgnoreCase("y")) { return DatabaseOptions.BRUTE_FORCE; }

            System.out.print(Config.LUCENE_SEARCH);
            if (scanner.nextLine().equalsIgnoreCase("y")) { return  DatabaseOptions.LUCENE; }

            System.out.print(Config.SQL_DB_SEARCH);
            if (scanner.nextLine().equalsIgnoreCase("y")) { return  DatabaseOptions.SQL_DB; }

            System.out.print(Config.MONGODB_SEARCH);
            if (scanner.nextLine().equalsIgnoreCase("y")) { return  DatabaseOptions.MONGO_DB; }

            System.out.println(Config.NO_SELECTION);            // Try again. No selection made
        }
    }

    private QueryOptions getQueryType(Scanner scanner) {

        while (true) {
            System.out.print(Config.IN_YEAR_QUERY);
            if (scanner.nextLine().equalsIgnoreCase("y")) { return QueryOptions.IN_YEAR_QUERY; }

            System.out.print(Config.RANGE_QUERY);
            if (scanner.nextLine().equalsIgnoreCase("y")) { return QueryOptions.RANGE_QUERY; }

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

    /*
     * Gets the starting month for a range based query
     */
    private String getStartMonth(Scanner scanner) {
        System.out.print(Config.ENTER_START_MONTH);
        return scanner.nextLine();
    }

    /*
     * Gets the ending month for a range based query
     */
    private String getEndMonth(Scanner scanner) {
        System.out.print(Config.ENTER_END_MONTH);
        return scanner.nextLine();
    }

    /*
     * Gets the start year for a range based query
     */
    private String getStartYear(Scanner scanner) {
        System.out.print(Config.ENTER_START_YEAR);
        return scanner.nextLine();
    }

    /*
     * Gets the ending year for a range based query
     */
    private String getEndYear(Scanner scanner) {
        System.out.print(Config.ENTER_END_YEAR);
        return scanner.nextLine();
    }

    /*
     * Gets the year to search in for a single year query
     */
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

    /*
     * Ask the user whether or not to display the length of the search
     */
    private String displaySearchTimes(Scanner scanner) {
        System.out.print(Config.DISPLAY_RUNTIMES);
        return scanner.nextLine();
    }

    /*
     * Print the length of the search to the console
     */
    private void printSearchTimes(double searchTime) {
        System.out.print("Run time results: ");
        System.out.println(searchTime / Config.MILLIS_TO_SECONDS + " seconds");
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
