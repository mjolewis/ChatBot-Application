package edu.bu.met622.daos;

import edu.bu.met622.resources.ApplicationConfig;
import edu.bu.met622.output.Log;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Document;

import java.io.IOException;
import java.nio.file.Paths;

/**********************************************************************************************************************
 * Support user queries with full-text and efficient search capabilities
 *
 * @author Michael Lewis
 * @version September 18, 2020 Kick-Off
 * @version November 26, 2020 Range query support
 *********************************************************************************************************************/
public class LuceneSearch {
    private IndexSearcher searcher = null;
    private QueryParser parser;
    private ScoreDoc[] hits = null;
    private static Log log;                               // Logs application events to files
    private double startTime;                                   // Tracks the runtime of the query
    private double endTime;                                     // Tracks the runtime of the query
    private double runtime;                                     // The total runtime of the query


    /**
     * Initialize a new SearchEngine to query the Lucene Index for a specified search parameter
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this new SearchEngine
     */
    public LuceneSearch() {

        parser = new QueryParser(ApplicationConfig.ARTICLE_TITLE, new StandardAnalyzer());
        log = Log.getInstance();                               // Log application events to a file
    }

    /**
     * Searches the Lucene Index for a keyword
     *
     * @param keyword   A user specified search string
     * @param numOfDocs The maximum number of documents to return when the search parameter is found
     * @return The number of times the keyword was found in the index
     */
    public double search(String keyword, int numOfDocs) {

        try {
            startTime = System.currentTimeMillis();

            Query query = parser.parse(keyword);
            searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(ApplicationConfig.INDEX_DIRECTORY))));
            TopDocs docs = searcher.search(query, numOfDocs);

            endTime = System.currentTimeMillis();

            hits = docs.scoreDocs;
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        runtime = endTime - startTime;
        log.runtime(ApplicationConfig.LUCENE_INDEX, keyword, runtime);

        return hits.length;                                     // The number of times the keyword was found
    }

    /**
     * Searches the Lucene Index for a keyword within [startYear, endYear]
     *
     * @param keyword   A value to be searched
     * @param startYear The first year within the search range
     * @param endYear   The last year within the search range
     * @param numOfDocs The maximum number of documents to return when the search parameter is found
     * @return The number of times the keyword was found in the specified range
     */
    public double search(String keyword, String startYear, String endYear, int numOfDocs) {

        try {
            startTime = System.currentTimeMillis();

            Query keywordQuery = parser.parse(keyword);
            TermRangeQuery termRangeQuery = TermRangeQuery.newStringRange(ApplicationConfig.YEAR,
                    startYear,
                    endYear,
                    true,
                    true);

            BooleanQuery.Builder bq = new BooleanQuery.Builder();              // Matches boolean combinations of multiple queries
            bq.add(keywordQuery, BooleanClause.Occur.MUST);                    // Add the keyword query to the clause
            bq.add(termRangeQuery, BooleanClause.Occur.MUST);                  // Add the range query to the clause

            searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(ApplicationConfig.INDEX_DIRECTORY))));

            TopDocs docs = searcher.search(bq.build(), numOfDocs);             // Create a new BooleanQuery based on the parameters that have been set

            endTime = System.currentTimeMillis();

            hits = docs.scoreDocs;

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        runtime = endTime - startTime;
        log.runtime(ApplicationConfig.LUCENE_INDEX, keyword, runtime);

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
                System.out.print(i + 1 + ") " + "Pub ID: " + doc.get(ApplicationConfig.PMID) + "\n" +
                        "\tTitle: " + doc.get(ApplicationConfig.ARTICLE_TITLE) + "\n" +
                        "\tMonth: " + doc.get(ApplicationConfig.MONTH) + "\n" +
                        "\tYear: " + doc.get(ApplicationConfig.YEAR) + "\n");
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
