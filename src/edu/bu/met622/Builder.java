package edu.bu.met622;

import edu.bu.met622.searchlib.SearchEngine;
import edu.bu.met622.sharedresources.Constants;
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

    /**
     * Initialize a new Builder
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this Builder
     */
    public Builder() {
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
        XMLParser XMLParser = new XMLParser();
        SearchEngine searchEngine = new SearchEngine();
        boolean isParsed = false;

        do {

            if ("0".equals(getSearchType(scanner))) {                     // Brute force search
                XMLParser.parse(getSearchParam(scanner));
                isParsed = true;
            } else {                                                      // Rich text search w/ Lucene Index
                if (isParsed) {                                           // Index already built, so don't parse again
                    searchEngine.search(getSearchParam(scanner), getNumOfDocs(scanner));
                } else {
                    XMLParser.parse();                                    // Parse and build index for the first time
                    searchEngine.search(getSearchParam(scanner), getNumOfDocs(scanner));
                    isParsed = true;
                }
            }

            if ("y".equalsIgnoreCase(displaySearchHistory(scanner))) {
                XMLParser.print();
            }

        } while ("y".equalsIgnoreCase(performSearch(scanner)));
    }

    private String getSearchType(Scanner scanner) {
        System.out.println("\nEnter search type");
        System.out.print("0 for Brute Force; 1 for Lucene: ");
        return scanner.nextLine();
    }

    private String getSearchParam(Scanner scanner) {
        System.out.print("Enter search parameter: ");
        return scanner.nextLine();
    }

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

    private String displaySearchHistory(Scanner scanner) {
        System.out.print("Display search history (y/n)? ");
        return scanner.nextLine();
    }

    private String performSearch(Scanner scanner) {
        System.out.print("Search again (y/n)? ");
        return scanner.nextLine();
    }
}
