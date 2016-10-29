package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Message;
import tikape.runko.domain.Topic;

/**
 * MessageDao
 */
public class MessageDao implements Dao<Message, Integer> {

    /**
     * Tietokanta -olio
     */
    private final Database database;

    /**
     * MessageDao
     *
     * @param database Tietokanta -olio
     */
    public MessageDao(Database database) {
        this.database = database;
    }

    /**
     * Palauttaa viestin ID:n perusteella (EI TOTEUTETTU)
     *
     * @param key Viestin ID
     * @return Viesti
     * @throws SQLException
     */
    @Override
    public Message findOne(Integer key) throws SQLException {
        //Ei toteutettu
        return null;
    }

    /**
     * Palauttaa kaikki viestit (EI TOTEUTETTU)
     *
     * @return Lista viesteistä
     * @throws SQLException
     */
    @Override
    public List<Message> findAll() throws SQLException {
        List<Message> msgs = new ArrayList<>();
        //Ei toteutettu
        return msgs;
    }

    /**
     * Poistaa viestin ID:n perusteella (EI TOTEUTETTU)
     *
     * @param key Viestin ID
     * @throws SQLException
     */
    @Override
    public void delete(Integer key) throws SQLException {
        //Ei toteutettu
    }

    /**
     * Palauttaa kaikki viestit viestiketjusta
     *
     * @param topicId Viestiketjun ID
     * @return Lista viesteistä
     * @throws SQLException
     */
    public List<Message> findAllFromTopic(int topicId) throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM posts INNER JOIN users ON posts.userId = users.userId WHERE posts.threadId = ?");
        stmt.setInt(1, topicId);
        ResultSet rs = stmt.executeQuery();

        List<Message> msg = new ArrayList<>();
        while (rs.next()) {
            Integer postId = rs.getInt("postId");
            Integer userId = rs.getInt("userId");
            String username = rs.getString("username");
            String body = rs.getString("body");
            String timeStamp = rs.getString("timestamp");
            Message m = new Message(postId, userId, body, timeStamp);
            m.setUsername(username);
            msg.add(m);
        }

        rs.close();
        stmt.close();
        connection.close();

        return msg;
    }

    /**
     * Palauttaa kaikki viestit viestiketjusta, rajoittaen näkymää. Jos
     * sivunumero on yksi, näytetään viestit väliltä [aloitus_id,aloitus_id +
     * 10] (Viestejä jokaisella sivulla on kymmenen!)
     *
     * @param topicId Viestiketjun ID
     * @param pageNum Sivunumero
     * @return Lista viesteistä
     * @throws SQLException
     */
    public List<Message> findAllFromTopicByPageNumber(int topicId, int pageNum) throws SQLException {

        Connection connection = database.getConnection();

        //Haetaan kaikki viestit viestiketjusta
        PreparedStatement stmt = connection.prepareStatement("SELECT posts.postId, posts.userId, users.username, posts.body, posts.timestamp FROM posts INNER JOIN users ON posts.userId = users.userId WHERE posts.threadId = ?");
        stmt.setInt(1, topicId);

        //Aloitusindeksi
        int startingIndex = Topic.messagesPerPage * (pageNum - 1) + 1;
        //Lopetusindeksi
        int endingIndex = Topic.messagesPerPage * pageNum;

        ResultSet rs = stmt.executeQuery();

        List<Message> msg = new ArrayList<>();
        int index = 1;
        while (rs.next()) {

            //Jos viestin ID ei ole sivuvälillä, jatka
            if (!(index >= startingIndex && index <= endingIndex)) {
                index++;
                continue;
            } else {
                index++;
            }

            Integer postId = rs.getInt("postId");
            Integer userId = rs.getInt("userId");
            String username = rs.getString("username");
            String body = rs.getString("body");
            String timeStamp = rs.getString("timestamp");

            Message m = new Message(postId, userId, body, timeStamp);
            m.setUsername(username);

            msg.add(m);

        }

        rs.close();
        stmt.close();
        connection.close();

        return msg;
    }

    public int getMessageCountFromTopic(int threadId) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(postId) AS postCount FROM posts WHERE threadId = ?");
        stmt.setInt(1, threadId);
        ResultSet rs = stmt.executeQuery();
        if (!rs.next()) {
            return 0;
        }
        int messageCount = rs.getInt("postCount");
        rs.close();
        stmt.close();
        return messageCount;

    }

    /**
     * Lisää uuden viestin tietokantaan
     *
     * @param m Viesti -olio
     * @throws SQLException
     */
    public void add(Message m) throws SQLException {

        int id = m.getThreadId();
        int uId = m.getUserId();
        String ts = m.getTimestamp();
        String body = m.getEscapedBody();

        Connection connection = database.getConnection();

        PreparedStatement stmt = connection.prepareStatement("INSERT INTO posts (threadId , userId, timestamp , body) VALUES (?, ?, ?, ?)");
        stmt.setInt(1, id);
        stmt.setInt(2, uId);
        stmt.setString(3, ts);
        stmt.setString(4, body);

        stmt.execute();
        stmt.close();

        PreparedStatement stmt2 = connection.prepareStatement("UPDATE threads SET timestamp = ? WHERE threads.threadId = ?");
        stmt2.setString(1, ts);
        stmt2.setInt(2, id);

        stmt2.execute();
        stmt2.close();
    }

    /**
     * Poistaa kaikki viestit alakategoriasta
     *
     * @param id Alakategorian ID
     * @throws SQLException
     */
    public void deleteAllFromSubCategory(int id) throws SQLException {

        Connection connection = database.getConnection();

        PreparedStatement stmt = connection.prepareStatement("DELETE FROM posts WHERE threadId IN(SELECT threadId FROM threads WHERE subCategoryId = ?); ");
        stmt.setInt(1, id);

        stmt.execute();
        stmt.close();
    }

}
