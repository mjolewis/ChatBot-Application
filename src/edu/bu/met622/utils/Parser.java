package edu.bu.met622.utils;

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
import java.sql.Timestamp;
import java.util.*;

/**********************************************************************************************************************
 * Parse XML trees using pull parsing, which allows the client to control the application thread.
 *
 * @author Michael Lewis
 *********************************************************************************************************************/
public class Parser extends DefaultHandler {
    private String fileName;                                    // File to be searched
    private List<Article> articles;                             // A container of PubMed articles
    private Storage storage;                                    // Persist search history

    /**
     * Initialize a new Parser
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this new XMLParser
     */
    public Parser() {

        fileName = Constants.OUTPUT_FILE;
        articles = new ArrayList<>();
        storage = new Storage();
    }

    /**
     * Initialize a new Parser with the file to be parsed
     *
     * @param fileName Name and extension of the file to parse
     * @param articles A list of articles
     * @param storage An object capable of storing data in memory and on disk
     * @throws OutOfMemoryError Indicates insufficient memory for this new XMLParser
     */
    public Parser(String fileName, ArrayList<Article> articles, Storage storage) {

        this.fileName = fileName;
        this.articles = articles;
        this.storage = storage;
    }

    /**
     * Event-based processing of an XML document assigned to this objects fileName member variable
     *
     * @param searchParam A value to be searched
     */
    public void parse(String searchParam) {
        String year = "";
        String title = "";

        save(searchParam);

        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new FileInputStream(fileName));

            while (xmlEventReader.hasNext()) {

                XMLEvent xmlEvent = xmlEventReader.nextEvent();

                if (xmlEvent.isStartElement()) {

                    StartElement startElement = xmlEvent.asStartElement();

                    switch (startElement.getName().getLocalPart()) {
                        case Constants.PUBLICATION_DATE:
                            xmlEventReader.nextTag();           // <PubDate> is followed by the <Year>
                            xmlEvent = xmlEventReader.nextEvent();
                            year = xmlEvent.asCharacters().toString();
                            break;
                        case Constants.ARTICLE_TITLE:
                            xmlEvent = xmlEventReader.nextEvent();
                            title = xmlEvent.toString().toLowerCase();
                            break;
                    }
                } else if (xmlEvent.isEndElement()) {
                    EndElement endElement = xmlEvent.asEndElement();

                    if (endElement.getName().getLocalPart().equals(Constants.PUB_MED_ARTICLE)) {
                        if (title.contains(searchParam.toLowerCase())) {
                            articles.add(new Article(year, title));
                        }
                    }
                }
            }
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
    public Map<String, ArrayList<Object>> getSearchHistory() {
        return storage.getSearchHistory();                 // Delegate to the storage object
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
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        storage.saveToMemory(searchParam, timestamp);
        try {
            storage.saveToDisk(searchParam, timestamp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}