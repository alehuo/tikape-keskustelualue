package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import tikape.runko.domain.Message;
import tikape.runko.domain.MessageThread;

/**
 * Viestiketju DAO
 */
public class TopicDao implements Dao<MessageThread, Integer> {

    private final Database database;

    /**
     * Viestiketju Dao
     *
     * @param database Tietokantaobjekti
     */
    public TopicDao(Database database) {
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
        /*
        Emme löytäneet syytä sille, miksi SQLitellä toimivat kyselyt eivät toimiveet PostgreSQL:n kanssa.
        Todella mystinen ongelma. Siksi on käytetty kolmea eri kyselyä.
         */
        Connection connection = database.getConnection();
        //1 Hae viestiketjun perustiedot
//        String query
//                = "SELECT "
//                + "threads.threadId, "
//                + "users.username AS creator, "
//                + "threads.title, "
//                + "threads.creationDate, "
//                + "COUNT(posts.postId) AS postCount , "
//                + "posts.timestamp AS latestPostTimestamp,"
//                + "(SELECT username FROM users WHERE userId = posts.userId) AS latestPostUserName "
//                + "FROM threads "
//                + "INNER JOIN users "
//                + "ON threads.userId = users.userId "
//                + "INNER JOIN posts ON posts.threadId = threads.threadId "
//                + "WHERE threads.subCategoryId = ? "
//                + "GROUP BY threads.threadId "
//                + "ORDER BY posts.postId DESC;";
//        PreparedStatement stmt = connection.prepareStatement("SELECT threads.threadId, users.username AS creator, threads.title, threads.creationDate, COUNT(posts.postId) AS postCount FROM threads INNER JOIN users ON threads.userId = users.userId INNER JOIN posts ON posts.threadId = threads.threadId WHERE threads.subCategoryId = ? GROUP BY threads.threadId");
        PreparedStatement stmt = connection.prepareStatement("SELECT threads.threadId, threads.title, threads.creationDate, users.username AS creator FROM threads INNER JOIN users ON threads.userId = users.userId WHERE threads.threadId = ?");

        stmt.setInt(1, key);
        ResultSet rs = stmt.executeQuery();

        List<MessageThread> msgThreads = new ArrayList<>();
        if (!rs.next()) {
            return null;
        }
        //1
        Integer threadId = rs.getInt("threadId");
        String title = rs.getString("title");
        String creationDate = rs.getString("creationDate");
        String creationUsername = rs.getString("creator");
        //2 Hae viestien lukumäärä viestiketjussa
        PreparedStatement stmt2 = connection.prepareStatement("SELECT COUNT(*) AS messageCount FROM posts INNER JOIN threads ON posts.threadId = threads.threadId WHERE threads.threadId = ?");
        stmt2.setInt(1, threadId);
        ResultSet rs2 = stmt2.executeQuery();
        if (!rs2.next()) {
            return null;
        }
        int postCount = rs2.getInt("messageCount");
        MessageThread mt = new MessageThread(threadId, title, creationDate, creationUsername, postCount);
        PreparedStatement stmt3 = connection.prepareStatement("SELECT users.username AS latestPostUsername, posts.timestamp AS latestPostTimestamp FROM posts INNER JOIN threads ON posts.threadId = threads.threadId INNER JOIN users ON posts.userId = users.userId WHERE threads.threadId = ? ORDER BY posts.postId DESC");
        stmt3.setInt(1, threadId);
        ResultSet rs3 = stmt3.executeQuery();
        if (rs3.next()) {
            mt.setLatestPostTimestamp(rs3.getString("latestPostTimestamp"));
            mt.setLatestPostUsername(rs3.getString("latestPostUsername"));
        }

        //3 Hae viimeisin viesti
        rs.close();
        stmt.close();
        connection.close();

        return mt;
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
        /*
        Emme löytäneet syytä sille, miksi SQLitellä toimivat kyselyt eivät toimiveet PostgreSQL:n kanssa.
        Todella mystinen ongelma. Siksi on käytetty kolmea eri kyselyä.
         */
        Connection connection = database.getConnection();
        //1 Hae viestiketjun perustiedot
//        String query
//                = "SELECT "
//                + "threads.threadId, "
//                + "users.username AS creator, "
//                + "threads.title, "
//                + "threads.creationDate, "
//                + "COUNT(posts.postId) AS postCount , "
//                + "posts.timestamp AS latestPostTimestamp,"
//                + "(SELECT username FROM users WHERE userId = posts.userId) AS latestPostUserName "
//                + "FROM threads "
//                + "INNER JOIN users "
//                + "ON threads.userId = users.userId "
//                + "INNER JOIN posts ON posts.threadId = threads.threadId "
//                + "WHERE threads.subCategoryId = ? "
//                + "GROUP BY threads.threadId "
//                + "ORDER BY posts.postId DESC;";
//        PreparedStatement stmt = connection.prepareStatement("SELECT threads.threadId, users.username AS creator, threads.title, threads.creationDate, COUNT(posts.postId) AS postCount FROM threads INNER JOIN users ON threads.userId = users.userId INNER JOIN posts ON posts.threadId = threads.threadId WHERE threads.subCategoryId = ? GROUP BY threads.threadId");
        PreparedStatement stmt = connection.prepareStatement("SELECT threads.threadId, threads.title, threads.creationDate, users.username AS creator FROM threads INNER JOIN users ON threads.userId = users.userId WHERE threads.subCategoryId = ?");

        stmt.setInt(1, subCategoryId);
        ResultSet rs = stmt.executeQuery();

        List<MessageThread> msgThreads = new ArrayList<>();
        while (rs.next()) {
            //1
            Integer threadId = rs.getInt("threadId");
            String title = rs.getString("title");
            String creationDate = rs.getString("creationDate");
            String creationUsername = rs.getString("creator");
            //2 Hae viestien lukumäärä viestiketjussa
            PreparedStatement stmt2 = connection.prepareStatement("SELECT COUNT(*) AS messageCount FROM posts INNER JOIN threads ON posts.threadId = threads.threadId WHERE threads.threadId = ?");
            stmt2.setInt(1, threadId);
            ResultSet rs2 = stmt2.executeQuery();
            if (!rs2.next()) {
                return null;
            }
            int postCount = rs2.getInt("messageCount");
            MessageThread mt = new MessageThread(threadId, title, creationDate, creationUsername, postCount);
            PreparedStatement stmt3 = connection.prepareStatement("SELECT users.username AS latestPostUsername, posts.timestamp AS latestPostTimestamp FROM posts INNER JOIN threads ON posts.threadId = threads.threadId INNER JOIN users ON posts.userId = users.userId WHERE threads.threadId = ? ORDER BY posts.postId DESC");
            stmt3.setInt(1, threadId);
            ResultSet rs3 = stmt3.executeQuery();
            if (rs3.next()) {
                mt.setLatestPostTimestamp(rs3.getString("latestPostTimestamp"));
                mt.setLatestPostUsername(rs3.getString("latestPostUsername"));
            }

            msgThreads.add(mt);
        }

        //3 Hae viimeisin viesti
        rs.close();
        stmt.close();
        connection.close();

        return msgThreads;
    }

    /**
     * Poistaa viestiketjun
     *
     * @param key Viestiketjun ID
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

        //Poimitaan uuden viestiketjun ID kun kysely on suoritettu
        stmt.execute();
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

    /**
     * Poistaa kaikki viestiketjut tietyn alakategorian ID:n perusteella
     *
     * @param id Alakategorian ID
     * @throws SQLException
     */
    public void deleteAllFromSubCategory(int id) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("DELETE FROM threads WHERE subCategoryId = ?");
        stmt.setInt(1, id);
        stmt.execute();
    }

}
