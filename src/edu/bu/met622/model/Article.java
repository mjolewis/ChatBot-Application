package edu.bu.met622.model;

/**********************************************************************************************************************
 * Data model for PubMed articles
 *
 * @author Michael Lewis
 *********************************************************************************************************************/
public class Article {
    private String year;
    private String title;

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
     * @param year  The publication year
     * @param title The title of the Article
     * @throws OutOfMemoryError Indicates insufficient memory for this Article
     */
    public Article(String year, String title) {
        this.year = year;
        this.title = title;
    }

    /**
     * Accessor method that returns the year this Article was published
     *
     * @return The year this article was published
     */
    public String getYear() {
        return year;
    }

    /**
     * Mutator method that sets the year this Article was published
     *
     * @param year The year this Article was published
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Accessor method that returns the title of the Article
     *
     * @return The title of the Article
     */
    public String getTitle() {
        return title;
    }

    /**
     * Mutator method that sets the title of this Article
     *
     * @param title The title of the Article
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * User friendly representation of an Article object
     *
     * @return A string that contains the core information about an Article
     */
    @Override
    public String toString() {
        return "Article{" +
                "year='" + year + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
