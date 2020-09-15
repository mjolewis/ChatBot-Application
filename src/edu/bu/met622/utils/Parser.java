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
import java.util.ArrayList;
import java.util.List;

/**********************************************************************************************************************
 * Parse XML trees using pull parsing, which allows the client to control the application thread.
 *
 * @author Michael Lewis
 *********************************************************************************************************************/
public class Parser extends DefaultHandler {
    private String fileName;                                    // File to be searched
    private List<Article> articles = new ArrayList<>();         // A container of PubMed articles

    /**
     * Initialize a new XMLParser
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this new XMLParser
     */
    public Parser() {

        fileName = Constants.OUTPUT_FILE;
    }

    /**
     * Initialize a new XMLParser with the file to be parsed
     *
     * @param fileName Name and extension of the file to parse
     * @throws OutOfMemoryError Indicates insufficient memory for this new XMLParser
     */
    public Parser(String fileName) {

        this.fileName = fileName;
    }

    /**
     * Event-based processing of an XML document assigned to this objects fileName member variable
     *
     * @param searchParam A value to be searched
     * @return A List of Articles whose titles contain the specified search parameter
     */
    public List<Article> parse(String searchParam) {
        String year = "";
        String title = "";
        List<Article> articles = new ArrayList<>();

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

        return articles;
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
}