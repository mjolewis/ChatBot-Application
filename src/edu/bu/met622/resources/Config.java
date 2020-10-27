package edu.bu.met622.resources;

/**********************************************************************************************************************
 * Application constants
 *
 * @author Michael Lewis
 *********************************************************************************************************************/
public class Config {

    // Start up messages
    public static final String START_MESSAGE = "\nLaunching ChatBot...";
    public static String BF_SEARCH = "Brute Force (y/n): ";
    public static String LUCENE_SEARCH = "Lucene Index (y/n): ";
    public static String SQL_DB_SEARCH = "SQL Database (y/n): ";
    public static String MONGODB_SEARCH = "MongoDB (y/n): ";
    public static String NO_SELECTION = "No selection made. Try again...";

    // User input
    public static final String ENTER_KEYWORD = "Enter search parameter: ";
    public static final String ENTER_YEAR = "Enter year of search: ";
    public static final String ENTER_NUM_OF_DOCS = "Number of docs: ";
    public static final String INVALID_INPUT = "Invalid input. Try Again.";
    public static final String DISPLAY_DOCUMENTS = "Display documents (y/n)? ";
    public static final String DISPLAY_RUNTIMES = "Display run times (y/n)? ";
    public static final String DISPLAY_SEARCH_HISTORY = "Display search history (y/n)? ";
    public static final String SEARCH_AGAIN = "Search again (y/n)? ";

    // Parser
    public static final String OPENING_ROOT_TAG = "<PubmedArticleSet>";
    public static final String CLOSING_ROOT_TAG =  "</PubmedArticleSet>";
    public static final String OUTPUT_FILE = "master_file.xml";
    public static final String PMID = "PMID";
    public static final String PUB_MED_ARTICLE = "PubmedArticle";
    public static final String ARTICLE_TITLE = "ArticleTitle";
    public static final String YEAR = "Year";
    public static final String MONTH = "Month";
    public static final String FILE_SELECTION_ERROR = "Error. Please select more than one file";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.mm";

    // Disk storage
    public static final String SEARCH_HISTORY = "search_history.csv";
    public static final String COMMA_DELIMITER = ",";
    public static final String NEW_LINE_SEPARATOR = "\n";

    // Lucene Indexing
    public static final String INDEX_DIRECTORY = "index_directory";

    // Conversions
    public static final double MILLIS_TO_SECONDS = 1000.0;

    // MySQL Database
    public static final String SQL_DB = "jdbc:mysql://localhost:3306/met622?useTimezone=true&serverTimezone=UTC";
    public static final String SQL_USER = "root";
    public static final String SQL_PWD = "root";
    public static final String CONNECTING = "Connecting to the database...";
    public static final String CONNECTED = "Connection successful...";
    public static final String CREATING_TABLE = "Creating table in the database...";
    public static final String CREATED_TABLE = "Created table successfully...";
    public static final String RECORDS_INSERTED = " records inserted";
    public static final String TABLE_NAME = "articles";
    public static final String CREATE_TABLE = "CREATE TABLE articles" +
                                              "(id VARCHAR(20) PRIMARY KEY," +
                                              "month VARCHAR(20)," +
                                              "year VARCHAR(20), " +
                                              "title VARCHAR(1000));";

    // MongoDB

    // Cleanup messages
    public static final String CLOSING_CONNECTIONS = "\nClosing Database Connections...";
    public static final String CLOSED_CONNECTIONS = "\nDatabase Connections Closed...";
    public static final String END_MESSAGE = "\nTerminated ChatBot Session\n";
}
