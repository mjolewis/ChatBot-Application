package main.java.edu.bu.met622.utils;

import main.java.edu.bu.met622.database.MongoDB;
import main.java.edu.bu.met622.database.MySQL;
import main.java.edu.bu.met622.searchlib.Indexer;
import main.java.edu.bu.met622.resources.Config;
import main.java.edu.bu.met622.model.Article;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**********************************************************************************************************************
 * Parse XML trees using pull parsing, which allows the client to control the application thread.
 *
 * @author Michael Lewis
 *********************************************************************************************************************/
public class XMLParser extends DefaultHandler {
    private String fileName;                                    // File to be searched
    private List<Article> articles;                             // A collection of every article in the input file
    private Storage storage;                                    // Persist search history
    private Indexer indexer;                                    // Builds a Lucene Index

    /**
     * Initialize a new Parser
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this new XMLParser
     */
    public XMLParser() {

        fileName = Config.OUTPUT_FILE;
        articles = new ArrayList<>();
        storage = new Storage();
        indexer = new Indexer();
    }

    /**
     * Initialize a new Parser with the file to be parsed
     *
     * @param fileName Name and extension of the file to parse
     * @param articles A list of articles
     * @param storage  An object capable of storing data in memory and on disk
     * @throws OutOfMemoryError Indicates insufficient memory for this new XMLParser
     */
    public XMLParser(String fileName, ArrayList<Article> articles, Storage storage) {

        this.fileName = fileName;
        this.articles = articles;
        this.storage = storage;
    }

    /**
     * Parses an XML file for Articles and passes those Articles into a Lucene Index, which can be used for efficient
     * searching.
     */
    public void parse() {
        boolean isID = false;
        boolean isMonth = false;
        boolean isYear = false;
        boolean isTitle = false;
        String id = "";
        String month = "";
        String year = "";
        String title = "";

        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new FileInputStream(fileName));

            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();

                switch(xmlEvent.getEventType()) {

                    case XMLStreamConstants.START_ELEMENT:
                        StartElement startElement = xmlEvent.asStartElement();
                        String qName = startElement.getName().getLocalPart();

                        if (qName.equalsIgnoreCase(Config.PMID)) {
                            isID = true;
                        } else if (qName.equalsIgnoreCase(Config.MONTH)) {
                            isMonth = true;
                        } else if (qName.equalsIgnoreCase(Config.YEAR)) {
                            isYear = true;
                        } else if (qName.equalsIgnoreCase(Config.ARTICLE_TITLE)) {
                            isTitle = true;
                        }
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        Characters characters = xmlEvent.asCharacters();

                        if (isID) {
                            id = characters.getData();
                            isID = false;
                        } else if (isMonth) {
                            month = characters.getData();
                            isMonth = false;
                        } else if (isYear) {
                            year = characters.getData();
                            isYear = false;
                        } else if (isTitle) {
                            title = characters.getData();
                            isTitle = false;
                        }
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        EndElement endElement = xmlEvent.asEndElement();

                        if (endElement.getName().getLocalPart().equalsIgnoreCase(Config.PUB_MED_ARTICLE)) {
                            articles.add(new Article(id, month, year, title));
                        }
                        break;
                }
            }
        } catch (FileNotFoundException | XMLStreamException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add articles into the relational database
     */
    public void createSQLDB() {
        MySQL db = MySQL.getInstance();
        db.buildDB(articles);
    }

    /**
     * Add articles into the document database
     */
    public void createMongoDB() {
        MongoDB db = MongoDB.getInstance();
        db.buildDB(articles);
    }

    /**
     * Builds a Lucene Index
     */
    public void createIndex() {
        indexer.createIndex(articles);
    }

    /**
     * Prints the search history to the console
     */
    public void print() {
        storage.print();                                                       // Delegate to the storage object
    }

    /**
     * Accessor method that returns the container of search history data. This includes the search parameter, the
     * frequency, and timestamps
     *
     * @return The users search history
     * @note Each entry in the container stores the time stamps for all searches
     */
    public Map<String, ArrayList<String>> getSearchHistory() {
        return storage.getSearchHistory();                                     // Delegate to the storage object
    }

    /**
     * Accessor method that returns the list of all articles
     *
     * @return A list of articles
     */
    public List<Article> getArticles() {
        return articles;
    }

    /**
     * Mutator method that sets the list of articles. This can be used if you already have a list and you want to add
     * additional articles to the list
     *
     * @param articles A list of articles
     */
    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    //*****************************************************************************************************************
    // Helper methods to persist data
    //*****************************************************************************************************************

    /*
     * Store search history in-memory and on disk
     */
    private void save(String searchParam) {
        LocalDateTime timestamp = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Config.DATE_FORMAT);

        storage.saveToMemory(searchParam, timestamp.format(dateTimeFormatter));
        try {
            storage.saveToDisk(searchParam, timestamp.format(dateTimeFormatter));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}