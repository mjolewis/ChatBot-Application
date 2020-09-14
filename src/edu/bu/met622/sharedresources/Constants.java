package edu.bu.met622.sharedresources;

/**********************************************************************************************************************
 * Application constants
 *
 * @author Michael Lewis
 *********************************************************************************************************************/
public class Constants {
    public static final String CLOSING_ROOT_TAG =  "</PubmedArticleSet>";
    public static final String OUTPUT_FILE = "masterFile.xml";
    public static final String DEFAULT_SEARCH_PARAM = "Covid-19";
    public static final String PUB_MED_ARTICLE = "PubmedArticle";
    public static final String ARTICLE_TITLE = "ArticleTitle";
    public static final String PUBLICATION_DATE = "PubDate";
    public static final String FILE_SELECTION_ERROR = "Error. No files selected.";

    // Builder messages
    public static final String START_MESSAGE = "\nMET CS 622\nXML Parsing Engine\nAuthor: Michael Lewis";
    public static final String END_MESSAGE = "\nSimulation Complete\nMerged File Name: " + OUTPUT_FILE;
}
