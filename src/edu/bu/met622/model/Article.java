package edu.bu.met622.model;

/**********************************************************************************************************************
 * Data model for PubMed articles
 *
 * @author Michael Lewis
 *********************************************************************************************************************/
public class Article {
    private String pubID;
    private String pubYear;
    private String articleTitle;

    /**
     * Initialize a new Article
     *
     * @throws OutOfMemoryError Indicates insufficient memory for this Article
     */
    public Article() {
    }

    /**
     * Initialize a new Article
     *
     * @param pubID        The publication ID
     * @param pubYear      The publication year
     * @param articleTitle The title of the Article
     * @throws OutOfMemoryError Indicates insufficient memory for this Article
     */
    public Article(String pubID, String pubYear, String articleTitle) {
        this.pubID = pubID;
        this.pubYear = pubYear;
        this.articleTitle = articleTitle;
    }

    /**
     * Accessor method that returns the year this Article was published
     *
     * @return The year this article was published
     */
    public String getPubYear() {
        return pubYear;
    }

    /**
     * Mutator method that sets the year this Article was published
     *
     * @param pubYear The year this Article was published
     */
    public void setPubYear(String pubYear) {
        this.pubYear = pubYear;
    }

    /**
     * Accessor method that returns the ID of this article
     *
     * @return The ID of this article
     */
    public String getPubID() {
        return pubID;
    }

    /**
     * Mutator method that sets the ID of this Article
     *
     * @param pubID The ID of this Article
     */
    public void setPubID(String pubID) {
        this.pubID = pubID;
    }

    /**
     * Accessor method that returns the title of the Article
     *
     * @return The title of the Article
     */
    public String getArticleTitle() {
        return articleTitle;
    }

    /**
     * Mutator method that sets the title of this Article
     *
     * @param articleTitle The title of the Article
     */
    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    /**
     * User friendly representation of an Article object
     *
     * @return A string that contains the core information about an Article
     */
    @Override
    public String toString() {
        return "Article{" +
                "pubID='" + pubID + '\'' +
                ", pubYear='" + pubYear + '\'' +
                ", articleTitle='" + articleTitle + '\'' +
                '}';
    }
}
