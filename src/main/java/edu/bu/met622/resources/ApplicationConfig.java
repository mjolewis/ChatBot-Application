package edu.bu.met622.resources;

/**********************************************************************************************************************
 * Application constants
 *
 * @author Michael Lewis
 *********************************************************************************************************************/
public class ApplicationConfig {

    // Start up messages
    public static final String START_MESSAGE = "\nLaunching ChatBot...";

    // Files
    public static final String FILE_1 = "/Users/mlewis/IntelliJProjects/CS622/ChatBot/pubmed20n1334.xml";
    public static final String FILE_2 = "/Users/mlewis/IntelliJProjects/CS622/ChatBot/pubmed20n1335.xml";
    public static final String RUNTIME_LOG = "/Users/mlewis/IntelliJProjects/CS622/ChatBot/Logs/runtime.csv";
    public static final String ERROR_LOG = "/Users/mlewis/IntelliJProjects/CS622/ChatBot/Logs/errors.csv";

    // Parser
    public static final String OPENING_ROOT_TAG = "<PubmedArticleSet>";
    public static final String CLOSING_ROOT_TAG =  "</PubmedArticleSet>";
    public static final String OUTPUT_FILE = "master_file.xml";
    public static final String PMID = "PMID";
    public static final String PUB_MED_ARTICLE = "PubmedArticle";
    public static final String ARTICLE_TITLE = "ArticleTitle";
    public static final String YEAR = "Year";
    public static final String MONTH = "Month";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.mm";

    // Disk storage
    public static final String SEARCH_HISTORY = "search_history.csv";
    public static final String COMMA_DELIMITER = ",";
    public static final String NEW_LINE_SEPARATOR = "\n";

    // Brute Force
    public static final String BRUTEFORCE = "Brute Force";

    // Lucene Indexing
    public static final String LUCENE_INDEX = "Lucene Index";
    public static final String INDEX_DIRECTORY = "index_directory";

    // Shared DB
    public static final String POPULATING_DB = "Populating database...";

    // MySQL Database
    public static final String MYSQL = "MySQL";
    public static final String SQL_DB = "jdbc:mysql://localhost:3306/met622?useTimezone=true&serverTimezone=UTC";
    public static final String SQL_USER = "root";
    public static final String SQL_PWD = "root";
    public static final String CONNECTING = "Connecting to the database...";
    public static final String CONNECTED = "Connection successful...";
    public static final String CREATING_TABLE = "Creating table in the database...";
    public static final String CREATED_TABLE = "Created table successfully...";
    public static final String TABLE_NAME = "articles";
    public static final String CREATE_TABLE = "CREATE TABLE articles" +
                                              "(id VARCHAR(20) PRIMARY KEY," +
                                              "month VARCHAR(20)," +
                                              "year VARCHAR(20), " +
                                              "title VARCHAR(1000));";

    // MongoDB
    public static final String MONGODB = "MongoDB";
    public static final String MONGO_DB = "met622";
    public static final String COLLECTION = "articles";
    public static final String DOCUMENT_ID = "id";
    public static final String DOCUMENT_MONTH = "month";
    public static final String DOCUMENT_YEAR = "year";
    public static final String DOCUMENT_DATE = "date";
    public static final String DOCUMENT_TITLE = "title";
    public static final String DUPLICATE_ENTRY = "Duplicate entry";

    // Chart info
    public static final String CHART_TITLE = "Runtime Results";

    // Cleanup messages
    public static final String CLOSING_CONNECTIONS = "\nClosing Database Connections...";
    public static final String CLOSED_CONNECTIONS = "\nDatabase Connections Closed...";
    public static final String END_MESSAGE = "\nTerminated ChatBot Session\n";
}
