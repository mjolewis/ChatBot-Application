package edu.bu.met622.searchlib;

import edu.bu.met622.sharedresources.Constants;
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
public class SearchEngine {
    private IndexSearcher searcher = null;
    private QueryParser parser;
    private ScoreDoc[] hits = null;


    /**
     * Initialize a new SearchEngine to query the Lucene Index for a specified search parameter
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this new SearchEngine
     */
    public SearchEngine() {
        parser = new QueryParser(Constants.ARTICLE_TITLE, new StandardAnalyzer());
    }

    /**
     * Searches the Lucene Index for the specified search parameter and returns the top N documents
     *
     * @param searchParam A user specified search string
     * @param numOfDocs   The maximum number of documents to return when the search parameter is found
     * @return The runtime of the current search
     */
    public long search(String searchParam, int numOfDocs) {
        long startTime = 0;
        long endTime = 0;

        try {
            startTime = System.currentTimeMillis();

            Query query = parser.parse(searchParam);
            searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(Constants.INDEX_DIRECTORY))));
            TopDocs docs = searcher.search(query, numOfDocs);

            endTime = System.currentTimeMillis();

            hits = docs.scoreDocs;
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        return endTime - startTime;                                       // Run time of this search
    }

    /**
     * Displays case-insensitive search results
     */
    public void displayHits() {
        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;

            try {
                Document doc = searcher.doc(docId);
                System.out.print(i + 1 + ") " + "Pub ID: " + doc.get(Constants.PMID) + "\n" +
                        "\tTitle: " + doc.get(Constants.ARTICLE_TITLE) + "\n" +
                        "\tPub Year: " + doc.get(Constants.PUBLICATION_DATE) + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Accessor method that returns an array of search results
     *
     * @return An array of search results
     * @note These search results cannot be used directly. Instead, the user must unwrap the search results into a
     *         Document object
     */
    public ScoreDoc[] getHits() {
        return hits;
    }
}
