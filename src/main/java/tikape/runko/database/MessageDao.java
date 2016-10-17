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
 *
 * @author jussiliu
 */
public class MessageDao implements Dao<Message, Integer> {

    private final Database database;

    public MessageDao(Database database) {
        this.database = database;
    }

    @Override
    public Message findOne(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Message> findAll() throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete(Integer key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

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

    public void add(Message m) throws SQLException {
        int id = m.getThreadId();
        int uId = m.getUserId();
        String ts = m.getTimestamp();
        String body = m.getBody();

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO posts (threadId , userId, timestamp , body) VALUES (?, ?, ?, ?)");
        stmt.setInt(1, id);
        stmt.setInt(2, uId);
        stmt.setString(3, ts);
        stmt.setString(4, body);
        stmt.execute();

    }

}
