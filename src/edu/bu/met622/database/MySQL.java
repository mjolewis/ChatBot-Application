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
    private static Statement stmt;
    private static PreparedStatement pStmt;
    private static boolean exists = false;                      // True if the table has been built; Otherwise false

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
                String sqlInsert = "INSERT IGNORE INTO articles values(?, ?, ?)";

                // Prepared Statements prevent SQL injection and efficiently execute the statement multiple times
                pStmt = con.prepareStatement(sqlInsert);
                pStmt.setString(1, article.getPubID());
                pStmt.setString(2, article.getPubYear());
                pStmt.setString(3, article.getArticleTitle());
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
        String sqlQuery = "SELECT * FROM articles WHERE title LIKE ? AND pubDate LIKE ?";
        try {
            // A Statement object holds SQL commands
            stmt = con.createStatement();

            // Prepared Statements prevent SQL injection and efficiently execute the statement multiple times
            pStmt = con.prepareStatement(sqlQuery);
            pStmt.setString(1, "%" + keyword + "%");
            pStmt.setString(2, "%" + year + "%");

            ResultSet rs = pStmt.executeQuery();
            while (rs != null && rs.next()) { ++hits; }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hits;
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
