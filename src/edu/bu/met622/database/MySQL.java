package edu.bu.met622.database;

import edu.bu.met622.sharedresources.Constants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**********************************************************************************************************************
 * Uses Java Database Connectivity (JDBC) API to provide a connection to the underlying relational data store
 *
 * @author Michael Lewis
 * @version October 24, 2020 - Kickoff
 *********************************************************************************************************************/
public class MySQL {
    private static MySQL db;                                    // A MySQL database
    private static Connection connection;                       // A connection (session) with a specific database

    /**
     * Initializes a new MySQL object
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this new object
     */
    private MySQL() {}

    /**
     * Static factory method to create an MySQL instance while avoiding the unnecessary expense of creating duplicate
     * objects
     *
     * @return A MySQL database instance
     * @note The client is responsible for closing the connection. This can be accomplished by executing the close
     *         method
     */
    public static MySQL getInstance() {
        if (db == null) { db = new MySQL(); }
        return db;
    }

    /**
     * A connection (session) with a specific database
     *
     * @return A connection to the database
     * @note The client is responsible for closing the connection. This can be accomplished by executing the close
     *         method
     */
    public Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(Constants.SQL_PATH, Constants.SQL_USER, Constants.SQL_PWD);
            } catch (SQLException e) {                              // Database access error, url is null, or timeout error
                e.printStackTrace();
            }
        }
        return connection;
    }

    /**
     * Close the connection with the specified database
     */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
