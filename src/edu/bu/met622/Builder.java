package edu.bu.met622;

import edu.bu.met622.searchlib.SearchEngine;
import edu.bu.met622.sharedresources.Constants;
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
                    System.out.println(Constants.FILE_SELECTION_ERROR);
                } else {
                    mergeXML(selectedFiles);
                    parseXML();
                    break;
                }
            } else {                                                     // No files selected
                System.out.println(Constants.FILE_SELECTION_ERROR);

                if (returnValue == JFileChooser.CANCEL_OPTION) {         // Break out of the selection process
                    break;
                }
            }

        }
    }

    /**
     * Write a welcome message to the console
     */
    public void startMessage() {
        System.out.println("\n\n************************************************");
        System.out.println(Constants.START_MESSAGE);
        System.out.println("\n************************************************\n");
    }

    /**
     * Write an ending message to the console
     */
    public void endMessage() {
        System.out.println("\n\n************************************************");
        System.out.println(Constants.END_MESSAGE);
        System.out.println("\n************************************************");
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

        FileMerger fileMerger = new FileMerger(fileContainer, Constants.OUTPUT_FILE);
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
        SearchEngine searchEngine = new SearchEngine();
        boolean isParsed = false;

        do {

            String searchType = getSearchType(scanner);
            switch (searchType) {
                case "0":
                    bfRunTimes.add(bfParser.parse(getSearchParam(scanner), getNumOfDocs(scanner)));    // Brute force search
                    if ("y".equalsIgnoreCase(displaySearchHistory(scanner))) { parser.print(); }       // Display search history
                    break;
                case "1":
                    if (!isParsed) {                                          // If document hasn't been parsed then...
                        isParsed = true;                                      // Ensure index is only built once
                        parser.parse();                                       // Parse the entire document
                        parser.createIndex();                                 // Build Lucene Index
                    }
                    luceneRunTimes.add(searchEngine.search(getSearchParam(scanner), getNumOfDocs(scanner)));  // Lucene search
                    if ("y".equalsIgnoreCase(displayDocuments(scanner))) { searchEngine.displayHits(); }      // Display search history
                    break;
                case "2":
                    if (!isParsed) {                                          // If document hasn't been parsed then...
                        isParsed = true;                                      // Ensure index is only built once
                        parser.parse();                                       // Parse the entire document
                        parser.createSQLDB();                                 // Build Lucene Index
                    }
//                    luceneRunTimes.add(searchEngine.search(getSearchParam(scanner), getNumOfDocs(scanner)));  // Lucene search
//                    if ("y".equalsIgnoreCase(displayDocuments(scanner))) { searchEngine.displayHits(); }      // Display search history
                    break;
                case "3":
                    if (!isParsed) {                                          // If document hasn't been parsed then...
                        isParsed = true;                                      // Ensure index is only built once
                        parser.parse();                                       // Parse the entire document
                        parser.createMongoDB();                                 // Build Lucene Index
                    }
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
    private String getSearchType(Scanner scanner) {

        System.out.print(Constants.BF_SEARCH);
        if (scanner.nextLine().equalsIgnoreCase("y")) { return  "0"; }

        System.out.print(Constants.LUCENE_SEARCH);
        if (scanner.nextLine().equalsIgnoreCase("y")) { return  "1"; }

        System.out.print(Constants.SQL_DB_SEARCH);
        if (scanner.nextLine().equalsIgnoreCase("y")) { return  "2"; }

        System.out.print(Constants.MONGODB_SEARCH);
        if (scanner.nextLine().equalsIgnoreCase("y")) { return  "3"; }

        return "-1";                                            // Indicates invalid input
    }

    /*
     * Gets the search parameter from the user
     */
    private String getSearchParam(Scanner scanner) {
        System.out.print("Enter search parameter: ");
        return scanner.nextLine();
    }

    /*
     * Gets the number of documents to return from the document search
     */
    private int getNumOfDocs(Scanner scanner) {
        while (true) {
            System.out.print("Number of docs: ");
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Try Again.");
            }
        }
    }

    /*
     * Ask the user whether or not to display documents from the Lucene search
     */
    private String displayDocuments(Scanner scanner) {
        System.out.print("Display documents: (y/n)? ");
        return scanner.nextLine();
    }

    private String displaySearchTimes(Scanner scanner) {
        System.out.print("Display run times: (y/n)? ");
        return scanner.nextLine();
    }

    private void printSearchTimes() {

        System.out.println("Run time results for Brute force: ");
        for (Long runTime : bfRunTimes) {
            System.out.println(runTime/Constants.MILLIS_TO_SECONDS + " seconds");
        }

        System.out.println("Run time results for Lucene search: ");
        for (Long runTime : luceneRunTimes) {
            System.out.println(runTime/Constants.MILLIS_TO_SECONDS + " seconds");
        }
    }

    /*
     * Ask the user whether or not to display search history
     */
    private String displaySearchHistory(Scanner scanner) {
        System.out.print("Display search history (y/n)? ");
        return scanner.nextLine();
    }

    /*
     * Ask the user whether or not to do another search
     */
    private String performSearch(Scanner scanner) {
        System.out.print("Search again (y/n)? ");
        return scanner.nextLine();
    }
}
