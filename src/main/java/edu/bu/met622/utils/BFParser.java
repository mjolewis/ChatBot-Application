package edu.bu.met622.utils;

import edu.bu.met622.resources.ApplicationConfig;
import edu.bu.met622.model.Article;
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
 * Illustration of brute force XML parsing
 *
 * @author Michael Lewis
 *********************************************************************************************************************/
public class BFParser extends DefaultHandler {
    private String fileName;                                    // File to be searched
    private List<Article> articles;                             // A container of PubMed articles
    private Storage storage;                                    // Persist search history
    private static Logger logger;                               // Logs application events to files
    private double startTime;                                   // Tracks the runtime of the query
    private double endTime;                                     // Tracks the runtime of the query
    private double runtime;                                     // The total runtime of the query

    /**
     * Initialize a new Parser
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this new XMLParser
     */
    public BFParser() {

        fileName = ApplicationConfig.OUTPUT_FILE;
        articles = new ArrayList<>();
        storage = new Storage();                                     // Store search parameters onto disk
        logger = Logger.getInstance();                               // Log application events to a file
    }

    /**
     * Initialize a new Parser with the file to be parsed
     *
     * @param fileName Name and extension of the file to parse
     * @param articles A list of articles
     * @param storage  An object capable of storing data in memory and on disk
     * @throws OutOfMemoryError Indicates insufficient memory for this new XMLParser
     */
    public BFParser(String fileName, ArrayList<Article> articles, Storage storage) {

        this.fileName = fileName;
        this.articles = articles;
        this.storage = storage;
        logger = Logger.getInstance();                               // Log application events to a file
    }

    /**
     * Brute force event-based processing of an XML document assigned to this objects fileName member variable
     *
     * @param keyword A value to be searched
     * @return The runtime of the current search
     */
    public long parse(String keyword) {

        boolean isID = false;
        boolean isMonth = false;
        boolean isYear = false;
        boolean isTitle = false;
        String id = "";
        String month = "";
        String year = "";
        String title = "";
        int hitCount = 0;

        save(keyword);

        startTime = System.currentTimeMillis();
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new FileInputStream(fileName));

            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();

                switch (xmlEvent.getEventType()) {

                    case XMLStreamConstants.START_ELEMENT:
                        StartElement startElement = xmlEvent.asStartElement();
                        String qName = startElement.getName().getLocalPart();

                        if (qName.equalsIgnoreCase(ApplicationConfig.PMID)) {
                            isID = true;
                        } else if (qName.equalsIgnoreCase(ApplicationConfig.MONTH)) {
                            isMonth = true;
                        } else if (qName.equalsIgnoreCase(ApplicationConfig.YEAR)) {
                            isYear = true;
                        } else if (qName.equalsIgnoreCase(ApplicationConfig.ARTICLE_TITLE)) {
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

                        if (endElement.getName().getLocalPart().equalsIgnoreCase(ApplicationConfig.PUB_MED_ARTICLE)) {
                            if (title.toLowerCase().contains(keyword.toLowerCase())) {
                                ++hitCount;                                          // Track the number of hits
                                articles.add(new Article(id, month, year, title));   // Track articles in container
                            }
                        }
                        break;
                }
            }

            endTime = System.currentTimeMillis();
            runtime = endTime - startTime;
            logger.runtime(ApplicationConfig.BRUTEFORCE, runtime);
        } catch (FileNotFoundException | XMLStreamException e) {
            e.printStackTrace();
        }
        return hitCount;                                                       // Number of times keyword was found
    }

    /**
     * Brute force event-based processing of an XML document assigned to this objects fileName member variable
     *
     * @param keyword    A value to be searched
     * @param startYear  The first year within the search range
     * @param endYear    The last year within the search range
     * @return The runtime of the current search
     */
    public long parse(String keyword, String startYear, String endYear) {

        boolean isID = false;
        boolean isMonth = false;
        boolean isYear = false;
        boolean isTitle = false;
        boolean inRange = false;
        String id = "";
        String month = "";
        String year = "";
        String title = "";
        int hitCount = 0;
        int numericStartYear = Integer.parseInt(startYear);          // Only count articles within the range
        int numericEndYear = Integer.parseInt(endYear);              // Only count articles within the range

        save(keyword);

        startTime = System.currentTimeMillis();
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new FileInputStream(fileName));

            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();

                switch (xmlEvent.getEventType()) {

                    case XMLStreamConstants.START_ELEMENT:
                        StartElement startElement = xmlEvent.asStartElement();
                        String qName = startElement.getName().getLocalPart();

                        if (qName.equalsIgnoreCase(ApplicationConfig.PMID)) {
                            isID = true;
                        } else if (qName.equalsIgnoreCase(ApplicationConfig.MONTH)) {
                            isMonth = true;
                        } else if (qName.equalsIgnoreCase(ApplicationConfig.YEAR)) {
                            isYear = true;
                        } else if (qName.equalsIgnoreCase(ApplicationConfig.ARTICLE_TITLE)) {
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
                            int numericYear = Integer.parseInt(year);
                            inRange = numericYear >= numericStartYear && numericYear <= numericEndYear;
                            isYear = false;
                        } else if (isTitle) {
                            title = characters.getData();
                            isTitle = false;
                        }
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        EndElement endElement = xmlEvent.asEndElement();

                        // Only count the article if it's within the specified range of years
                        if (endElement.getName().getLocalPart().equalsIgnoreCase(ApplicationConfig.PUB_MED_ARTICLE) && inRange) {
                            if (title.toLowerCase().contains(keyword.toLowerCase())) {
                                ++hitCount;                                          // Track the number of hits
                                articles.add(new Article(id, month, year, title));   // Track articles in container
                            }
                        }
                        break;
                }
            }

            endTime = System.currentTimeMillis();
            runtime = endTime - startTime;
            logger.runtime(ApplicationConfig.BRUTEFORCE, runtime);
        } catch (FileNotFoundException | XMLStreamException e) {
            e.printStackTrace();
        }
        return hitCount;                                                       // Number of times keyword was found
    }

    /**
     * Prints search history to the console
     */
    public void print() {
        storage.print();                                                       // Delegate to the storage object
    }

    /**
     * Accessor method that return the total runtime of the query
     * @return The runtime of the query
     */
    public double getRunTime() {
        return runtime;
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
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(ApplicationConfig.DATE_FORMAT);

        storage.saveToMemory(searchParam, timestamp.format(dateTimeFormatter));
        try {
            storage.saveToDisk(searchParam, timestamp.format(dateTimeFormatter));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
