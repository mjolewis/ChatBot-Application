package edu.bu.met622.utils;

import edu.bu.met622.database.MySQL;
import edu.bu.met622.searchlib.Indexer;
import edu.bu.met622.resources.Config;
import edu.bu.met622.model.Article;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
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
        String pubYear = "";
        String pubID = "";
        String articleTitle = "";

        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new FileInputStream(fileName));

            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();

                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();

                    switch (startElement.getName().getLocalPart()) {
                        case Config.PMID:
                            xmlEvent = xmlEventReader.nextEvent();
                            pubID = xmlEvent.asCharacters().toString();
                            break;
                        case Config.PUBLICATION_DATE:
                            xmlEventReader.nextTag();
                            xmlEvent = xmlEventReader.nextEvent();
                            pubYear = xmlEvent.asCharacters().toString();
                            break;
                        case Config.ARTICLE_TITLE:
                            xmlEvent = xmlEventReader.nextEvent();
                            if (!xmlEvent.isCharacters()) {
                                xmlEvent = xmlEventReader.nextEvent();
                            }
                            articleTitle = xmlEvent.asCharacters().toString();
                            break;
                    }
                } else if (xmlEvent.isEndElement()) {
                    EndElement endElement = xmlEvent.asEndElement();

                    if (endElement.getName().getLocalPart().equals(Config.PUB_MED_ARTICLE)) {
                        articles.add(new Article(pubID, pubYear, articleTitle));
                    }
                }
            }
        } catch (FileNotFoundException | XMLStreamException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add articles into the SQL database
     */
    public void createSQLDB() {
        MySQL db = MySQL.getInstance();
        db.buildDB(articles);
    }

    public void createMongoDB() {

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