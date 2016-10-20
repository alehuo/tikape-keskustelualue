package tikape.runko.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import tikape.runko.domain.SubCategory;

public class SubCategoryDao implements Dao<SubCategory, Integer> {

    private final Database database;

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
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM subCategories WHERE subCatId = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer subCatId = rs.getInt("subCatId");
        Integer mainCatId = rs.getInt("catId");
        String title = rs.getString("title");
        String description = rs.getString("description");
        SubCategory cat = new SubCategory(mainCatId, subCatId, title).setDescription(description);

        //Tämä kysely hakee uusimman viestin tietystä alakategoriasta. Haku palauttaa viestin timestampin, käyttäjätunnuksen, viestiketjun otsikon sekä ID:n.
        String query = "SELECT posts.timestamp, users.username, threads.title, threads.threadId FROM posts INNER JOIN threads ON posts.threadId = threads.threadId INNER JOIN users ON posts.userId = users.userId WHERE threads.subCategoryId = ? ORDER BY posts.timestamp DESC LIMIT 1";
        PreparedStatement stmt2 = connection.prepareStatement(query);
        stmt2.setInt(1, key);
        //Tässä haetaan viimeisimmän viestin tiedot
        ResultSet result = stmt2.executeQuery();
        if (result.next()) {
            cat.setLatestMessageThreadId(result.getInt("threadId"));
            cat.setLatestMessageThreadTitle(result.getString("title"));
            cat.setLatestMessageTimestamp(result.getString("timestamp"));
            cat.setLatestMessageUsername(result.getString("username"));
        }
        rs.close();
        stmt.close();
        connection.close();

        return cat;
    }

    /**
     * Hakee kaikki alakategoriat
     *
     * @return
     * @throws SQLException
     */
    @Override
    public List<SubCategory> findAll() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM subCategories");
        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();

        List<SubCategory> categories = new ArrayList<>();
        while (rs.next()) {
            Integer mainCatId = rs.getInt("catId");
            Integer subCatId = rs.getInt("categoryId");
            String title = rs.getString("title");
            String description = rs.getString("description");

            SubCategory cat = new SubCategory(mainCatId, subCatId, title).setDescription(description);
            //Haetaan vielä viimeisin tieto
            String query = "SELECT posts.timestamp, users.username, threads.title, threads.threadId FROM posts INNER JOIN threads ON posts.threadId = threads.threadId INNER JOIN users ON posts.userId = users.userId WHERE threads.subCategoryId = ? ORDER BY posts.timestamp DESC LIMIT 1";
            PreparedStatement stmt2 = connection.prepareStatement(query);
            stmt2.setInt(1, cat.getSubCategoryId());
            //Tässä haetaan viimeisimmän viestin tiedot
            ResultSet result = stmt2.executeQuery();
            if (result.next()) {
                System.out.println(cat.getLatestMessageThreadId());
                System.out.println(cat.getLatestMessageThreadTitle());
                System.out.println(cat.getLatestMessageTimestamp());
                System.out.println(cat.getLatestMessageUsername());
                cat.setLatestMessageThreadId(result.getInt("threads.threadId"));
                cat.setLatestMessageThreadTitle(result.getString("threads.title"));
                cat.setLatestMessageTimestamp(result.getString("posts.timestamp"));
                cat.setLatestMessageUsername(result.getString("users.username"));
            }
            categories.add(cat);
            result.close();
            stmt2.close();

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
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM subCategories WHERE catId = ?");
        stmt.setInt(1, categoryId);
        ResultSet rs = stmt.executeQuery();

        List<SubCategory> categories = new ArrayList<>();
        while (rs.next()) {
            Integer mainCatId = rs.getInt("catId");
            Integer subCatId = rs.getInt("subCatId");
            String title = rs.getString("title");
            String description = rs.getString("description");

            SubCategory cat = new SubCategory(mainCatId, subCatId, title).setDescription(description);
            //Haetaan vielä viimeisin tieto
            String query = "SELECT posts.timestamp, users.username, threads.title, threads.threadId FROM posts INNER JOIN threads ON posts.threadId = threads.threadId INNER JOIN users ON posts.userId = users.userId WHERE threads.subCategoryId = ? ORDER BY posts.timestamp DESC LIMIT 1";
            PreparedStatement stmt2 = connection.prepareStatement(query);
            stmt2.setInt(1, cat.getSubCategoryId());
            //Tässä haetaan viimeisimmän viestin tiedot
            ResultSet result = stmt2.executeQuery();
            if (result.next()) {
                cat.setHasMessages(true);
                cat.setLatestMessageThreadId(result.getInt("threadId"));
                cat.setLatestMessageThreadTitle(result.getString("title"));
                cat.setLatestMessageTimestamp(result.getString("timestamp"));
                cat.setLatestMessageUsername(result.getString("username"));
            }
            categories.add(cat);
            result.close();
            stmt2.close();
        }

        rs.close();
        stmt.close();
        connection.close();

        return categories;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        //To change body of generated methods, choose Tools | Templates.

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("DELETE FROM subCategories WHERE subCatId = ?;");
        stmt.setInt(1, key);
        stmt.execute();

    }

    /**
     * Lisää alakategorian
     *
     * @param c
     * @return
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
