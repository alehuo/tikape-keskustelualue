package tikape.runko.domain;

/**
 *
 * @author Aleksi Huotala
 */
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
    private int messageCount = 0;

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

    /**
     * Alakategoria -luokka
     *
     * @param categoryId Yläkategorian ID
     * @param name Alakategorian nimi
     */
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

    /**
     * Asettaa yläkategorian ID:n
     *
     * @param categoryId Yläkategorian ID
     */
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * Asettaa alakategorian nimen
     *
     * @param name Alakategorian nimi
     */
    public void setName(String name) {
        this.name = name.trim();
    }

    /**
     * Asettaa alakategorian ID:n
     *
     * @param subCategoryId Alakategorian ID
     */
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

    /**
     * Palauttaa viimeisimmän viestin aikaleiman
     *
     * @return Viestin aikaleima
     */
    public String getLatestMessageTimestamp() {
        return latestMessageTimestamp;
    }

    /**
     * Asettaa viimeisimmän viestin aikaleiman
     *
     * @param latestMessageTimestamp Viestin aikaleima
     */
    public void setLatestMessageTimestamp(String latestMessageTimestamp) {
        this.latestMessageTimestamp = latestMessageTimestamp.substring(0, 16);
    }

    /**
     * Palauttaa viimeisimmän viestin ID:n
     *
     * @return Viimeisimmän viestin ID
     */
    public int getLatestMessageThreadId() {
        return latestMessageThreadId;
    }

    /**
     * Asettaa viimeisimmän viestin ID:n
     *
     * @param latestMessageThreadId Viimeisimmän viestin ID
     */
    public void setLatestMessageThreadId(int latestMessageThreadId) {
        this.latestMessageThreadId = latestMessageThreadId;
    }

    /**
     * Palauttaa viimeisimmän viestin kirjoittajan käyttäjätunnuksen
     *
     * @return Käyttäjätunnuksen nimi
     */
    public String getLatestMessageUsername() {
        return latestMessageUsername;
    }

    /**
     * Asettaa viimeisimmän viestin kirjoittajan käyttäjätunnuksen
     *
     * @param latestMessageUsername Käyttäjätunnuksen nimi
     */
    public void setLatestMessageUsername(String latestMessageUsername) {
        this.latestMessageUsername = latestMessageUsername;
    }

    /**
     * Asettaa viimeisimmän viestiketjun nimen
     *
     * @param latestMessageThreadTitle Viestiketjun nimi
     */
    public void setLatestMessageThreadTitle(String latestMessageThreadTitle) {
        this.latestMessageThreadTitle = latestMessageThreadTitle;
    }

    /**
     * Palauttaa viimeisimmän viestiketjun nimen
     *
     * @return Viestiketjun nimi
     */
    public String getLatestMessageThreadTitle() {
        return latestMessageThreadTitle;
    }

    /**
     * Asettaa tilan, jolla ilmoitetaan se, onko viestiketjussa viestejä
     *
     * @param hasMessages Onko viestiketjussa viestejä vai ei
     */
    public void setHasMessages(boolean hasMessages) {
        this.hasMessages = hasMessages;
    }

    /**
     * Palauttaa tilan, jolla ilmoitetaan se, onko viestiketjussa viestejä
     *
     * @return
     */
    public boolean getHasMessages() {
        return hasMessages;
    }

    /**
     * Asettaa viestiketjun viestien lukumäärän
     *
     * @param messageCount Viestien lukumäärä
     */
    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    /**
     * Palauttaa viestiketjun viestien lukumäärän
     *
     * @return Viestiketjun viestien lukumäärä
     */
    public int getMessageCount() {
        return messageCount;
    }

}
