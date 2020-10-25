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
//    public Connection getConnection() {
//        if (connection == null) {
//            try {
//                connection = DriverManager.getConnection(Constants.SQL_PATH, Constants.SQL_USER, Constants.SQL_PWD);
//            } catch (SQLException e) {                              // Database access error, url is null, or timeout error
//                e.printStackTrace();
//            }
//        }
//        return connection;
//    }

    public void buildDB(List<Article> articlesList) {
        int countInserted = 0;
        PreparedStatement pStmt = null;

        try {
            // A connection (session) with a specific database
            con = DriverManager.getConnection(Config.SQL_DB, Config.SQL_USER, Config.SQL_PWD);

            // A statement object holds SQL commands
            Statement stmt = con.createStatement();

            // Metadata about the db we're connected to
            DatabaseMetaData metaData = con.getMetaData();

            // A description of the table represented by the catalog, schema, table name, and types
            ResultSet table = metaData.getTables(null, null, Config.TABLE_NAME, null);
            if (!table.next()) {
                System.out.println(Config.CREATING_TABLE);
                stmt.execute(Config.CREATE_TABLE);                    // Create table if it doesn't exist
                System.out.println(Config.CREATED_TABLE);

            } else {
                for (Article article : articlesList) {
                    String sqlInsert = "INSERT IGNORE INTO articles values(?, ?, ?)";

                    // Prepared Statements prevent SQL injection and efficiently execute the statement multiple times
                    pStmt = con.prepareStatement(sqlInsert);
                    pStmt.setString(1, article.getPubID());
                    pStmt.setString(2, article.getPubYear());
                    pStmt.setString(3, article.getArticleTitle());
                    countInserted += pStmt.executeUpdate();
                }
                if (pStmt != null) {pStmt.close(); }
                System.out.println(countInserted + " records inserted.\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Close the connection with the specified database
     */
    public void close() {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
