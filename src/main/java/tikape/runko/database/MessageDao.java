/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Message;

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
            msg.add(new Message(postId, userId, body, timeStamp).setUsername(username));
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
    }

}
