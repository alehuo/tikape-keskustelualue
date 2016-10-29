package tikape.runko.domain;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Aleksi Huotala
 */
public class Topic {

    /**
     *
     */
    public static int messagesPerPage = 10;

    private List<Message> messages;
    private int subCatId;
    private int userId;
    private String latestPostUsername;
    private String latestPostTimestamp;
    private String formattedLatestPostTimestamp;
    private String title;
    private String timestamp;
    private String creationUsername;
    private int threadId;
    private int messageCount = 0;
    private int pageCount;
    private int currentPage = 1;

    /**
     * Viestiketjujen käsittelyyn tarkoitettu luokka
     *
     * @param subCatId Alakategoria ID
     * @param threadId
     * @param userId Käyttäjätunnuksen ID
     * @param title Viestiketjun otsikko
     * @param timestamp Aikaleima muodossa YYYY-MM-DD HH:MM:SS
     */
    public Topic(int subCatId, int threadId, int userId, String title, String timestamp) {
        messages = new ArrayList<>();
        this.threadId = threadId;
        this.subCatId = subCatId;
        this.userId = userId;
        this.title = title;
        this.timestamp = timestamp;
    }

    /**
     * Viestiketjujen käsittelyyn tarkoitettu luokka
     *
     * @param subCatId Alakategoria ID
     * @param userId Käyttäjätunnuksen ID
     * @param title Viestiketjun otsikko
     * @param timestamp Aikaleima muodossa YYYY-MM-DD HH:MM:SS
     */
    public Topic(int subCatId, int userId, String title, String timestamp) {
        this(subCatId, -1, userId, title, timestamp);
        messages = new ArrayList<>();
    }

    /**
     * Viestiketjujen käsittelyyn tarkoitettu luokka
     *
     * @param threadId Viestiketjun ID
     * @param creator Viestiketjun luojan nimi
     * @param postCount Viestiketjun viestien lukumäärä
     * @param title Viestiketjun otsikko
     * @param timestamp Aikaleima muodossa YYYY-MM-DD HH:MM:SS
     */
    public Topic(int threadId, String title, String timestamp, String creator, int postCount) {
        this(-1, -1, -1, title, timestamp);
        this.threadId = threadId;
        messageCount = postCount;
        creationUsername = creator;
    }

    /**
     * Viestiketjujen käsittelyyn tarkoitettu luokka
     *
     * @param threadId Viestiketjun ID
     * @param title Viestiketjun otsikko
     * @param messageCount Viestien lukumäärä viestiketjussa
     */
    public Topic(int threadId, String title, int messageCount) {
        this(-1, threadId, -1, title, "");
        this.messageCount = messageCount;
    }

    /**
     * Lisää uusi viesti viestiketjuun
     *
     * @param message Message -olio
     */
    public void addMessage(Message message) {
        messages.add(message);
    }

    /**
     * Lisää kaikki viestit viestiketjuun listalta
     *
     * @param msgs Lista viesteistä
     */
    public void addAllMessages(List<Message> msgs) {
        messages.addAll(msgs);
    }

    /**
     * Palauttaa kaikki viestiketjun viestit
     *
     * @return Lista viesteistä
     */
    public List<Message> getMessages() {
        return messages;
    }

    /**
     * Palauttaa viestien lukumäärän viestiketjusta
     *
     * @return Viestien lukumäärä
     */
    public int getMessageCount() {
        return messageCount;
    }

    /**
     * Palauttaa viestiketjun viimeisimmän viestin käyttäjätunnuksen ID:n
     *
     * @return Käyttäjätunnuksen ID
     */
    public int getLatestPostUserId() {
        if (messages.size() > 0) {
            return messages.get(messages.size() - 1).getUserId();
        }
        return -1;
    }

    /**
     * Palauttaa alakategorian ID:n
     *
     * @return Alakategorian ID
     */
    public int getSubCategoryId() {
        return subCatId;
    }

    /**
     * Palauttaa käyttäjätunnuksen ID:n
     *
     * @return Käyttäjätunnuksen ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Palauttaa viestiketjun otsikon
     *
     * @return Viestiketjun otsikko
     */
    public String getTitle() {
        return title;
    }

    /**
     * Palauttaa viestiketjun aikaleiman
     *
     * @return Viestiketjun aikaleima
     */
    public String getTimeStamp() {
        return timestamp;
    }

    /**
     * Asettaa viestiketjun viestit
     *
     * @param messages Viestiketjun viestit
     */
    public void setMessages(List<Message> messages) {
        this.messages = messages;
        messageCount = messages.size();
    }

    /**
     * Palauttaa viestiketjun alakategorian ID:n
     *
     * @return Alakategorian ID
     */
    public int getSubCatId() {
        return subCatId;
    }

    /**
     * Palauttaa viestiketjun ID:n
     *
     * @return Viestiketjun ID
     */
    public int getThreadId() {
        return threadId;
    }

    /**
     * Asettaa viestiketjun luomispäiväyksen
     *
     * @param timestamp Luomispäiväys
     */
    public void setTimeStamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Asettaa viestien lukumäärän viestiketjussa
     *
     * @param messageCount Viestien lukumäärä
     */
    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
        pageCount = (int) Math.ceil(messageCount * 1.0 / messagesPerPage);
    }

    /**
     * Asettaa alakategorian ID:n
     *
     * @param subCatId Alakategorian ID
     */
    public void setSubCatId(int subCatId) {
        this.subCatId = subCatId;
    }

    /**
     * Asettaa viestiketjun ID:n
     *
     * @param threadId Viestiketjun ID
     */
    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    /**
     * Asettaa viestiketjun otsikon
     *
     * @param title Viestiketjun otsikko
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Asettaa viestiketjun luojan ID:n
     *
     * @param userId Käyttäjätunnuksen ID
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Asettaa viimeisimmän postauksen käyttäjänimen
     *
     * @param username Käyttäjänimi
     */
    public void setLatestPostUsername(String username) {
        latestPostUsername = username;

    }

    /**
     * Palauttaa viimeisimmän postauksen käyttäjänimen
     *
     * @return Käyttäjänimi
     */
    public String getLatestPostUsername() {
        return latestPostUsername;
    }

    /**
     * Palauttaa viestiketjun luojan käyttäjänimen
     *
     * @return Käyttäjänimi
     */
    public String getCreationUsername() {
        return creationUsername;
    }

    /**
     * Asettaa viestiketjun luojan käyttäjänimen
     *
     * @param creationUsername Käyttäjänimi
     */
    public void setCreationUsername(String creationUsername) {
        this.creationUsername = creationUsername;
    }

    /**
     * Palauttaa viestiketjun viimeisimmän postauksen aikaleiman
     *
     * @return Aikaleima
     */
    public String getLatestPostTimestamp() {
        return latestPostTimestamp;
    }

    /**
     * Palauttaa parsitun aikaleiman
     *
     * @return Aikaleima
     */
    public String getFormattedLatestPostTimestamp() {
        return formattedLatestPostTimestamp;
    }

    /**
     * Asettaa viestiketjun viimeisimmän postauksen aikaleiman
     *
     * @param latestPostTimestamp Aikaleima
     */
    public void setLatestPostTimestamp(String latestPostTimestamp) {
        this.latestPostTimestamp = latestPostTimestamp;
        setFormattedLatestPostTimestamp(latestPostTimestamp);
    }

    /**
     * Asettaa viestiketjun viimeisimmän postauksen parsitun aikaleiman
     *
     * @param latestPostTimestamp Aikaleima
     */
    public void setFormattedLatestPostTimestamp(String latestPostTimestamp) {
        formattedLatestPostTimestamp = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(java.sql.Timestamp.valueOf(latestPostTimestamp));
    }

    /**
     *
     * @param pages
     */
    public void setPageCount(int pages) {
        pageCount = pages;
    }

    /**
     *
     * @return
     */
    public int getPageCount() {
        return pageCount;
    }

    /**
     *
     * @param currentPage
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    /**
     *
     * @return
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * Palauttaa viestiketjun otsikon ja aikaleiman
     *
     * @return Viestiketjun otsikko ja aikaleima muodossa otsikko (aikaleima)
     */
    @Override
    public String toString() {
        return title + " (" + timestamp + ")";
    }

}
