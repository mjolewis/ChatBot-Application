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
 *
 * @author Michael Lewis
 *********************************************************************************************************************/
public class SearchEngine {
    IndexSearcher searcher = null;
    QueryParser parser = null;
    TopDocs docs = null;
    ScoreDoc[] hits = null;


    /**
     * Initialize a new SearchEngine to query the Lucene Index for a specified search parameter
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this new SearchEngine
     */
    public SearchEngine() {
        try {
            searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(Paths.get(Constants.INDEX_DIRECTORY))));
            parser = new QueryParser(Constants.ARTICLE_TITLE, new StandardAnalyzer());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Searches the Lucene Index for the specified search parameter and returns the top N documents
     *
     * @param searchParam A user specified search string
     * @param numOfDocs   The maximum number of documents to return when the search parameter is found
     */
    public void search(String searchParam, int numOfDocs) {
        try {
            Query query = parser.parse(searchParam);
            docs = searcher.search(query, numOfDocs);
            hits = docs.scoreDocs;
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    public void displayHits() {
        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;

            try {
                Document doc = searcher.doc(docId);
                System.out.print(i + 1 + ") " + "Pub ID: " + doc.get(Constants.PUB_ID) + "\n" +
                        "\tTitle: " + doc.get(Constants.ARTICLE_TITLE) + "\n" +
                        "\tPub Year: " + doc.get(Constants.PUBLICATION_YEAR) + "\n" +
                        "\tPub Month: " + doc.get(Constants.PUBLICATION_MONTH) + "\n" +
                        "\tPub Day: " + doc.get(Constants.PUBLICATION_DAY) + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ScoreDoc[] getHits() {
        return hits;
    }
}
