package edu.bu.met622.sharedresources;

/**********************************************************************************************************************
 * Application constants
 *
 * @author Michael Lewis
 *********************************************************************************************************************/
public class Constants {

    // Parser info
    public static final String OPENING_ROOT_TAG = "<PubmedArticleSet>";
    public static final String CLOSING_ROOT_TAG =  "</PubmedArticleSet>";
    public static final String OUTPUT_FILE = "master_file.xml";
    public static final String PUB_MED_ARTICLE = "PubmedArticle";
    public static final String ARTICLE_TITLE = "ArticleTitle";
    public static final String PUBLICATION_DATE = "PubDate";
    public static final String FILE_SELECTION_ERROR = "Error. Please select more than one file";

    // Builder messages
    public static final String START_MESSAGE = "\nMET CS 622\nXML Parsing Engine\nAuthor: Michael Lewis";
    public static final String END_MESSAGE = "\nSimulation Complete\nMerged File Name: " + OUTPUT_FILE;

    // Disk storage
    public static final String SEARCH_HISTORY = "search_history.csv";
    public static final String COMMA_DELIMITER = ",";
    public static final String NEW_LINE_SEPARATOR = "\n";
}
