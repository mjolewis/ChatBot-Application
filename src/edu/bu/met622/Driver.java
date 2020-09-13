package edu.bu.met622;

import edu.bu.met622.sharedresources.Constants;
import edu.bu.met622.model.Article;
import edu.bu.met622.utils.FileMerger;
import edu.bu.met622.utils.PubMedParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**********************************************************************************************************************
 * Main entry point
 *
 * @author Michael Lewis
 *********************************************************************************************************************/
public class Driver {

    public static void main(String[] args) {
        mergeXML();
        parseXML();
    }

    /*
     * Merge multiple XML documents into one
     */
    public static void mergeXML() {
        ArrayList<String> fileContainer = new ArrayList<>();
        fileContainer.add("pubmed20n1334.xml");
        fileContainer.add("pubmed20n1335.xml");
        fileContainer.add("pubmed20n1335 copy.xml");

        FileMerger fileMerger = new FileMerger(fileContainer, Constants.DEFAULT_FILE_NAME);

        try {
            fileMerger.merge();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Search an XML document for a search parameter specified by the user
     */
    public static void parseXML() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter XML search parameter: ");
        String searchParam = scanner.nextLine();

        PubMedParser pubMedParser = new PubMedParser();
        List<Article> articles = pubMedParser.parse(searchParam);

        // Print all results to the console
        for (Article article : articles) {
            System.out.println(article);
        }
    }
}
