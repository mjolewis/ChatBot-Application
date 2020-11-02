package edu.bu.met622.model;

import java.util.Objects;

/**********************************************************************************************************************
 * Data model - POJO
 *
 * @author Michael Lewis
 *********************************************************************************************************************/
public class Article {
    private String id;
    private String month;
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
     * @param id    The publication ID
     * @param month The publication month
     * @param year  The publication year
     * @param title The title of the Article
     * @throws OutOfMemoryError Indicates insufficient memory for this Article
     */
    public Article(String id, String month, String year, String title) {
        this.id = id;
        this.month = month;
        this.year = year;
        this.title = title;
    }

    /**
     * Accessor method that returns the ID of this article
     *
     * @return The ID of this article
     */
    public String getId() {
        return id;
    }

    /**
     * Mutator method that sets the ID of this Article
     *
     * @param id The ID of this Article
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Accessor method that returns the month of this article
     *
     * @return The month of this article
     */
    public String getMonth() {
        return month;
    }

    /**
     * Mutator method that sets the month of this Article
     *
     * @param month The month of this Article
     */
    public void setMonth(String month) {
        this.month = month;
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
                "pubID='" + id + '\'' +
                ", month='" + month + '\'' +
                ", pubYear='" + year + '\'' +
                ", articleTitle='" + title + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Article)) return false;
        Article article = (Article) o;
        return Objects.equals(getId(), article.getId()) &&
                Objects.equals(getMonth(), article.getMonth()) &&
                Objects.equals(getYear(), article.getYear()) &&
                Objects.equals(getTitle(), article.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getMonth(), getYear(), getTitle());
    }
}
