package main.java.edu.bu.met622.database;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import main.java.edu.bu.met622.model.Article;
import main.java.edu.bu.met622.resources.Config;

import java.util.ArrayList;
import java.util.List;

/**********************************************************************************************************************
 * A document database provided by MongoDB
 *
 * @author Michael Lewis
 * @version October 28, 2020 - Kickoff
 *********************************************************************************************************************/
public class MongoDB {
    private static MongoDB mongoDB;                             // A connection to the database
    private static MongoClient client;                          // Connection to a local instance
    private MongoDatabase db;                                   // The database object
    private MongoCollection<Document> collection;               // A collection within the database
    private Document doc;                                       // A document (row) that is inserted into the collection
    private static boolean exists = false;                      // True if the table has been built; Otherwise false
    private double startTime;                                   // Tracks the runtime of the query
    private double endTime;                                     // Tracks the runtime of the query
    private double runtime;                                     // The total runtime of the query

    /**
     * Initializes a new MongoDB object
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this new object
     */
    private MongoDB() {
        client = new MongoClient();                                  // Connect to a local instance on the default port

        System.out.println(Config.CONNECTING);                       // Notification about connecting to the db

        // If the database doesn't exist, it will be created automatically so there is no need for null checks
        db = client.getDatabase(Config.MONGO_DB);

        System.out.println(Config.CONNECTED);                        // Notification about being connecting to the db

        // If the collection does not exist, MongoDB creates it implicitly
        collection = db.getCollection(Config.COLLECTION);
    }

    /**
     * Static factory method to create an MongoDB instance while avoiding the unnecessary expense of creating duplicate
     * objects
     *
     * @return A MongoDB database instance
     * @note The client is responsible for closing the connection. This can be accomplished by executing the close
     *         method
     */
    public static MongoDB getInstance() {
        if (mongoDB == null) {
            mongoDB = new MongoDB();
        }
        return mongoDB;
    }

    /**
     * Builds the MongoDB database by performing the following operations. Connect to the database. Create the articles
     * collection if it doesn't exist. Populates the articles collection with the ID, Date, and Title of each article
     * that was parsed from the XML document
     *
     * @note The article is only inserted if its ID is not already contained in the collection
     */
    public void buildDB(List<Article> articlesList) {

        System.out.println(Config.POPULATING_DB);                    // Notification about populating the db

        // Create a unique index based on the article id
        collection.createIndex(Indexes.text(Config.DOCUMENT_ID), new IndexOptions().unique(true).sparse(true));
        for (Article article : articlesList) {

            doc = new Document();

            doc.put(Config.DOCUMENT_ID, article.getId());
            doc.put(Config.DOCUMENT_MONTH, article.getMonth());
            doc.put(Config.DOCUMENT_YEAR, article.getYear());
            doc.put(Config.DOCUMENT_DATE, article.getYear() + "-" + article.getMonth() + "-" + "1");
            doc.put(Config.DOCUMENT_TITLE, article.getTitle());

            try {
                collection.insertOne(doc);                               // Insert the document into the collection
            } catch (Exception e) {
                System.out.println(Config.DUPLICATE_ENTRY);
            }
        }

        exists = true;                                               // Prevents the XML document from being re-parsed
    }

    /**
     * Accessor method to determine whether or not the database has been built
     *
     * @return True if the database and table have been created; Otherwise false
     */
    public boolean exists() {
        if (collection.countDocuments() > 0) {
            exists = true;
        }
        return exists;
    }

    /**
     * A query to count the number of times the given keyword appears in the specified year. For example, how many
     * times does "flu" appear in knowledge base within the year 2020
     *
     * @param keyword A value to be searched for
     * @param year    The year to search within
     * @return The number of times the keyword was found within the specified year
     */
    public int query(String keyword, String year) {
        int hits = 0;

        startTime = System.currentTimeMillis();                           // Start the runtime clock

        BasicDBObject query = new BasicDBObject();
        query.put("title", java.util.regex.Pattern.compile(keyword));
        query.put("year", year);
        FindIterable<Document> results = collection.find(query);

        for (Document result : results) {
            ++hits;
        }

        endTime = System.currentTimeMillis();                             // Stop the runtime clock
        runtime = endTime - startTime;                                    // The total runtime of the query

        return hits;
    }

    /**
     * A query to count the number of times the given keyword appears in the specified date range. For example, “flu”,
     * “obesity" in the range June 2018 - December 2020
     *
     * @param keyword    A value to be searched
     * @param startMonth The first month within the search range
     * @param startYear  The first year within the search range
     * @param endMonth   The last month within the search range
     * @param endYear    The last year within the search range
     * @return The number of times the keyword was found in the specified year
     */
    public int query(String keyword, String startMonth, String startYear, String endMonth, String endYear) {
        int hits = 0;
        String startDate = startYear + "-" + startMonth + "-" + "1";
        String endDate = endYear + "-" + endMonth + "-" + "1";

        startTime = System.currentTimeMillis();                           // Start the runtime clock

        List<DBObject> criteria = new ArrayList<>();
        criteria.add(new BasicDBObject("title", java.util.regex.Pattern.compile(keyword)));
        criteria.add(new BasicDBObject("date", new BasicDBObject("$gte", startDate)));
        criteria.add(new BasicDBObject("date", new BasicDBObject("$lte", endDate)));
        FindIterable<Document> results = collection.find(new BasicDBObject("$and", criteria));

        for (Document result : results) {
            ++hits;
        }

        endTime = System.currentTimeMillis();                             // Stop the runtime clock
        runtime = endTime - startTime;                                    // The total runtime of the query

        return hits;
    }

    /**
     * Accessor method that return the total runtime of the query
     *
     * @return The runtime of the query
     */
    public double getRunTime() {
        return runtime;
    }

    /**
     * Close the MongoDB client
     */
    public static void closeConnection() {
        if (client != null) {
            client.close();
        }
    }
}
