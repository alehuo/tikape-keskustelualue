package tikape.runko.domain;

import java.util.ArrayList;
import java.util.List;

public class MessageThread {

    private List<Message> messages;
    private int subCatId;
    private int userId;
    private String latestPostUsername;
    private String title;
    private String creationDate;
    private String creationUsername;
    private int threadId;
    private int messageCount = 0;

    /**
     * Viestiketjujen käsittelyyn tarkoitettu luokka
     *
     * @param subCatId Alakategoria ID
     * @param threadId
     * @param userId Käyttäjätunnuksen ID
     * @param title Viestiketjun otsikko
     * @param creationDate Aikaleima muodossa YYYY-MM-DD HH:MM:SS
     */
    public MessageThread(int subCatId, int threadId, int userId, String title, String creationDate) {
        messages = new ArrayList<>();
        this.threadId = threadId;
        this.subCatId = subCatId;
        this.userId = userId;
        this.title = title;
        this.creationDate = creationDate;
    }

    /**
     * Viestiketjujen käsittelyyn tarkoitettu luokka
     *
     * @param subCatId Alakategoria ID
     * @param userId Käyttäjätunnuksen ID
     * @param title Viestiketjun otsikko
     * @param creationDate Aikaleima muodossa YYYY-MM-DD HH:MM:SS
     */
    public MessageThread(int subCatId, int userId, String title, String creationDate) {
        this(subCatId, -1, userId, title, creationDate);
        messages = new ArrayList<>();
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
        if (messages.size() != 0) {
            return messages.size();
        }
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
        return creationDate;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public int getSubCatId() {
        return subCatId;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public int getThreadId() {
        return threadId;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public void setSubCatId(int subCatId) {
        this.subCatId = subCatId;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Palauttaa viestiketjun otsikon ja aikaleiman
     *
     * @return Viestiketjun otsikko ja aikaleima muodossa otsikko (aikaleima)
     */
    @Override
    public String toString() {
        return title + " (" + creationDate + ")";
    }

    public MessageThread setLatestPostUsername(String username) {
        latestPostUsername = username;
        return this;
    }

    public String getLatestPostUsername() {
        return latestPostUsername;
    }

    public String getCreationUsername() {
        return creationUsername;
    }

    public MessageThread setCreationUsername(String creationUsername) {
        this.creationUsername = creationUsername;
        return this;
    }

}
