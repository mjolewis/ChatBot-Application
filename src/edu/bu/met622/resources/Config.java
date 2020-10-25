package edu.bu.met622.resources;

/**********************************************************************************************************************
 * Application constants
 *
 * @author Michael Lewis
 *********************************************************************************************************************/
public class Config {

    // Parser info
    public static final String OPENING_ROOT_TAG = "<PubmedArticleSet>";
    public static final String CLOSING_ROOT_TAG =  "</PubmedArticleSet>";
    public static final String OUTPUT_FILE = "master_file.xml";
    public static final String PMID = "PMID";
    public static final String PUB_MED_ARTICLE = "PubmedArticle";
    public static final String ARTICLE_TITLE = "ArticleTitle";
    public static final String PUBLICATION_DATE = "PubDate";
    public static final String FILE_SELECTION_ERROR = "Error. Please select more than one file";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.mm";

    // Builder messages
    public static final String START_MESSAGE = "\nMET CS 622\nXML Parsing Engine\nAuthor: Michael Lewis";
    public static final String END_MESSAGE = "\nSimulation Complete\nMerged File Name: " + OUTPUT_FILE;

    // Disk storage
    public static final String SEARCH_HISTORY = "search_history.csv";
    public static final String COMMA_DELIMITER = ",";
    public static final String NEW_LINE_SEPARATOR = "\n";

    // Lucene Indexing Info
    public static final String INDEX_DIRECTORY = "index_directory";

    // Conversions
    public static final double MILLIS_TO_SECONDS = 1000.0;

    // MySQL Database
    public static final String SQL_DB = "jdbc:mysql://localhost:3306/met622?useTimezone=true&serverTimezone=UTC";
    public static final String SQL_USER = "root";
    public static final String SQL_PWD = "root";
    public static final String TABLE_NAME = "articles";
    public static final String CREATE_TABLE = "CREATE TABLE articles" +
                                              "(id VARCHAR(20)," +
                                              "pubDate VARCHAR(20), " +
                                              "title VARCHAR(1000));";
    public static final String CONNECTING = "Connecting to the database...";
    public static final String CONNECTED = "Connection successful...";
    public static final String CREATING_TABLE = "Creating table in the database...";
    public static final String CREATED_TABLE = "Created table successfully...";
    public static final String CLOSE = "Closing connection...";
    public static final String CLOSED = "Connection closed...";

    // Search types
    public static String BF_SEARCH = "Brute Force (y/n): ";
    public static String LUCENE_SEARCH = "Lucene Index (y/n): ";
    public static String SQL_DB_SEARCH = "SQL Database (y/n): ";
    public static String MONGODB_SEARCH = "MongoDB (y/n): ";
}
