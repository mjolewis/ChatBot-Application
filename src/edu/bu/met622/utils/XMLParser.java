package edu.bu.met622.utils;

import edu.bu.met622.searchlib.Indexer;
import edu.bu.met622.sharedresources.Constants;
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
    private List<Article> articles;                             // A container of PubMed articles
    private List<Article> allArticles;                          // A container of every article in the input file
    private Storage storage;                                    // Persist search history
    private Indexer indexer;                            // Builds a Lucene Index

    /**
     * Initialize a new Parser
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this new XMLParser
     */
    public XMLParser() {

        fileName = Constants.OUTPUT_FILE;
        articles = new ArrayList<>();
        allArticles = new ArrayList<>();
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
     * Brute force event-based processing of an XML document assigned to this objects fileName member variable
     *
     * @param searchParam A value to be searched
     * @return The runtime of the current search
     */
    public long parse(String searchParam) {
        String pubYear = "";
        String pubID = "";
        String articleTitle = "";
        long startTime = 0;
        long endTime = 0;

        save(searchParam);

        startTime = System.currentTimeMillis();
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new FileInputStream(fileName));

            while (xmlEventReader.hasNext()) {

                XMLEvent xmlEvent = xmlEventReader.nextEvent();

                if (xmlEvent.isStartElement()) {

                    StartElement startElement = xmlEvent.asStartElement();

                    switch (startElement.getName().getLocalPart()) {
                        case Constants.PMID:
                            xmlEvent = xmlEventReader.nextEvent();
                            pubID = xmlEvent.asCharacters().toString();
                            break;
                        case Constants.PUBLICATION_DATE:
                            xmlEventReader.nextTag();           // <PubDate> is followed by the <Year>
                            xmlEvent = xmlEventReader.nextEvent();
                            pubYear = xmlEvent.asCharacters().toString();
                            break;
                        case Constants.ARTICLE_TITLE:
                            xmlEvent = xmlEventReader.nextEvent();
                            if (!xmlEvent.isCharacters()) {
                                xmlEvent = xmlEventReader.nextEvent();
                            }
                            articleTitle = xmlEvent.asCharacters().toString();
                            break;
                    }
                } else if (xmlEvent.isEndElement()) {
                    EndElement endElement = xmlEvent.asEndElement();

                    if (endElement.getName().getLocalPart().equals(Constants.PUB_MED_ARTICLE)) {
                        if (articleTitle.toLowerCase().contains(searchParam.toLowerCase())) {
                            articles.add(new Article(pubID, pubYear, articleTitle));
                        }
                    }
                }
            }

            endTime = System.currentTimeMillis();
        } catch (FileNotFoundException | XMLStreamException e) {
            e.printStackTrace();
        }

        return endTime - startTime;                                       // Runtime of current search
    }

    /**
     * Parses an XML file for Articles and passes those Articles into a Lucene Index, which can be used for efficient
     * searching
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
                        case Constants.PMID:
                            xmlEvent = xmlEventReader.nextEvent();
                            pubID = xmlEvent.asCharacters().toString();
                            break;
                        case Constants.PUBLICATION_DATE:
                            xmlEventReader.nextTag();
                            xmlEvent = xmlEventReader.nextEvent();
                            pubYear = xmlEvent.asCharacters().toString();
                            break;
                        case Constants.ARTICLE_TITLE:
                            xmlEvent = xmlEventReader.nextEvent();
                            if (!xmlEvent.isCharacters()) {
                                xmlEvent = xmlEventReader.nextEvent();
                            }
                            articleTitle = xmlEvent.asCharacters().toString();
                            break;
                    }
                } else if (xmlEvent.isEndElement()) {
                    EndElement endElement = xmlEvent.asEndElement();

                    if (endElement.getName().getLocalPart().equals(Constants.PUB_MED_ARTICLE)) {
                        allArticles.add(new Article(pubID, pubYear, articleTitle));
                    }
                }
            }

            indexer.createIndex(allArticles);
        } catch (FileNotFoundException | XMLStreamException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prints the search history to the console
     */
    public void print() {
        storage.print();                      // Delegate to the storage object
    }

    /**
     * Accessor method that returns the container of search history data. This includes the search parameter, the
     * frequency, and timestamps
     *
     * @return The users search history
     * @note Each entry in the container stores the time stamps for all searches
     */
    public Map<String, ArrayList<String>> getSearchHistory() {
        return storage.getSearchHistory();                      // Delegate to the storage object
    }

    /**
     * Accessor method that returns the list of articles containing the search string
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
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);

        storage.saveToMemory(searchParam, timestamp.format(dateTimeFormatter));
        try {
            storage.saveToDisk(searchParam, timestamp.format(dateTimeFormatter));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}