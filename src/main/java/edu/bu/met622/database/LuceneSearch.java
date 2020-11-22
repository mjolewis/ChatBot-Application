package edu.bu.met622.database;

import edu.bu.met622.resources.Config;
import edu.bu.met622.utils.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Document;

import java.io.IOException;
import java.nio.file.Paths;

/**********************************************************************************************************************
 * Support user queries with full-text and efficient search capabilities
 *
 * @author Michael Lewis
 *********************************************************************************************************************/
public class LuceneSearch {
    private IndexSearcher searcher = null;
    private QueryParser parser;
    private ScoreDoc[] hits = null;
    private static Logger logger;                               // Logs application events to files
    private double startTime;                                   // Tracks the runtime of the query
    private double endTime;                                     // Tracks the runtime of the query
    private double runtime;                                     // The total runtime of the query


    /**
     * Initialize a new SearchEngine to query the Lucene Index for a specified search parameter
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this new SearchEngine
     */
    public LuceneSearch() {

        parser = new QueryParser(Config.ARTICLE_TITLE, new StandardAnalyzer());
        logger = Logger.getInstance();                               // Log application events to a file
    }

    /**
     * Searches the Lucene Index for the specified search parameter and returns the top N documents
     *
     * @param searchParam A user specified search string
     * @param numOfDocs   The maximum number of documents to return when the search parameter is found
     * @return The runtime of the current search
     */
    public double search(String searchParam, int numOfDocs) {

        try {
            startTime = System.currentTimeMillis();

            Query query = parser.parse(searchParam);
            searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(Config.INDEX_DIRECTORY))));
            TopDocs docs = searcher.search(query, numOfDocs);

            endTime = System.currentTimeMillis();

            hits = docs.scoreDocs;
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        runtime = endTime - startTime;
        logger.runtime(Config.LUCENE_INDEX, runtime);

        return hits.length;                                     // The number of times the keyword was found
    }

    /**
     * Displays case-insensitive search results
     */
    public void displayHits() {
        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;

            try {
                Document doc = searcher.doc(docId);
                System.out.print(i + 1 + ") " + "Pub ID: " + doc.get(Config.PMID) + "\n" +
                        "\tTitle: " + doc.get(Config.ARTICLE_TITLE) + "\n" +
                        "\tMonth: " + doc.get(Config.MONTH) + "\n" +
                        "\tYear: " + doc.get(Config.YEAR) + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Accessor method that return the total runtime of the query
     *
     * @return The runtime of the query
     */
    public double getRunTime() {
        return runtime;
    }
}
