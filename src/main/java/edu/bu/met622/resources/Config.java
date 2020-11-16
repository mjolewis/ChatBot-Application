package edu.bu.met622.resources;

/**********************************************************************************************************************
 * Application constants
 *
 * @author Michael Lewis
 *********************************************************************************************************************/
public class Config {

    // Start up messages
    public static final String START_MESSAGE = "\nLaunching ChatBot...";

    // Files
    public static final String FILE_1 = "/Users/mlewis/IntelliJProjects/CS622/ChatBot/pubmed20n1334.xml";
    public static final String FILE_2 = "/Users/mlewis/IntelliJProjects/CS622/ChatBot/pubmed20n1335.xml";

    // Database selection
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
    public static final String RANGE_QUERY = "Range search (y/n): ";
    public static final String RANGE_NOTIFICATION = "You selected a range based search. Enter start month, start year, end month, end year";
    public static final String ENTER_START_MONTH = "Enter start month: ";
    public static final String ENTER_START_YEAR = "Enter start year: ";
    public static final String ENTER_END_MONTH = "Enter end month: ";
    public static final String ENTER_END_YEAR = "Enter end year: ";

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

    // Shared DB
    public static final String POPULATING_DB = "Populating database...";

    // MySQL Database
    public static final String SQL_DB = "jdbc:mysql://localhost:3306/met622?useTimezone=true&serverTimezone=UTC";
    public static final String SQL_USER = "root";
    public static final String SQL_PWD = "root";
    public static final String CONNECTING = "Connecting to the database...";
    public static final String CONNECTED = "Connection successful...";
    public static final String CREATING_TABLE = "Creating table in the database...";
    public static final String CREATED_TABLE = "Created table successfully...";
    public static final String RECORDS_INSERTED = " records inserted";
    public static final String IN_YEAR_QUERY = "Single year search (y/n): ";
    public static final String TABLE_NAME = "articles";
    public static final String CREATE_TABLE = "CREATE TABLE articles" +
                                              "(id VARCHAR(20) PRIMARY KEY," +
                                              "month VARCHAR(20)," +
                                              "year VARCHAR(20), " +
                                              "title VARCHAR(1000));";

    // MongoDB
    public static final String MONGO_DB = "met622";
    public static final String COLLECTION = "articles";
    public static final String DOCUMENT_ID = "id";
    public static final String DOCUMENT_MONTH = "month";
    public static final String DOCUMENT_YEAR = "year";
    public static final String DOCUMENT_DATE = "date";
    public static final String DOCUMENT_TITLE = "title";
    public static final String DUPLICATE_ENTRY = "Duplicate entry";

    // Cleanup messages
    public static final String CLOSING_CONNECTIONS = "\nClosing Database Connections...";
    public static final String CLOSED_CONNECTIONS = "\nDatabase Connections Closed...";
    public static final String END_MESSAGE = "\nTerminated ChatBot Session\n";
}
