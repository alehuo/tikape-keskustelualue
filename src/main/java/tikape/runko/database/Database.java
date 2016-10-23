package tikape.runko.database;

import java.net.URI;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Tietokantaluokka
 */
public class Database {

    private String databaseAddress;

    /**
     * Tietokantaluokka
     *
     * @param databaseAddress Tietokannan osoitemerkkijono
     * @throws ClassNotFoundException
     */
    public Database(String databaseAddress) throws ClassNotFoundException {
        this.databaseAddress = databaseAddress;
    }

    /**
     * Palauttaa tietokantayhteyden
     *
     * @return Tietokantayhteys
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        if (this.databaseAddress.contains("postgres")) {
            try {
                URI dbUri = new URI(databaseAddress);

                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

                return DriverManager.getConnection(dbUrl, username, password);
            } catch (Throwable t) {
                System.out.println("Error: " + t.getMessage());
                t.printStackTrace();
            }
        }

        return DriverManager.getConnection(databaseAddress);
    }

    /**
     * Suorittaa kyselyt sovelluksen käynnistymisen aikana
     */
    public void init() {
        List<String> lauseet = sqliteLauseet();
        if (this.databaseAddress.contains("postgres")) {
            lauseet = postgreLauseet();
        }
        // "try with resources" sulkee resurssin automaattisesti lopuksi
        try (Connection conn = getConnection()) {
            Statement st = conn.createStatement();

            // suoritetaan komennot
            for (String lause : lauseet) {
                System.out.println("Running command >> " + lause);
                st.executeUpdate(lause);
            }

        } catch (Throwable t) {
            // jos tietokantataulu on jo olemassa, ei komentoja suoriteta
            System.out.println("Error >> " + t.getMessage());
        }
    }

    /**
     * Palauttaa SQL-kyselyt SQLite -tietokantaan, jotka suoritetaan palvelimen
     * käynnistyessä
     *
     * @return Lista SQL -kyselyistä
     */
    private List<String> sqliteLauseet() {
        ArrayList<String> lista = new ArrayList<>();
        //Foreign keys päälle
        lista.add("PRAGMA foreign_keys = ON;");

        // tietokantataulujen luomiseen tarvittavat komennot suoritusjärjestyksessä
        lista.add("DROP TABLE IF EXISTS posts;");
        lista.add("DROP TABLE IF EXISTS threads;");
        lista.add("DROP TABLE IF EXISTS users;");
        lista.add("DROP TABLE IF EXISTS subCategories;");
        lista.add("DROP TABLE IF EXISTS categories;");

        //Kategoriat -taulu
        lista.add("CREATE TABLE categories (categoryId integer PRIMARY KEY, title varchar(60));");
        //Alakategoriat -taulu
        lista.add("CREATE TABLE subCategories (subCatId integer PRIMARY KEY, catId integer , title varchar(30), description varchar(100), FOREIGN KEY(catId) REFERENCES categories(categoryId));");
        //Käyttäjät
        lista.add("CREATE TABLE users (userId integer PRIMARY KEY, username varchar(16), password varchar(1024), salt varchar(1024), userLevel integer);");
        //Viestiketjut
        lista.add("CREATE TABLE threads (threadId integer PRIMARY KEY, subCategoryId integer , userId integer, title varchar(80), creationDate varchar(255), FOREIGN KEY(subCategoryId) REFERENCES subCategories(subCatId), FOREIGN KEY(userId) REFERENCES users(userId));");
        //Viestiketjun postaukset
        lista.add("CREATE TABLE posts (postId integer PRIMARY KEY, threadId integer , userId integer , timestamp varchar(255), body varchar(4096), FOREIGN KEY(threadId) REFERENCES threads(threadId), FOREIGN KEY(userId) REFERENCES users(userId));");

        //Kategoriat
        lista.add("INSERT INTO categories (categoryId, title) VALUES (1,'Testikategoria 1');");
        lista.add("INSERT INTO categories (categoryId, title) VALUES (2,'Testikategoria 2');");
        lista.add("INSERT INTO subCategories (subCatId, catId, title, description) VALUES (1,1,'Testialakategoria 1','Hello World');");
        lista.add("INSERT INTO subCategories (subCatId, catId, title, description) VALUES (2,1,'Testialakategoria 2','Hello World');");
        lista.add("INSERT INTO subCategories (subCatId, catId, title, description) VALUES (3,1,'Testialakategoria 3','Hello World');");
        lista.add("INSERT INTO subCategories (subCatId, catId, title, description) VALUES (4,2,'Testialakategoria 4','Hello World');");
        lista.add("INSERT INTO subCategories (subCatId, catId, title, description) VALUES (5,2,'Testialakategoria 5','Hello World');");
        //Admin -tunnus (admin::admin)
        lista.add("INSERT INTO users (userId, username, password, salt, userLevel) VALUES (1,'admin','RLxcC7GMXsb4lymp+tV/aMAJfCVc7N9+Sj1c2mZryT0=','5Wqs2e/dGhg=',1)");
        //User -tunnus (user:user)
        lista.add("INSERT INTO users (userId, username, password, salt, userLevel) VALUES (2,'user','QDEj1qay1U05Q7UBRsznCfTG2nR40CzNrKflcs6skWg=','BFxg7e3SPJE=',0)");
        //Jne..
        lista.add("INSERT INTO threads (threadId, subCategoryId, userId, title, creationDate) VALUES (1,1,1,'Testipostaus','2016-10-17 20:23')");
        lista.add("INSERT INTO posts (postId, threadId, userId, timestamp, body) VALUES (1,1,1,'2016-10-17 20:23','[b]Hello world![/b][i]Kursivoitu[/i]')");
        return lista;
    }

    /**
     * Palauttaa SQL-kyselyt PostgreSQL -tietokantaan, jotka suoritetaan
     * palvelimen käynnistyessä
     *
     * @return Lista SQL -kyselyistä
     */
    private List<String> postgreLauseet() {
        ArrayList<String> lista = new ArrayList<>();

        // tietokantataulujen luomiseen tarvittavat komennot suoritusjärjestyksessä
        lista.add("DROP TABLE IF EXISTS posts;");
        lista.add("DROP TABLE IF EXISTS threads;");
        lista.add("DROP TABLE IF EXISTS users;");
        lista.add("DROP TABLE IF EXISTS subCategories;");
        lista.add("DROP TABLE IF EXISTS categories;");

        //Kategoriat -taulu
        lista.add("CREATE TABLE categories (categoryId SERIAL PRIMARY KEY, title varchar(60));");
        //Alakategoriat -taulu
        lista.add("CREATE TABLE subCategories (subCatId SERIAL PRIMARY KEY, catId integer , title varchar(30), description varchar(100), FOREIGN KEY(catId) REFERENCES categories(categoryId));");
        //Käyttäjät
        lista.add("CREATE TABLE users (userId SERIAL PRIMARY KEY, username varchar(16), password varchar(1024), salt varchar(1024), userLevel SERIAL);");
        //Viestiketjut
        lista.add("CREATE TABLE threads (threadId SERIAL PRIMARY KEY, subCategoryId integer , userId integer, title varchar(80), creationDate varchar(255), FOREIGN KEY(subCategoryId) REFERENCES subCategories(subCatId), FOREIGN KEY(userId) REFERENCES users(userId));");
        //Viestiketjun postaukset
        lista.add("CREATE TABLE posts (postId SERIAL PRIMARY KEY, threadId integer , userId integer , timestamp varchar(255), body varchar(4096), FOREIGN KEY(threadId) REFERENCES threads(threadId), FOREIGN KEY(userId) REFERENCES users(userId));");

        //Kategoriat
        lista.add("INSERT INTO categories (title) VALUES ('Testikategoria 1');");
        lista.add("INSERT INTO categories (title) VALUES ('Testikategoria 2');");
        lista.add("INSERT INTO subCategories (catId, title, description) VALUES (1,'Testialakategoria 1','Hello World');");
        lista.add("INSERT INTO subCategories (catId, title, description) VALUES (1,'Testialakategoria 2','Hello World');");
        lista.add("INSERT INTO subCategories (catId, title, description) VALUES (1,'Testialakategoria 3','Hello World');");
        lista.add("INSERT INTO subCategories (catId, title, description) VALUES (2,'Testialakategoria 4','Hello World');");
        lista.add("INSERT INTO subCategories (catId, title, description) VALUES (2,'Testialakategoria 5','Hello World');");
        //Admin -tunnus (admin::admin)
        lista.add("INSERT INTO users (username, password, salt, userLevel) VALUES ('admin','RLxcC7GMXsb4lymp+tV/aMAJfCVc7N9+Sj1c2mZryT0=','5Wqs2e/dGhg=',1)");
        //User -tunnus (user:user)
        lista.add("INSERT INTO users (username, password, salt, userLevel) VALUES ('user','QDEj1qay1U05Q7UBRsznCfTG2nR40CzNrKflcs6skWg=','BFxg7e3SPJE=',0)");
        //Jne..
        lista.add("INSERT INTO threads (subCategoryId, userId, title, creationDate) VALUES (1,1,'Testipostaus','2016-10-17 20:23')");
        lista.add("INSERT INTO posts (threadId, userId, timestamp, body) VALUES (1,1,'2016-10-17 20:23','[b]Hello world![/b][i]Kursivoitu[/i]')");
        return lista;
    }
}
