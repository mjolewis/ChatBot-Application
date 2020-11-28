package edu.bu.met622.utils;

import edu.bu.met622.daos.MongoDB;
import edu.bu.met622.daos.MySQL;
import edu.bu.met622.daos.LuceneIndex;
import edu.bu.met622.resources.ApplicationConfig;
import edu.bu.met622.entities.Article;
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
import java.util.*;

/**********************************************************************************************************************
 * Parse XML trees using pull parsing, which allows the client to control the application thread.
 *
 * @author Michael Lewis
 *********************************************************************************************************************/
public class XMLParser extends DefaultHandler {
    private String fileName;                                    // File to be searched
    private List<Article> articles;                             // A collection of every article in the input file

    /**
     * Initialize a new Parser
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this new XMLParser
     */
    public XMLParser() {

        this.fileName = ApplicationConfig.MERGED_XML_FILE;
        this.articles = new ArrayList<>();
    }

    /**
     * Initialize a new Parser with the file to be parsed
     *
     * @param fileName Name and extension of the file to parse
     * @throws OutOfMemoryError Indicates insufficient memory for this new XMLParser
     */
    public XMLParser(String fileName) {

        this.fileName = fileName;
        this.articles = new ArrayList<>();
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
    public void createLuceneIndex() {
        LuceneIndex luceneIndex = new LuceneIndex();
        luceneIndex.createIndex(articles);
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
}