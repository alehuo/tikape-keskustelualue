package tikape.runko.domain;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {

    private final String body;
    private final int userId;
    private String username;
    private final int messageId;
    private final String timestamp;
    private int threadId;

    /**
     * Viesti -luokka
     *
     * @param messageId Viestin ID
     * @param userId Käyttäjätunnuksen ID
     * @param body Viestin sisältö
     * @param timeStamp Viestin aikaleima
     */
    public Message(int messageId, int userId, String body, String timeStamp) {
        this.messageId = messageId;
        this.userId = userId;
        this.body = body;
        this.timestamp = timeStamp.substring(0, 16);

    }

    public Message(int userId, String body, String timeStamp) {
        this(-1, userId, body, timeStamp);
    }

    public String getUsername() {
        return username;
    }

    public Message setUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * Palauttaa viestin sisällön
     *
     * @return
     */
    public String getBody() {
        return body;
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
     * Palauttaa viestiketjun aikaleiman
     *
     * @return Viestiketjun aikaleima
     */
    public String getTimestamp() {
        return timestamp;
    }

    public int getThreadId() {
        return threadId;
    }

    public void setThreadId(int threadId) {
        this.threadId = threadId;
    }

    public int getMessageId() {
        return messageId;
    }

}
