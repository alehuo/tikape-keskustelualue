package tikape.runko;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import tikape.runko.database.CategoryDao;
import tikape.runko.database.MessageDao;
import tikape.runko.database.SubCategoryDao;
import tikape.runko.database.TopicDao;
import tikape.runko.database.UserDao;
import tikape.runko.domain.Category;
import tikape.runko.domain.Message;
import tikape.runko.domain.SubCategory;
import tikape.runko.domain.Topic;
import tikape.runko.domain.User;

/**
 * Tekstikäyttöliittymä
 */
public class TextUi {

    /**
     * Skanneri
     */
    private final Scanner sc;
    /**
     * UserDao
     */
    private final UserDao userDao;
    /**
     * CategoryDao
     */
    private final CategoryDao catDao;
    /**
     * SubCategoryDao
     */
    private final SubCategoryDao subCatDao;
    /**
     * TopicDao
     */
    private final TopicDao msgThreadDao;
    /**
     * MessageDao
     */
    private final MessageDao msgDao;

    /**
     * Tekstikäyttöliittymä
     *
     * @param sc Scanner
     * @param userDao UserDao
     * @param catDao CategoryDao
     * @param subCatDao SubCategoryDao
     * @param msgThreadDao TopicDao
     * @param msgDao MessageDao
     */
    public TextUi(Scanner sc, UserDao userDao, CategoryDao catDao, SubCategoryDao subCatDao, TopicDao msgThreadDao, MessageDao msgDao) {
        this.sc = sc;
        this.userDao = userDao;
        this.catDao = catDao;
        this.subCatDao = subCatDao;
        this.msgThreadDao = msgThreadDao;
        this.msgDao = msgDao;
    }

    /**
     * Näyttää tekstikäyttöliittymän
     *
     * @throws SQLException
     */
    public void show() throws SQLException {
        OUTER:
        while (true) {
            //Tulostetaan valikko
            System.out.println("1) Listaa kategoriat ja niiden alakategoriat");
            System.out.println("2) Listaa viestiketjut alakategoriasta");
            System.out.println("3) Lisää uusi kategoria");
            System.out.println("4) Lisää uusi alakategoria");
            System.out.println("5) Kirjoita uusi viestiketju");
            System.out.println("6) Kirjoita uusi viesti viestiketjuun");
            System.out.println("7) Lisää uusi käyttäjä tietokantaan");
            System.out.println("8) Listaa käyttäjät");
            System.out.println("enter Poistu ja käynnistä Web-sovellus");
            System.out.println("");
            System.out.print("> ");
            //Poimitaan käyttäjän syöte
            String komento = sc.nextLine();
            //Tarkastetaan, löytyykö komennolle vastike
            switch (komento) {
                case "":
                    break OUTER;
                case "1":
                    System.out.println("Listataan kategoriat ja niiden alakategoriat: ");
                    List<Category> categories = catDao.findAll();
                    for (Category cat : categories) {
                        System.out.println(cat.getCategoryId() + ": " + cat);
                        if (cat.getSubCategories().size() > 0) {
                            for (SubCategory subCat : cat.getSubCategories()) {
                                System.out.println("- " + subCat);
                            }
                        } else {
                            System.out.println("(Ei alakategorioita)");
                        }

                    }
                    break;
                case "2":
                    System.out.print("Anna alakategorian ID: ");
                    Integer subCategoryId = Integer.parseInt(sc.nextLine());
                    List<Topic> msgThreads = msgThreadDao.findAllFromSubCategory(subCategoryId);
                    if (msgThreads.size() > 0) {
                        for (Topic msgThread : msgThreads) {
                            System.out.println(msgThread);
                        }
                    } else {
                        System.out.println("Ei viestiketjuja kyseisessä alakategoriassa.");
                    }

                    break;
                case "3":
                    System.out.print("Anna nimi: ");
                    String name = sc.nextLine();
                    Category c = new Category(-1, name);
                    catDao.add(c);
                    break;
                case "4":
                    System.out.print("Anna yläkategorian ID: ");
                    Integer categoryId = Integer.parseInt(sc.nextLine());
                    System.out.print("Anna nimi: ");
                    String subCategoryName = sc.nextLine();
                    System.out.print("Anna kuvaus: ");
                    String desc = sc.nextLine();
                    SubCategory subCategory = new SubCategory(categoryId, -1, subCategoryName).setDescription(desc);
                    subCatDao.add(subCategory);
                    break;
                case "5":
                    //Uusi viestiketju
                    System.out.println("Anna alakaterogian ID: ");
                    subCategoryId = Integer.parseInt(sc.nextLine());
                    System.out.println("Anna käyttäjätunnuksen ID: ");
                    int userId = Integer.parseInt(sc.nextLine());
                    System.out.println("Anna otsikko: ");
                    String title = sc.nextLine();
                    System.out.println("Kirjoita aloituspostaus: ");
                    String body = sc.nextLine();
                    String timeStamp = new java.sql.Timestamp(new java.util.Date().getTime()).toString();
                    Topic tmpThread = new Topic(subCategoryId, userId, title, timeStamp);
                    tmpThread.addMessage(new Message(-1, userId, body, timeStamp));
                    msgThreadDao.add(tmpThread);
                    break;
                case "6":
                    //Viestiketjuun vastaus
                    System.out.println("Viestiketjun ID: ");
                    int threadId = Integer.parseInt(sc.nextLine());
                    System.out.println("Käyttäjätunnuksen ID: ");
                    userId = Integer.parseInt(sc.nextLine());
                    System.out.println("Viestin sisältö: ");
                    body = sc.nextLine();
                    String ts = new java.sql.Timestamp(new java.util.Date().getTime()).toString();
                    Message m = new Message(userId, body, ts);
                    m.setThreadId(threadId);
                    msgDao.add(m);
                    break;
                case "7":
                    //Käyttäjätilin luonti
                    System.out.print("Anna käyttäjätunnus: ");
                    String userName = sc.nextLine();
                    System.out.print("Anna salasana: ");
                    String passWd = sc.nextLine();
                    System.out.print("Anna salasana uudestaan: ");
                    String passWdAgain = sc.nextLine();
                    if (passWd.equals(passWdAgain)) {
                        System.out.println("Lisätään käyttäjä..");
                        userDao.add(userName, passWd);
                    }

                    break;
                case "8":
                    //Listaa kaikki käyttäjät
                    List<User> users = userDao.findAll();
                    for (User u : users) {
                        System.out.println(u);
                    }
                    break;
                default:
                    break;
            }
            System.out.println("");
        }
    }
}
