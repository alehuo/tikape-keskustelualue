package tikape.runko.domain;

public class User {

    private int id;
    private String username;
    private String password;
    private String salt;
    private int userLevel = -1;

    /**
     * User -luokka
     * @param id Käyttäjän ID
     * @param username Käyttäjätunnus
     */
    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }

    /**
     * Palauttaa käyttäjän id:n
     *
     * @return Käyttäjätilin ID
     */
    public int getId() {
        return id;
    }

    /**
     * Palauttaa käyttäjätunnuksen
     *
     * @return Käyttäjätunnus
     */
    public String getUsername() {
        return username;
    }

    /**
     * Palauttaa salasanan tiivisteen
     *
     * @return Salasanan tiiviste
     */
    public String getPasswordHash() {
        return password;
    }

    /**
     * Palauttaa salasanan suolan
     *
     * @return Salasanan suola
     */
    public String getSalt() {
        return salt;
    }

    /**
     * Palauttaa käyttätilin tason
     *
     * @return Käyttäjätilin taso
     */
    public int getUserLevel() {
        return userLevel;
    }

    /**
     * Asettaa salasanan tiivisteen
     *
     * @param hash Salasanan tiiviste
     * @return User -ilmentymä
     */
    public User setPasswordHash(String hash) {
        password = hash;
        return this;
    }

    /**
     * Asettaa salasanan suolan
     *
     * @param salt Suola
     * @return User -ilmentymä
     */
    public User setSalt(String salt) {
        this.salt = salt;
        return this;
    }

    /**
     * Asettaa käyttäjätilin tason
     *
     * @param level Käyttäjätilin taso. 0 = Normaali, 1 = Admin
     * @return User -lmentymä
     */
    public User setUserLevel(int level) {
        userLevel = level;
        return this;
    }

    /**
     * Palauttaa käyttäjätilin ID:n ja käyttäjätunnuksen
     *
     * @return Käyttäjätilin ID ja tunnus muodossa id : käyttäjätunnus
     */
    @Override
    public String toString() {
        return id + " : " + username; //To change body of generated methods, choose Tools | Templates.
    }

}
