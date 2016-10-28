package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Message;
import tikape.runko.domain.MessageThread;

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
     * @return Lista viesteistä
     * @throws SQLException
     */
    public List<Message> findAllFromTopicByPageNumber(int topicId, int pageNum) throws SQLException {

        Connection connection = database.getConnection();

        //Haetaan kaikki viestit viestiketjusta
        //PostgreSQL ei ymmärrä tätäkään kyselyä. Mitähän ihmettä..
//        PreparedStatement stmt = connection.prepareStatement("SELECT posts.postId, posts.userId, users.username, posts.body, posts.timestamp, (SELECT COUNT(*) FROM posts AS posts2 WHERE posts2.postId <= posts.postId  AND posts2.threadId = ?) AS row_index FROM posts INNER JOIN users ON posts.userId = users.userId WHERE posts.threadId = ? AND row_index BETWEEN ? AND ?");
        PreparedStatement stmt = connection.prepareStatement("SELECT posts.postId, posts.userId, users.username, posts.body, posts.timestamp FROM posts INNER JOIN users ON posts.userId = users.userId WHERE posts.threadId = ?");
        stmt.setInt(1, topicId);
//        stmt.setInt(2, topicId);
        //Aloitus- ja lopetusindeksi
        /*
        MessagesPerPage = 10;
        pageNum = 1;
        start = 10 * (1 - 1) + 1 = 1
        end = 10 * 1 = 10
        
        pageNum = 2;
        start = 10 * (2 - 1) + 1 = 11
        end = 10 * 2 = 20
         */
        //Aloitusindeksi
        int startingIndex = MessageThread.messagesPerPage * (pageNum - 1) + 1;
//        stmt.setInt(3, startingIndex);
        //Lopetusindeksi
        int endingIndex = MessageThread.messagesPerPage * pageNum;
//        stmt.setInt(4, endingIndex);
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
