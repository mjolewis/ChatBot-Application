package edu.bu.met622.database;

import edu.bu.met622.model.Article;

import java.util.List;

/**********************************************************************************************************************
 * A document database provided by MongoDB
 *
 * @author Michael Lewis
 * @version October 28, 2020 - Kickoff
 *********************************************************************************************************************/
public class MongoDB {
    private static MongoDB db;                                  // A connection to the database
    private static boolean exists = false;                      // True if the table has been built; Otherwise false
    double startTime;                                           // Tracks the runtime of the query
    double endTime;                                             // Tracks the runtime of the query
    double runtime;                                              // The total runtime of the query

    /**
     * Initializes a new MongoDB object
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this new object
     */
    private MongoDB() { }

    /**
     * Static factory method to create an MongoDB instance while avoiding the unnecessary expense of creating duplicate
     * objects
     *
     * @return A MongoDB database instance
     * @note The client is responsible for closing the connection. This can be accomplished by executing the close
     *         method
     */
    public static MongoDB getInstance() {
        if (db == null) {
            db = new MongoDB();
        }
        return db;
    }

    /**
     * Builds the MongoDB database by performing the following operations. Connect to the database. Create the articles
     * collection if it doesn't exist. Populates the articles collection with the ID, Date, and Title of each article
     * that was parsed from the XML document
     *
     * @note The article is only inserted if its ID is not already contained in the collection
     */
    public void buildDB(List<Article> articlesList) {

    }

    /**
     * Accessor method that return the total runtime of the query
     * @return The runtime of the query
     */
    public double getRunTime() {
        return runtime;
    }

    /**
     * Accessor method to determine whether or not the database has been built
     *
     * @return True if the database and table have been created; Otherwise false
     */
    public static boolean exists() {
        return exists;
    }


}
