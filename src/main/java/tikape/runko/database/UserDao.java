package tikape.runko.database;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64.Encoder;
import java.util.List;
import tikape.runko.Auth;
import tikape.runko.domain.User;

/**
 * UserDao
 */
public class UserDao implements Dao<User, Integer> {

    private final Database database;

    /**
     * UserDao
     *
     * @param database Tietokantaolio
     */
    public UserDao(Database database) {
        this.database = database;
    }

    /**
     * Palauttaa yhden käyttäjän ID:n perusteella
     *
     * @param key Käyttäjätilin ID
     * @return Käyttäjä
     * @throws SQLException
     */
    @Override
    public User findOne(Integer key) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE userId = ?");
        stmt.setObject(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("userId");
        String uname = rs.getString("username");
        String passwordHash = rs.getString("password");
        String salt = rs.getString("salt");
        Integer userLevel = rs.getInt("userLevel");
        User u = new User(id, uname).setUserLevel(userLevel).setPasswordHash(passwordHash).setSalt(salt);

        rs.close();
        stmt.close();
        connection.close();

        return u;
    }

    /**
     * Palauttaa käyttäjätilin nimen perusteella
     *
     * @param username Käyttäjätunnus
     * @return Käyttäjä
     * @throws SQLException
     */
    public User findByUsername(String username) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users WHERE lower(username) = ?");
        stmt.setObject(1, username.trim().toLowerCase());

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        Integer id = rs.getInt("userId");
        String uname = rs.getString("username");
        String passwordHash = rs.getString("password");
        String salt = rs.getString("salt");
        Integer userLevel = rs.getInt("userLevel");
        User u = new User(id, uname).setUserLevel(userLevel).setPasswordHash(passwordHash).setSalt(salt);

        rs.close();
        stmt.close();
        connection.close();

        return u;
    }

    /**
     * Palauttaa kaikki käyttäjät tietokannasta
     *
     * @return Käyttäjä -lista
     * @throws SQLException
     */
    @Override
    public List<User> findAll() throws SQLException {

        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM users");

        ResultSet rs = stmt.executeQuery();
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            Integer id = rs.getInt("userId");
            String uname = rs.getString("username");
            String passwordHash = rs.getString("password");
            String salt = rs.getString("salt");
            Integer userLevel = rs.getInt("userLevel");
            User u = new User(id, uname);
            u.setUserLevel(userLevel);
            u.setPasswordHash(passwordHash);
            u.setSalt(salt);
            users.add(u);
        }

        rs.close();
        stmt.close();
        connection.close();

        return users;
    }

    /**
     * Poistaa käyttäjätilin (EI TOTEUTETTU)
     *
     * @param key Käyttäjätilin ID
     * @throws SQLException
     */
    @Override
    public void delete(Integer key) throws SQLException {
        // ei toteutettu
    }

    /**
     * Lisää käyttäjätilin tietokantaan
     *
     * @param username Käyttäjätunnus
     * @param password Salasana
     * @throws SQLException
     */
    public void add(String username, String password) throws SQLException {
        username = username.trim();
        //Luodaan salasanalle "suola"
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        try {
            //Yhdistetään suola ja salasanan bittijono
            byte[] passwd = password.getBytes("UTF-8");
            byte[] saltedPassword = Auth.combineTwoByteArrays(salt, passwd);
            //Käytetään SHA-256 salausta:
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedPassword = digest.digest(saltedPassword);
            //Lopuksi käytetään Base64-enkoodausta jotta salasana ja suola voidaan tallentaa tietokantaan.
            Encoder en = java.util.Base64.getEncoder();
            //Base64-enkoodatut suola ja salasana
            String saltBase64 = en.encodeToString(salt);
            String hashedPasswordBase64 = en.encodeToString(hashedPassword);

            //Kysely, joka aiotaan suorittaa
            String query = "INSERT INTO users (username, password, salt, userLevel) VALUES(?, ?, ?, 0)";
            Connection con = database.getConnection();
            PreparedStatement stmt = con.prepareStatement(query);
            //Käyttäjätunnus
            stmt.setString(1, username);
            //Salasana
            stmt.setString(2, hashedPasswordBase64);
            //Suola
            stmt.setString(3, saltBase64);
            //Suorita kysely
            stmt.execute();
            stmt.close();
            con.close();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ex) {
        }

    }

}
