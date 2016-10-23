package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.SubCategory;

/**
 * Alakategorian DAO
 */
public class SubCategoryDao implements Dao<SubCategory, Integer> {

    private final Database database;

    /**
     * Alakategorian DAO
     *
     * @param db Tietokantaolio
     */
    public SubCategoryDao(Database db) {
        this.database = db;
    }

    /**
     * Hakee alakategorian ID:n avulla
     *
     * @param key
     * @return
     * @throws SQLException
     */
    @Override
    public SubCategory findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
//        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM subCategories WHERE subCatId = ?");
        String query = "SELECT s.subCatId,"
                + "s.catId,"
                + "s.title AS subCategoryTitle, "
                + "s.description AS subCategoryDescription, "
                + "COUNT(posts.threadId) AS messageCount, "
                + "threads.threadId AS latestPostThreadId, "
                + "posts.timestamp AS latestPostTimestamp, "
                + "users.username AS latestPostUsername, "
                + "threads.title AS latestPostThreadTitle "
                + "FROM subCategories s "
                + "LEFT JOIN threads ON s.subCatId = threads.subCategoryId "
                + "LEFT JOIN posts ON threads.threadId = posts.threadId "
                + "LEFT JOIN users ON posts.userId = users.userId "
                + "WHERE s.subCatId = ? "
                + "GROUP BY s.subCatId "
                + "ORDER BY posts.postId DESC;";
        PreparedStatement stmt = connection.prepareStatement(query);

        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer subCatId = rs.getInt("subCatId");
        Integer mainCatId = rs.getInt("catId");
        String title = rs.getString("subCategoryTitle");
        String description = rs.getString("subCategoryDescription");
        int messageCount = rs.getInt("messageCount");
        int latestPostThreadId = rs.getInt("latestPostThreadId");
        String latestPostTimestamp = rs.getString("latestPostTimestamp");
        String latestPostUsername = rs.getString("latestPostUsername");
        String latestPostThreadTitle = rs.getString("latestPostThreadTitle");
        SubCategory cat = new SubCategory(mainCatId, subCatId, title);
        cat.setDescription(description);
        if (latestPostTimestamp != null) {
            cat.setHasMessages(true);
            cat.setLatestMessageThreadId(latestPostThreadId);
            cat.setLatestMessageThreadTitle(latestPostThreadTitle);
            cat.setLatestMessageTimestamp(latestPostTimestamp);
            cat.setLatestMessageUsername(latestPostUsername);
            cat.setMessageCount(messageCount);
        }
//
//        //Tämä kysely hakee uusimman viestin tietystä alakategoriasta. Haku palauttaa viestin timestampin, käyttäjätunnuksen, viestiketjun otsikon sekä ID:n.
//        String query = "SELECT posts.timestamp, users.username, threads.title, threads.threadId, COUNT(posts.postId) AS messageCount  FROM posts INNER JOIN threads ON posts.threadId = threads.threadId INNER JOIN users ON posts.userId = users.userId WHERE threads.subCategoryId = ? GROUP BY threads.subCategoryId ORDER BY posts.timestamp DESC";
//        PreparedStatement stmt2 = connection.prepareStatement(query);
//        stmt2.setInt(1, key);
//        //Tässä haetaan viimeisimmän viestin tiedot
//        ResultSet result = stmt2.executeQuery();
//        if (result.next() && result.getString("timestamp") != null) {
//            cat.setLatestMessageThreadId(result.getInt("threadId"));
//            cat.setLatestMessageThreadTitle(result.getString("title"));
//            cat.setLatestMessageTimestamp(result.getString("timestamp"));
//            cat.setLatestMessageUsername(result.getString("username"));
//            cat.setMessageCount(result.getInt("messageCount"));
//        }
        rs.close();
        stmt.close();
        connection.close();

        return cat;
    }

    /**
     * Hakee kaikki alakategoriat
     *
     * @return Lista alakategorioista
     * @throws SQLException
     */
    @Override
    public List<SubCategory> findAll() throws SQLException {
        Connection connection = database.getConnection();
//        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM subCategories");
        String query = "SELECT s.subCatId,"
                + "s.catId,"
                + "s.title AS subCategoryTitle, "
                + "s.description AS subCategoryDescription, "
                + "COUNT(posts.threadId) AS messageCount, "
                + "threads.threadId AS latestPostThreadId, "
                + "posts.timestamp AS latestPostTimestamp, "
                + "users.username AS latestPostUsername, "
                + "threads.title AS latestPostThreadTitle "
                + "FROM subCategories s "
                + "LEFT JOIN threads ON s.subCatId = threads.subCategoryId "
                + "LEFT JOIN posts ON threads.threadId = posts.threadId "
                + "LEFT JOIN users ON posts.userId = users.userId "
                + "GROUP BY s.subCatId "
                + "ORDER BY posts.postId DESC;";
        PreparedStatement stmt = connection.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();

        List<SubCategory> categories = new ArrayList<>();
        while (rs.next()) {
            Integer subCatId = rs.getInt("subCatId");
            Integer mainCatId = rs.getInt("catId");
            String title = rs.getString("subCategoryTitle");
            String description = rs.getString("subCategoryDescription");
            int messageCount = rs.getInt("messageCount");
            int latestPostThreadId = rs.getInt("latestPostThreadId");
            String latestPostTimestamp = rs.getString("latestPostTimestamp");
            String latestPostUsername = rs.getString("latestPostUsername");
            String latestPostThreadTitle = rs.getString("latestPostThreadTitle");
            SubCategory cat = new SubCategory(mainCatId, subCatId, title);
            cat.setDescription(description);
            if (latestPostTimestamp != null) {
                cat.setHasMessages(true);
                cat.setLatestMessageThreadId(latestPostThreadId);
                cat.setLatestMessageThreadTitle(latestPostThreadTitle);
                cat.setLatestMessageTimestamp(latestPostTimestamp);
                cat.setLatestMessageUsername(latestPostUsername);
                cat.setMessageCount(messageCount);
            }
//            Integer mainCatId = rs.getInt("catId");
//            Integer subCatId = rs.getInt("categoryId");
//            String title = rs.getString("title");
//            String description = rs.getString("description");
//
//            SubCategory cat = new SubCategory(mainCatId, subCatId, title).setDescription(description);
//            //Haetaan vielä viimeisin tieto
//            String query = "SELECT posts.timestamp, users.username, threads.title, threads.threadId, COUNT(posts.postId) AS messageCount  FROM posts INNER JOIN threads ON posts.threadId = threads.threadId INNER JOIN users ON posts.userId = users.userId WHERE threads.subCategoryId = ? GROUP BY threads.subCategoryId ORDER BY posts.timestamp DESC";
//            PreparedStatement stmt2 = connection.prepareStatement(query);
//            stmt2.setInt(1, cat.getSubCategoryId());
//            //Tässä haetaan viimeisimmän viestin tiedot
//            ResultSet result = stmt2.executeQuery();
//            if (result.next() && result.getString("timestamp") != null) {
//                cat.setLatestMessageThreadId(result.getInt("threads.threadId"));
//                cat.setLatestMessageThreadTitle(result.getString("threads.title"));
//                cat.setLatestMessageTimestamp(result.getString("posts.timestamp"));
//                cat.setLatestMessageUsername(result.getString("users.username"));
//                cat.setMessageCount(result.getInt("messageCount"));
//            }
            categories.add(cat);
//            result.close();
//            stmt2.close();

        }

        rs.close();
        stmt.close();

        connection.close();

        return categories;
    }

    /**
     * Listaa kaikki alakategoriat halutulle pääkategorian ID:lle
     *
     * @param categoryId
     * @return
     * @throws SQLException
     */
    public List<SubCategory> findAllByCategoryId(int categoryId) throws SQLException {
        Connection connection = database.getConnection();
//        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM subCategories WHERE catId = ?");
        String query = "SELECT s.subCatId,"
                + "s.catId,"
                + "s.title AS subCategoryTitle, "
                + "s.description AS subCategoryDescription, "
                + "COUNT(posts.threadId) AS messageCount, "
                + "threads.threadId AS latestPostThreadId, "
                + "posts.timestamp AS latestPostTimestamp, "
                + "users.username AS latestPostUsername, "
                + "threads.title AS latestPostThreadTitle "
                + "FROM subCategories s "
                + "LEFT JOIN threads ON s.subCatId = threads.subCategoryId "
                + "LEFT JOIN posts ON threads.threadId = posts.threadId "
                + "LEFT JOIN users ON posts.userId = users.userId "
                + "WHERE s.catId = ? "
                + "GROUP BY s.subCatId "
                + "ORDER BY s.subCatId ASC, posts.postId DESC;";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, categoryId);
        ResultSet rs = stmt.executeQuery();

        List<SubCategory> categories = new ArrayList<>();
        while (rs.next()) {
            Integer subCatId = rs.getInt("subCatId");
            Integer mainCatId = rs.getInt("catId");
            String title = rs.getString("subCategoryTitle");
            String description = rs.getString("subCategoryDescription");
            int messageCount = rs.getInt("messageCount");
            int latestPostThreadId = rs.getInt("latestPostThreadId");
            String latestPostTimestamp = rs.getString("latestPostTimestamp");
            String latestPostUsername = rs.getString("latestPostUsername");
            String latestPostThreadTitle = rs.getString("latestPostThreadTitle");
            SubCategory cat = new SubCategory(mainCatId, subCatId, title);
            cat.setDescription(description);
            if (latestPostTimestamp != null) {
                cat.setHasMessages(true);
                cat.setLatestMessageThreadId(latestPostThreadId);
                cat.setLatestMessageThreadTitle(latestPostThreadTitle);
                cat.setLatestMessageTimestamp(latestPostTimestamp);
                cat.setLatestMessageUsername(latestPostUsername);
                cat.setMessageCount(messageCount);
            }

//            Integer mainCatId = rs.getInt("catId");
//            Integer subCatId = rs.getInt("subCatId");
//            String title = rs.getString("title");
//            String description = rs.getString("description");
//
//            SubCategory cat = new SubCategory(mainCatId, subCatId, title).setDescription(description);
//            //Haetaan vielä viimeisin tieto
//            String query = "SELECT posts.timestamp, users.username, threads.title, threads.threadId, COUNT(posts.postId) AS messageCount  FROM posts INNER JOIN threads ON posts.threadId = threads.threadId INNER JOIN users ON posts.userId = users.userId WHERE threads.subCategoryId = ? GROUP BY threads.subCategoryId ORDER BY posts.timestamp DESC";
//            PreparedStatement stmt2 = connection.prepareStatement(query);
//            stmt2.setInt(1, cat.getSubCategoryId());
//            //Tässä haetaan viimeisimmän viestin tiedot
//            ResultSet result = stmt2.executeQuery();
//            if (result.next() && result.getString("timestamp") != null) {
//                cat.setHasMessages(true);
//                cat.setLatestMessageThreadId(result.getInt("threadId"));
//                cat.setLatestMessageThreadTitle(result.getString("title"));
//                cat.setLatestMessageTimestamp(result.getString("timestamp"));
//                cat.setLatestMessageUsername(result.getString("username"));
//                cat.setMessageCount(result.getInt("messageCount"));
//            }
            categories.add(cat);
//            result.close();
//            stmt2.close();
        }

        rs.close();
        stmt.close();
        connection.close();

        return categories;
    }

    /**
     * Poistaa alakategorian ID:n perusteella
     *
     * @param key Alakategorian ID
     * @throws SQLException
     */
    @Override
    public void delete(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("DELETE FROM subCategories WHERE subCatId = ?;");
        stmt.setInt(1, key);
        stmt.execute();
    }

    /**
     * Lisää alakategorian
     *
     * @param c Alakategoria
     * @return true tai false
     * @throws SQLException
     */
    public boolean add(SubCategory c) throws SQLException {
        Connection connection = database.getConnection();
        int mainCatId = c.getCategoryId();
        String title = c.getName();
        String desc = c.getDescription();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO subCategories (catId, title , description) VALUES (?, ?, ?)");
        stmt.setInt(1, mainCatId);
        stmt.setString(2, title);
        stmt.setString(3, desc);
        return stmt.execute();
    }

}
