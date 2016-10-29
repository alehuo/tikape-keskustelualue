package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.SubCategory;
import tikape.runko.domain.Topic;

/**
 * Viestiketju DAO
 */
public class TopicDao implements Dao<Topic, Integer> {

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
    public Topic findOne(Integer key) throws SQLException {

        Connection connection = database.getConnection();

        //1 Hae viestiketjun perustiedot
        PreparedStatement stmt = connection.prepareStatement("SELECT threads.threadId, threads.title, threads.timestamp, users.username AS creator FROM threads INNER JOIN users ON threads.userId = users.userId WHERE threads.threadId = ?");

        stmt.setInt(1, key);
        ResultSet rs = stmt.executeQuery();

        List<Topic> msgThreads = new ArrayList<>();
        if (!rs.next()) {
            return null;
        }

        Integer threadId = rs.getInt("threadId");
        String title = rs.getString("title");
        String timestamp = rs.getString("timestamp");
        String creationUsername = rs.getString("creator");

        //2 Hae viestien lukumäärä viestiketjussa
        PreparedStatement stmt2 = connection.prepareStatement("SELECT COUNT(*) AS messageCount FROM posts INNER JOIN threads ON posts.threadId = threads.threadId WHERE threads.threadId = ?");
        stmt2.setInt(1, threadId);

        ResultSet rs2 = stmt2.executeQuery();

        if (!rs2.next()) {
            return null;
        }

        int postCount = rs2.getInt("messageCount");

        Topic mt = new Topic(threadId, title, timestamp, creationUsername, postCount);

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
    public List<Topic> findAll() throws SQLException {
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
    public List<Topic> findAllFromSubCategory(int subCategoryId) throws SQLException {

        Connection connection = database.getConnection();

        //1 Hae viestiketjun perustiedot
        PreparedStatement stmt = connection.prepareStatement("SELECT threads.threadId, threads.title, threads.timestamp, users.username AS creator FROM threads INNER JOIN users ON threads.userId = users.userId WHERE threads.subCategoryId = ? ORDER BY threads.timestamp DESC");

        stmt.setInt(1, subCategoryId);
        ResultSet rs = stmt.executeQuery();

        List<Topic> msgThreads = new ArrayList<>();

        while (rs.next()) {

            //1
            Integer threadId = rs.getInt("threadId");
            String title = rs.getString("title");
            String timestamp = rs.getString("timestamp");
            String creationUsername = rs.getString("creator");

            //2 Hae viestien lukumäärä viestiketjussa
            PreparedStatement stmt2 = connection.prepareStatement("SELECT COUNT(*) AS messageCount FROM posts INNER JOIN threads ON posts.threadId = threads.threadId WHERE threads.threadId = ?");
            stmt2.setInt(1, threadId);

            ResultSet rs2 = stmt2.executeQuery();

            if (!rs2.next()) {
                return null;
            }

            int postCount = rs2.getInt("messageCount");

            Topic mt = new Topic(threadId, title, timestamp, creationUsername, postCount);

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
     * Hakee kaikki viestiketjut tietystä alakategoriasta, sivunumeron mukaan
     *
     * @param subCategoryId Alakategorian id
     * @param pageNumber Sivunumero
     * @return Lista viestiketjuista
     * @throws SQLException
     */
    public List<Topic> findAllFromSubCategoryByPageNumber(int subCategoryId, int pageNumber) throws SQLException {

        Connection connection = database.getConnection();

        //1 Hae viestiketjun perustiedot
        PreparedStatement stmt = connection.prepareStatement("SELECT threads.threadId, threads.title, threads.timestamp, users.username AS creator FROM threads INNER JOIN users ON threads.userId = users.userId WHERE threads.subCategoryId = ? ORDER BY threads.timestamp DESC");

        stmt.setInt(1, subCategoryId);
        ResultSet rs = stmt.executeQuery();

        //Aloitusindeksi
        int startingIndex = SubCategory.topicsPerPage * (pageNumber - 1) + 1;
        //Lopetusindeksi
        int endingIndex = SubCategory.topicsPerPage * pageNumber;

        List<Topic> msgThreads = new ArrayList<>();

        int index = 1;
        while (rs.next()) {

            //Jos viestin ID ei ole sivuvälillä, jatka
            if (!(index >= startingIndex && index <= endingIndex)) {
                index++;
                continue;
            } else {
                index++;
            }

            //1
            Integer threadId = rs.getInt("threadId");
            String title = rs.getString("title");
            String timestamp = rs.getString("timestamp");
            String creationUsername = rs.getString("creator");

            //2 Hae viestien lukumäärä viestiketjussa
            PreparedStatement stmt2 = connection.prepareStatement("SELECT COUNT(*) AS messageCount FROM posts INNER JOIN threads ON posts.threadId = threads.threadId WHERE threads.threadId = ?");
            stmt2.setInt(1, threadId);

            ResultSet rs2 = stmt2.executeQuery();

            if (!rs2.next()) {
                return null;
            }

            int postCount = rs2.getInt("messageCount");

            Topic mt = new Topic(threadId, title, timestamp, creationUsername, postCount);

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
     * Palauttaa aiheiden lukumäärän alakategoriasta
     *
     * @param subCategoryId Alakategorian ID
     * @return Aiheiden lukumäärä
     * @throws SQLException
     */
    public int getTopicCountFromSubCategory(int subCategoryId) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(threadId) AS topicCount FROM threads WHERE subCategoryId = ?");
        stmt.setInt(1, subCategoryId);
        ResultSet rs = stmt.executeQuery();
        if (!rs.next()) {
            return 0;
        }
        int topicCount = rs.getInt("topicCount");
        rs.close();
        stmt.close();
        connection.close();
        return topicCount;
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
    public void add(Topic msgThread) throws SQLException {
        Connection connection = database.getConnection();
        //Lisätään uusi viestiketju
        String query = "INSERT INTO threads (subCategoryId, userId, title, timestamp) VALUES (?,?,?,?)";
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
                //Oletetaan että Topic -olioon on lisätty yksi viesti
                stmt2.setString(3, msgThread.getMessages().get(0).getTimestamp());
                stmt2.setString(4, msgThread.getMessages().get(0).getBody());
                //Suorita kysely
                stmt2.execute();
                stmt2.close();
            }
        }
        stmt.close();
        connection.close();
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
        stmt.close();
        connection.close();
    }

}
