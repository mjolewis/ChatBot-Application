package edu.bu.met622.searchengine;

import edu.bu.met622.model.Article;
import edu.bu.met622.sharedresources.Constants;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;


/**********************************************************************************************************************
 *
 * @author Michael Lewis
 * @version October 1, 2020 - Kickoff
 *********************************************************************************************************************/
public class LuceneIndex {
    IndexWriter indexWriter;

    /**
     * Initialize a new Lucene Index. After using this Lucene Index, you must manually clean up system resources by
     * calling the closeResources method
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this new Indexer
     */
    public LuceneIndex() {
        try (Directory indexDir = FSDirectory.open(Paths.get(Constants.INDEX_DIRECTORY))) {
            IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
            indexWriter = new IndexWriter(indexDir, config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add Document objects to the Lucene Index, which can be used for efficient searching
     */
    public void createIndex(Article article) {
        try {
            Document document = new Document();            // Create documents that will be stored in the index

            // Use TextField for a field that needs to be tokenized into a set of words
            document.add(new TextField(Constants.ARTICLE_TITLE, article.getArticleTitle(), Field.Store.YES));

            // Use StringField for a field with an atomic value that should not be tokenized
            document.add(new StringField(Constants.PUB_ID, article.getPubID(), Field.Store.YES));
            document.add(new StringField(Constants.PUBLICATION_YEAR, article.getPubYear(), Field.Store.YES));
            document.add(new StringField(Constants.PUBLICATION_MONTH, article.getPubMonth(), Field.Store.YES));
            document.add(new StringField(Constants.PUBLICATION_DAY, article.getPubDay(), Field.Store.YES));

            indexWriter.addDocument(document);             // Add the document to the index
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clean up system resources to prevent memory leaks
     */
    public void closeResources() {
        try {
            indexWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
