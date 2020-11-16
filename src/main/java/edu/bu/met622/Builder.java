package edu.bu.met622;

import edu.bu.met622.database.MongoDB;
import edu.bu.met622.database.MySQL;
import edu.bu.met622.resources.DbSelector;
import edu.bu.met622.resources.QuerySelector;
import edu.bu.met622.database.LuceneIndex;
import edu.bu.met622.database.LuceneSearch;
import edu.bu.met622.resources.Config;
import edu.bu.met622.utils.BFParser;
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
        parseXML();
//        JFileChooser jFileChooser = new JFileChooser(FileSystemView.getFileSystemView());
//        jFileChooser.setMultiSelectionEnabled(true);
//
//        File[] selectedFiles;
//        while (true) {
//            int returnValue = jFileChooser.showOpenDialog(null);
//
//            if (returnValue == JFileChooser.APPROVE_OPTION) {
//                selectedFiles = jFileChooser.getSelectedFiles();
//
//                if (selectedFiles.length == 1) {                         // Only merge if more than one was selected
//                    System.out.println(Config.FILE_SELECTION_ERROR);
//                } else {
//                    mergeXML(selectedFiles);
//                    parseXML();
//                    break;
//                }
//            } else {                                                      // No files selected
//                System.out.println(Config.FILE_SELECTION_ERROR);
//
//                if (returnValue == JFileChooser.CANCEL_OPTION) {          // Break out of the selection process
//                    break;
//                }
//            }
//
//        }
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
//        ArrayList<String> fileContainer = new ArrayList<>();
//        for (File file : selectedFiles) {
//            fileContainer.add(file.getPath());
//        }

        FileMerger fileMerger = new FileMerger(selectedFiles, Config.OUTPUT_FILE);
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
        MongoDB mongoDB;
        LuceneSearch luceneSearch = new LuceneSearch();
        String keyword;
        QuerySelector querySelector;
        int hits;
        double runTime;

        do {

            DbSelector searchType = getSearchType(scanner);
            switch (searchType) {
                case BRUTE_FORCE:
                    runTime = bfParser.parse(getSearchParam(scanner), getNumOfDocs(scanner));            // Brute force search
                    if ("y".equalsIgnoreCase(displaySearchHistory(scanner))) { bfParser.print(); }       // Display search history
                    if ("y".equalsIgnoreCase(displayRunTime(scanner))) { printRunTime(runTime); }    // Display search times
                    break;
                case LUCENE:
                    if (!LuceneIndex.exists()) {                              // If document hasn't been parsed then...
                        parser.parse();                                   // Parse the entire document
                        parser.createIndex();                             // Build Lucene Index
                    }
                    runTime = luceneSearch.search(getSearchParam(scanner), getNumOfDocs(scanner));            // Lucene search
                    if ("y".equalsIgnoreCase(displayDocuments(scanner))) { luceneSearch.displayHits(); }      // Display search history
                    if ("y".equalsIgnoreCase(displayRunTime(scanner))) { printRunTime(runTime); }     // Display search times
                    break;
                case SQL_DB:
                    if (!MySQL.exists()) {                                // If database hasn't been built then...
                        parser.parse();                                   // Parse the entire XML document
                        parser.createSQLDB();                             // Build the database and insert content
                    }

                    keyword = getSearchParam(scanner);
                    querySelector = getQueryType(scanner);
                    switch (querySelector) {
                        case IN_YEAR_QUERY:
                            String year = getYear(scanner);
                            hits = mySQLDB.query(keyword, year);
                            System.out.println(year + ": " + hits);
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
                    if ("y".equalsIgnoreCase(displayRunTime(scanner))) { printRunTime(runTime); }     // Display search times
                    break;
                case MONGO_DB:
                    mongoDB = MongoDB.getInstance();
                    if (!mongoDB.exists()) {                              // If document hasn't been parsed then...
                        parser.parse();                                   // Parse the entire XML document
                        parser.createMongoDB();                           // Build the database and insert content
                    }

                    keyword = getSearchParam(scanner);
                    querySelector = getQueryType(scanner);
                    switch (querySelector) {
                        case IN_YEAR_QUERY:
                            String year = getYear(scanner);
                            hits = mongoDB.query(keyword, year);
                            System.out.println(year + ": " + hits);
                            break;
                        case RANGE_QUERY:
                            System.out.println(Config.RANGE_NOTIFICATION);
                            String startMonth = getStartMonth(scanner);
                            String startYear = getStartYear(scanner);
                            String endMonth = getEndMonth(scanner);
                            String endYear = getEndYear(scanner);
                            hits = mongoDB.query(keyword, startMonth, startYear, endMonth, endYear);
                            System.out.println(startMonth + "/" + startYear + "-" + endMonth + "/" + endYear + ": " + hits);
                            break;
                    }

                    runTime = mongoDB.getRunTime();
                    if ("y".equalsIgnoreCase(displayRunTime(scanner))) { printRunTime(runTime); }     // Display search times
                    break;
            }
        } while ("y".equalsIgnoreCase(performSearch(scanner)));           // Perform another search?
    }

    /*
     * Gets the search type from the user
     */
    private DbSelector getSearchType(Scanner scanner) {

        while (true) {
            System.out.print(Config.BF_SEARCH);
            if (scanner.nextLine().equalsIgnoreCase("y")) { return DbSelector.BRUTE_FORCE; }

            System.out.print(Config.LUCENE_SEARCH);
            if (scanner.nextLine().equalsIgnoreCase("y")) { return  DbSelector.LUCENE; }

            System.out.print(Config.SQL_DB_SEARCH);
            if (scanner.nextLine().equalsIgnoreCase("y")) { return  DbSelector.SQL_DB; }

            System.out.print(Config.MONGODB_SEARCH);
            if (scanner.nextLine().equalsIgnoreCase("y")) { return  DbSelector.MONGO_DB; }

            System.out.println(Config.NO_SELECTION);                      // Try again. No selection made
        }
    }

    private QuerySelector getQueryType(Scanner scanner) {

        while (true) {
            System.out.print(Config.IN_YEAR_QUERY);
            if (scanner.nextLine().equalsIgnoreCase("y")) { return QuerySelector.IN_YEAR_QUERY; }

            System.out.print(Config.RANGE_QUERY);
            if (scanner.nextLine().equalsIgnoreCase("y")) { return QuerySelector.RANGE_QUERY; }

            System.out.println(Config.NO_SELECTION);                      // Try again. No selection made
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
    private String displayRunTime(Scanner scanner) {
        System.out.print(Config.DISPLAY_RUNTIMES);
        return scanner.nextLine();
    }

    /*
     * Print the length of the search to the console
     */
    private void printRunTime(double searchTime) {
        System.out.print("Run time: ");
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
