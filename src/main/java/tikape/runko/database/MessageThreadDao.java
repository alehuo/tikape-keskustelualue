package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.Message;
import tikape.runko.domain.MessageThread;

public class MessageThreadDao implements Dao<MessageThread, Integer> {

    private final Database database;

    /**
     * Viestiketju Dao
     *
     * @param database Tietokantaobjekti
     */
    public MessageThreadDao(Database database) {
        this.database = database;
    }

    /**
     * Hakee tietyn viestiketjun ID:n avulla
     *
     * @param key Viestiketjun ID
     * @return Viestiketju
     * @throws SQLException
     */
    @Override
    public MessageThread findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT DISTINCT threads.threadId, threads.subCategoryId, threads.userId, users.username, threads.title, threads.creationDate, COUNT(posts.postId) AS postCount FROM threads INNER JOIN users ON threads.userId = users.userId INNER JOIN posts ON posts.threadId = threads.threadId WHERE threads.threadId = ? GROUP BY threads.threadId");
        stmt.setInt(1, key);
        ResultSet rs = stmt.executeQuery();

        if (!rs.next()) {
            return null;
        }

        Integer subCatId = rs.getInt("subCategoryId");
        Integer threadId = rs.getInt("threadId");
        Integer userId = rs.getInt("userId");
        String title = rs.getString("title");
        String creationDate = rs.getString("creationDate");
        MessageThread msgThread = new MessageThread(subCatId, threadId, userId, title, creationDate).setCreationUsername(rs.getString("username"));
        msgThread.setMessageCount(rs.getInt("postCount"));
        rs.close();
        stmt.close();

        stmt = connection.prepareStatement("SELECT posts.postId, posts.userId, posts.body, posts.timestamp FROM posts WHERE threadId = ? ORDER BY postId ASC");
        stmt.setInt(1, key);
        ResultSet result = stmt.executeQuery();

        while (result.next()) {
            Integer postId = result.getInt("postId");
            userId = result.getInt("userId");
            String body = result.getString("body");
            String timeStamp = result.getString("timestamp");
            msgThread.addMessage(new Message(postId, userId, body, timeStamp));
        }
        result.close();

        connection.close();

        return msgThread;
    }

    /**
     * Hakee kaikki viestiketjut
     *
     * @return
     * @throws SQLException
     */
    @Override
    public List<MessageThread> findAll() throws SQLException {
        //Ei toteutettu
        return null;
    }

    /**
     * Hakee kaikki viestiketjut tietystä alakategoriasta
     *
     * @param subCategoryId Alakategorian id
     * @return Lista viestiketjuista
     * @throws SQLException
     */
    public List<MessageThread> findAllFromSubCategory(int subCategoryId) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT DISTINCT threads.threadId, threads.subCategoryId, threads.userId, users.username, threads.title, threads.creationDate, COUNT(posts.postId) AS postCount FROM threads INNER JOIN users ON threads.userId = users.userId INNER JOIN posts ON posts.threadId = threads.threadId WHERE threads.subCategoryId = ? GROUP BY threads.threadId");
        stmt.setInt(1, subCategoryId);
        ResultSet rs = stmt.executeQuery();

        List<MessageThread> msgThreads = new ArrayList<>();
        while (rs.next()) {
            Integer subCatId = rs.getInt("subCategoryId");
            Integer threadId = rs.getInt("threadId");
            Integer userId = rs.getInt("userId");
            String title = rs.getString("title");
            String creationDate = rs.getString("creationDate");
            int postCount = rs.getInt("postCount");
            String creationUsername = rs.getString("username");
            MessageThread mt = new MessageThread(subCategoryId, threadId, userId, title, creationDate);
            mt.setCreationUsername(creationUsername);
            mt.setMessageCount(postCount);
            msgThreads.add(mt);
        }

        rs.close();
        stmt.close();
        connection.close();

        return msgThreads;
    }

    /**
     * Poistaa viestiketjun
     *
     * @param key
     * @throws SQLException
     */
    @Override
    public void delete(Integer key) throws SQLException {
        //Ei toteutettu
    }

    /**
     * Uuden viestiketjun luonti. Samalla lisätään viestiketjuun aloituspostaus.
     *
     * @param msgThread Viestiketju-olio
     * @throws SQLException
     */
    public void add(MessageThread msgThread) throws SQLException {
        Connection connection = database.getConnection();
        //Lisätään uusi viestiketju
        String query = "INSERT INTO threads (subCategoryId, userId, title, creationDate) VALUES (?,?,?,?)";
        PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, msgThread.getSubCategoryId());
        stmt.setInt(2, msgThread.getUserId());
        stmt.setString(3, msgThread.getTitle());
        stmt.setString(4, msgThread.getTimeStamp());
        System.out.println(stmt.toString());
        //Poimitaan uuden viestiketjun ID kun kysely on suoritettu
        int affectedRows = stmt.executeUpdate();
        try (ResultSet insertId = stmt.getGeneratedKeys()) {
            if (insertId.next()) {
                //Tämän voisi korvata MessageDao:lla?
                query = "INSERT INTO posts (threadId, userId, timestamp, body) VALUES (?,?,?,?)";
                PreparedStatement stmt2 = connection.prepareStatement(query);
                stmt2.setInt(1, insertId.getInt(1));
                stmt2.setInt(2, msgThread.getUserId());
                //Oletetaan että MessageThread -olioon on lisätty yksi viesti
                stmt2.setString(3, msgThread.getMessages().get(0).getTimestamp());
                stmt2.setString(4, msgThread.getMessages().get(0).getBody());
                //Suorita kysely
                stmt2.execute();
            }

        }

    }

    public void deleteAllFromSubCategory(int id) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("DELETE FROM threads WHERE subCategoryId = ?");
        stmt.setInt(1, id);
        stmt.execute();
    }

}
