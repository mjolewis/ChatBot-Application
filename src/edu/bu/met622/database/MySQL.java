package edu.bu.met622.database;

import edu.bu.met622.model.Article;
import edu.bu.met622.resources.Config;

import java.sql.*;
import java.util.List;

/**********************************************************************************************************************
 * Uses Java Database Connectivity (JDBC) API to provide a connection to the underlying relational data store
 *
 * @author Michael Lewis
 * @version October 24, 2020 - Kickoff
 *********************************************************************************************************************/
public class MySQL {
    private static MySQL db;                                    // A MySQL database
    private static Connection con;                              // A connection (session) with a specific database
    private static Statement stmt;                              // Holds a SQL command
    private static PreparedStatement pStmt;                     // A wrapper for a Statement object to prevent injection
    private static boolean exists = false;                      // True if the table has been built; Otherwise false
    double startTime;                                           // Tracks the runtime of the query
    double endTime;                                             // Tracks the runtime of the query
    double runtime;                                              // The total runtime of the query

    /**
     * Initializes a new MySQL object
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this new object
     */
    private MySQL() {
    }

    /**
     * Static factory method to create an MySQL instance while avoiding the unnecessary expense of creating duplicate
     * objects
     *
     * @return A MySQL database instance
     * @note The client is responsible for closing the connection. This can be accomplished by executing the close
     *         method
     */
    public static MySQL getInstance() {
        if (db == null) {
            db = new MySQL();
        }
        return db;
    }

    /**
     * Builds the MySQL database by performing the following operations. Connect to the database. Create the articles
     * table if it doesn't exist. Populates the articles table with the ID, Date, and Title of each article that was
     * parsed from the XML document
     *
     * @note The article is only inserted if its ID is not already contained in the table
     */
    public void buildDB(List<Article> articlesList) {
        int countInserted = 0;

        try {
            // A connection (session) with a specific database
            System.out.println(Config.CONNECTING);
            con = DriverManager.getConnection(Config.SQL_DB, Config.SQL_USER, Config.SQL_PWD);
            System.out.println(Config.CONNECTED);

            // A statement object holds SQL commands
            stmt = con.createStatement();

            // Metadata about the db we're connected to
            DatabaseMetaData metaData = con.getMetaData();

            // A description of the table represented by the catalog, schema, table name, and types
            ResultSet table = metaData.getTables(null, null, Config.TABLE_NAME, null);
            if (!table.next()) {
                System.out.println(Config.CREATING_TABLE);
                stmt.execute(Config.CREATE_TABLE);                    // Create table if it doesn't exist
                System.out.println(Config.CREATED_TABLE);
            }

            // Insert data into the database only if the ID doesn't already exist
            for (Article article : articlesList) {
                String sqlInsert = "INSERT IGNORE INTO articles values(?, ?, ?, ?)";

                // Prepared Statements prevent SQL injection and efficiently execute the statement multiple times
                pStmt = con.prepareStatement(sqlInsert);
                pStmt.setString(1, article.getId());
                pStmt.setString(2, article.getMonth());
                pStmt.setString(3, article.getYear());
                pStmt.setString(4, article.getTitle());
                countInserted += pStmt.executeUpdate();
            }

            System.out.println(countInserted + Config.RECORDS_INSERTED);
            exists = true;                                      // Prevents the XML document from being re-parsed
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Accessor method to determine whether or not the database has been built
     *
     * @return True if the database and table have been created; Otherwise false
     */
    public static boolean exists() {
        return exists;
    }

    /**
     * A query to count the number of times the given keywords per appears in the specified year. For example, “flu”,
     * “obesity"
     *
     * @param keyword A value to be searched
     * @param year    The year to search in
     * @return The number of times the keyword was found in the specified year
     */
    public int query(String keyword, String year) {
        int hits = 0;

        String sqlQuery = "SELECT * FROM articles WHERE title LIKE ? AND year LIKE ?";
        try {
            startTime = System.currentTimeMillis();                       // Start the runtime clock

            stmt = con.createStatement();                                 // A Statement object holds SQL commands

            // Prepared Statements prevent SQL injection and efficiently execute the statement multiple times
            pStmt = con.prepareStatement(sqlQuery);
            pStmt.setString(1, "%" + keyword + "%");
            pStmt.setString(2, "%" + year + "%");

            ResultSet rs = pStmt.executeQuery();                          // Execute the query
            endTime = System.currentTimeMillis();                         // Stop the runtime clock
            runtime = endTime - startTime;                                // The total runtime of the query

            // Determine the number of times the keyword is in the database
            while (rs != null && rs.next()) {
                ++hits;
            }

        } catch (SQLException e) {
            // TODO: 10/31/20 write to log file
            e.printStackTrace();
        }
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
        String sqlQuery = "SELECT * FROM articles " +
                "WHERE title LIKE ? " +
                "AND month LIKE ? " +
                "AND year LIKE ? " +
                "AND month LIKE ? " +
                "AND year LIKE ?";

        try {
            startTime = System.currentTimeMillis();                       // Start the run time clock

            stmt = con.createStatement();                                 // A Statement object holds SQL commands

            // Prepared Statements prevent SQL injection and efficiently execute the statement multiple times
            pStmt = con.prepareStatement(sqlQuery);
            pStmt.setString(1, "%" + keyword + "%");
            pStmt.setString(2, "%" + startMonth + "%");
            pStmt.setString(3, "%" + startYear + "%");
            pStmt.setString(4, "%" + endMonth + "%");
            pStmt.setString(5, "%" + endYear + "%");

            ResultSet rs = pStmt.executeQuery();                          // Execute the query
            endTime = System.currentTimeMillis();                         // Stop the runtime clock
            runtime = endTime - startTime;                                // The total runtime of the query

            // Determine the number of times the keyword is in the database
            while (rs != null && rs.next()) {
                ++hits;
            }

        } catch (SQLException e) {
            // TODO: 10/31/20 write to log file
            e.printStackTrace();
        }

        return hits;
    }

    /**
     * Accessor method that return the total runtime of the query
     * @return The runtime of the query
     */
    public double getRunTime() {
        return runtime;
    }

    /**
     * Close the connection with the specified database
     */
    public static void closeConnection() {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Close the SQL Statement object
     */
    public static void closeStatement() {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Close the PreparedStatement object
     */
    public static void closePreparedStatement() {
        if (pStmt != null) {
            try {
                pStmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
