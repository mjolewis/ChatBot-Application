package edu.bu.met622.searchlib;

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
import java.util.List;


/**********************************************************************************************************************
 *
 * @author Michael Lewis
 * @version October 1, 2020 - Kickoff
 *********************************************************************************************************************/
public class Indexer {
    IndexWriter indexWriter = null;

    /**
     * Initialize a new Lucene Index. After using this Lucene Index, you must manually clean up system resources by
     * calling the closeResources method
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this new Indexer
     */
    public Indexer() {
        try (Directory indexDir = FSDirectory.open(Paths.get(Constants.INDEX_DIRECTORY))) {
            IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
            indexWriter = new IndexWriter(indexDir, config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds every article into a Document object, which is then added into a Lucene Index
     *
     * @note A Lucene Index provides rich full-text and efficient searching capabilities
     */
    public void createIndex(List<Article> articles) {

        for (Article article : articles) {
            try {
                Document doc = new Document();             // Create documents that will be stored in the index

                // Use TextField for a field that needs to be tokenized into a set of words
                doc.add(new TextField(Constants.ARTICLE_TITLE, article.getArticleTitle(), Field.Store.YES));

                // Use StringField for a field with an atomic value that should not be tokenized
                doc.add(new StringField(Constants.PUB_ID, article.getPubID(), Field.Store.YES));
                doc.add(new StringField(Constants.PUBLICATION_YEAR, article.getPubYear(), Field.Store.YES));
                doc.add(new StringField(Constants.PUBLICATION_MONTH, article.getPubMonth(), Field.Store.YES));
                doc.add(new StringField(Constants.PUBLICATION_DAY, article.getPubDay(), Field.Store.YES));

                indexWriter.addDocument(doc);              // Add the document to the index
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Opening and closing the index is expensive, so only do it after the entire batch updates
        closeIndexWriter();
    }

    /**
     * Clean up system resources to prevent memory leaks
     */
    public void closeIndexWriter() {
        if (indexWriter != null) {
            try {
                indexWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
