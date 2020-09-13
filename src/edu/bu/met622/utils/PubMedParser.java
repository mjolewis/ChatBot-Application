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
public class PubMedParser extends DefaultHandler {
    private String fileName;                                    // File to be searched
    private String searchParam;                                 // Search string
    private List<Article> articles = new ArrayList<>();         // A container of PubMed articles

    /**
     * Initialize a new XMLParser
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this new XMLParser
     */
    public PubMedParser() {

        fileName = Constants.DEFAULT_FILE_NAME;
        searchParam = Constants.DEFAULT_SEARCH_PARAM;
    }

    /**
     * Initialize a new XMLParser with the file to be parsed
     *
     * @param searchParam A value to be searched
     * @throws OutOfMemoryError Indicates insufficient memory for this new XMLParser
     */
    public PubMedParser(String fileName, String searchParam) {

        this.fileName = fileName;
        this.searchParam = searchParam;
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
                            //System.out.println("Year: " + xmlEvent.asCharacters().getData());
                            break;
                        case Constants.ARTICLE_TITLE:
                            xmlEvent = xmlEventReader.nextEvent();
                            title = xmlEvent.toString();
                            //System.out.println("Article: " + xmlEvent.toString());// asCharacters().getData().toString());
                            break;
                    }
                } else if (xmlEvent.isEndElement()) {
                    EndElement endElement = xmlEvent.asEndElement();

                    if (endElement.getName().getLocalPart().equals(Constants.PUB_MED_ARTICLE)) {
                        if (title.contains(searchParam)) {
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

    public String getSearchParam() {
        return searchParam;
    }

    public void setSearchParam(String searchParam) {
        this.searchParam = searchParam;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }
}