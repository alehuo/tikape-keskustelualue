package tikape.runko.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;

public class SubCategory {

    private int subCategoryId;
    private int categoryId;
    private String name;
    private String description;
    private String latestMessageTimestamp;
    private int latestMessageThreadId;
    private String latestMessageThreadTitle;
    private String latestMessageUsername;
    private boolean hasMessages = false;

    /**
     * Alakategoria -luokka
     *
     * @param categoryId Yläkategorian ID
     * @param subCategoryId Alakategorian ID
     * @param name Alakategorian nimi
     */
    public SubCategory(int categoryId, int subCategoryId, String name) {
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        this.name = name;
    }

    public SubCategory(int categoryId, String name) {
        this(categoryId, -1, name);
    }

    /**
     * Palauttaa alakategorian nimen
     *
     * @return Alakategorian nimi
     */
    public String getName() {
        return name;
    }

    /**
     * Palauttaa yläkategorian ID:n
     *
     * @return Yläkategorian ID
     */
    public int getCategoryId() {
        return categoryId;
    }

    /**
     * Palauttaa alakategorian ID:n
     *
     * @return Alakategorian ID
     */
    public int getSubCategoryId() {
        return subCategoryId;
    }

    /**
     * Palauttaa alakategorian kuvauksen
     *
     * @return Alakategorian kuvaus
     */
    public String getDescription() {
        return description;
    }

    /**
     * Asettaa alakategorian kuvauksen
     *
     * @param description Alakategorian kuvaus
     * @return SubCategory -ilmentymä
     */
    public SubCategory setDescription(String description) {
        this.description = description;
        return this;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSubCategoryId(int subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    /**
     * Palauttaa alakategorian ID:n, nimen ja kuvauksen
     *
     * @return Alakategorian kuvaus muodossa ID: nimi (kuvaus)
     */
    @Override
    public String toString() {
        return subCategoryId + ": " + name + " (" + description + ")";
    }

    public String getLatestMessageTimestamp() {
        return latestMessageTimestamp;
    }

    public void setLatestMessageTimestamp(String latestMessageTimestamp) {
        this.latestMessageTimestamp = latestMessageTimestamp.substring(0, 16);
    }

    public int getLatestMessageThreadId() {
        return latestMessageThreadId;
    }

    public void setLatestMessageThreadId(int latestMessageThreadId) {
        this.latestMessageThreadId = latestMessageThreadId;
    }

    public String getLatestMessageUsername() {
        return latestMessageUsername;
    }

    public void setLatestMessageUsername(String latestMessageUsername) {
        this.latestMessageUsername = latestMessageUsername;
    }

    public void setLatestMessageThreadTitle(String latestMessageThreadTitle) {
        this.latestMessageThreadTitle = latestMessageThreadTitle;
    }

    public String getLatestMessageThreadTitle() {
        return latestMessageThreadTitle;
    }

    public void setHasMessages(boolean hasMessages) {
        this.hasMessages = hasMessages;
    }

    public boolean getHasMessages() {
        return hasMessages;
    }

}
