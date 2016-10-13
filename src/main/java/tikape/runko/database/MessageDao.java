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
import tikape.runko.domain.MessageThread;

/**
 *
 * @author jussiliu
 */
public class MessageDao implements Dao<Message, Integer>{

    
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
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM posts WHERE threadId = ?");
        stmt.setInt(1, topicId);
        ResultSet rs = stmt.executeQuery();

        List<Message> msg = new ArrayList<>();
        while (rs.next()) {
            Integer postId = rs.getInt("postId");
            Integer userId = rs.getInt("userId");
            String body = rs.getString("body");
            String timeStamp = rs.getString("timestamp");
            msg.add(new Message(postId, userId, body, timeStamp));
        }

        rs.close();
        stmt.close();
        connection.close();

        return msg;
    }
    
    
}
