package tikape.runko.domain;

import java.util.ArrayList;
import java.util.List;

public class MessageThread {

    private List<Message> messages;
    private final int subCatId;
    private final int userId;
    private final String title;
    private final String creationDate;

    /**
     * Viestiketjujen käsittelyyn tarkoitettu luokka
     *
     * @param subCatId Alakategoria ID
     * @param userId Käyttäjätunnuksen ID
     * @param title Viestiketjun otsikko
     * @param creationDate Aikaleima muodossa YYYY-MM-DD HH:MM:SS
     */
    public MessageThread(int subCatId, int userId, String title, String creationDate) {
        messages = new ArrayList<>();
        this.subCatId = subCatId;
        this.userId = userId;
        this.title = title;
        this.creationDate = creationDate;
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
        return messages.size();
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

    /**
     * Palauttaa viestiketjun otsikon ja aikaleiman
     *
     * @return Viestiketjun otsikko ja aikaleima muodossa otsikko (aikaleima)
     */
    @Override
    public String toString() {
        return title + " (" + creationDate + ")";
    }

}
