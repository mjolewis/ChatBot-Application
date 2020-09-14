package edu.bu.met622;

import edu.bu.met622.model.Article;
import edu.bu.met622.sharedresources.Constants;
import edu.bu.met622.utils.FileMerger;
import edu.bu.met622.utils.Parser;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    public Builder() { }

    /**
     * Get files using a file chooser dialog box and builds the simulation
     */
    public void build() {
        JFileChooser jFileChooser = new JFileChooser(FileSystemView.getFileSystemView());
        jFileChooser.setMultiSelectionEnabled(true);
        int returnValue = jFileChooser.showOpenDialog(null);

        File[] selectedFiles = null;
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFiles = jFileChooser.getSelectedFiles();

            mergeXML(selectedFiles);                                 // Start simulation only if files were selected
            parseXML();
        } else {                                                     // No files selected
            System.out.println(Constants.FILE_SELECTION_ERROR);
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
    public void mergeXML(File[] selectedFiles) {
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
    public void parseXML() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter XML search parameter: ");
        String searchParam = scanner.nextLine();

        Parser parser = new Parser();
        List<Article> articles = parser.parse(searchParam);

        for (Article article : articles) {                           // Print articles to the console
            System.out.println(article);
        }
    }
}
