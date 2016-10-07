package tikape.runko.domain;

public class Message {

    private final String body;
    private final int userId;
    private final int messageId;
    private final String timeStamp;

    /**
     * Viesti -luokka
     * @param messageId Viestin ID
     * @param userId Käyttäjätunnuksen ID
     * @param body Viestin sisältö
     * @param timeStamp Viestin aikaleima
     */
    public Message(int messageId, int userId, String body, String timeStamp) {
        this.messageId = messageId;
        this.userId = userId;
        this.body = body;
        this.timeStamp = timeStamp;
    }

    /**
     * Palauttaa viestin sisällön
     * @return 
     */
    public String getBody() {
        return body;
    }

    /**
     * Palauttaa käyttäjätunnuksen ID:n
     * @return Käyttäjätunnuksen ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Palauttaa viestiketjun aikaleiman
     * @return Viestiketjun aikaleima
     */
    public String getTimeStamp() {
        return timeStamp;
    }
}
