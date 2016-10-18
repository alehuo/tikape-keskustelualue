package tikape.runko;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import static spark.Spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import tikape.runko.database.CategoryDao;
import tikape.runko.database.Database;
import tikape.runko.database.MessageDao;
import tikape.runko.database.MessageThreadDao;
import tikape.runko.database.SubCategoryDao;
import tikape.runko.database.UserDao;
import tikape.runko.domain.Category;
import tikape.runko.domain.Message;
import tikape.runko.domain.MessageThread;
import tikape.runko.domain.SubCategory;
import tikape.runko.domain.User;

public class Main {

    public static void main(String[] args) throws Exception {
        //Tietokannan alustus
        //Käytetään oletuksena paikallista sqlite-tietokantaa
        String jdbcOsoite = "jdbc:sqlite:keskustelualue.db";
        //Jos heroku antaa käyttöömme tietokantaosoitteen, otetaan se käyttöön
        if (System.getenv("DATABASE_URL") != null) {
            jdbcOsoite = System.getenv("DATABASE_URL");
        }

        Spark.staticFileLocation("/img");

        Database database = new Database(jdbcOsoite);
        database.init();

        UserDao userDao = new UserDao(database);
        CategoryDao catDao = new CategoryDao(database);
        SubCategoryDao subCatDao = new SubCategoryDao(database);
        MessageThreadDao msgThreadDao = new MessageThreadDao(database);
        MessageDao msgDao = new MessageDao(database);
        Scanner sc = new Scanner(System.in);

        //Tekstikäyttöliittymän alustus
        TextUi textUi = new TextUi(sc, userDao, catDao, subCatDao, msgThreadDao);
        //Näytä tekstikäyttöliittymä
//        textUi.show();

        //Oletusportti
        int appPort = 4567;
        if (System.getenv("PORT") != null) {
            appPort = Integer.parseInt(System.getenv("PORT"));
        }

        //Asetetaan oletusportti
        port(appPort);

        //Etusivu
        get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            //Tähän näkymä, jolla näytetään etusivu (kategoriat ja alakategoriat)
            //Haetaan kategoriat
            List<Category> categories = catDao.findAll();
            map.put("kategoriat", categories);
            map.put("user", (User) req.session().attribute("user"));

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
        //Näytä viestiketju
        get("/thread/:threadId", (req, res) -> {
            HashMap map = new HashMap<>();
            int id = Integer.parseInt(req.params("threadId"));
            map.put("messageThread", msgThreadDao.findOne(id));
            map.put("viestit", msgDao.findAllFromTopic(id));
            map.put("user", (User) req.session().attribute("user"));
            //Tähän näkymä, jossa näytetään viestiketju
            return new ModelAndView(map, "messages");
        }, new ThymeleafTemplateEngine());
        //Lähetä viestiketjuun uusi vastaus
        post("/thread/:threadId", (req, res) -> {
            int id = Integer.parseInt(req.params("threadId"));
            User u = req.session().attribute("user");
            String ts = new java.sql.Timestamp(new java.util.Date().getTime()).toString();
            String body = req.queryParams("message");
            Message m = new Message(u.getId(), body, ts);
            m.setThreadId(id);
            msgDao.add(m);
            //Käsitellään tässä POST-pyynnön data ja lisätään tietokantaan
            res.redirect("/thread/" + id);
            return "Vastaus viestiketjuun, jolla id: " + id;
        });
        //Näytä alakategorian viestit:
        get("/subcategory/:subCategoryId", (req, res) -> {
            HashMap map = new HashMap<>();
            int id = Integer.parseInt(req.params("subCategoryId"));
            map.put("subcategoryId", id);
            map.put("subcategory", subCatDao.findOne(id));
            map.put("viestiketjut", msgThreadDao.findAllFromSubCategory(id));
            map.put("user", (User) req.session().attribute("user"));
            //Tähän näkymä, jossa näytetään alakategorian viestit
            return new ModelAndView(map, "topics");
        }, new ThymeleafTemplateEngine());
        //Uuden viestiketjun lähettäminen:
        post("/subcategory/:subCategoryId", (req, res) -> {
            int id = Integer.parseInt(req.params("subCategoryId"));
            //Käsitellään tässä POST-pyynnön data ja lisätään tietokantaan
            return "Tällä käsitellään viestiketjun data alakategoriaan " + id + ".";
        });
        //Uuden viestiketjun luominen:
        get("/new/:subCategoryId", (req, res) -> {
            int id = Integer.parseInt(req.params("subCategoryId"));
            HashMap map = new HashMap<>();
            map.put("user", (User) req.session().attribute("user"));
            //Näytetään tässä lomake käyttäjälle
            return new ModelAndView(map, "new");
        }, new ThymeleafTemplateEngine());

        //Uuden viestiketjun luominen:
        post("/new/:subCategoryId", (req, res) -> {
            int id = Integer.parseInt(req.params("subCategoryId"));
            HashMap map = new HashMap<>();
            User u = req.session().attribute("user");
            String timeStamp = new java.sql.Timestamp(new java.util.Date().getTime()).toString();
            MessageThread tmpThread = new MessageThread(id, u.getId(), req.queryParams("title"), timeStamp);
            tmpThread.addMessage(new Message(-1, u.getId(), req.queryParams("body"), timeStamp
            ));
            msgThreadDao.add(tmpThread);
            res.redirect("/subcategory/" + id);
            return "";
        });

        //Kirjaudu sisään
        post("/login", (req, res) -> {
            //Käsitellään tässä POST-pyynnön data
            //Käyttäjätunnus
            String username = req.queryParams("username").trim();
            //Salasana
            String password = req.queryParams("password");
            User u = userDao.findByUsername(username);
            //Jos käyttäjä löytyy tietokannasta
            if (u != null) {
                if (Auth.passwordMatches(password, u.getPasswordHash(), u.getSalt())) {
                    //Kirjaudu sisään. Aloitetaan uusi istunto
                    req.session(true).attribute("user", u);
                    res.redirect("/");
                    return "Kirjauduttu sisään.";
                } else {
                    //Väärä salasana!
                    res.redirect("/login?error");
                    return "Käyttäjätunnus tai salasana väärä.";
                }
            } else {
                //Käyttäjätunnusta ei ole olemassa!

                res.redirect("/login?error");
                return "Käyttäjätunnus tai salasana väärä.";
            }

        });
        //Uuden käyttäjän lisääminen
        post("/register", (req, res) -> {
            HashMap map = new HashMap<>();
            //Tähän uuden käyttäjän lisääminen
            //Käyttäjätunnus
            String username = req.queryParams("username").trim();
            //Salasana
            String password = req.queryParams("password");
            User userList = userDao.findByUsername(username);
            //Jos käyttäjänimellä ei löydy tietokannasta käyttäjää, lisätään se tietokantaan
            if (userList == null) {
                System.out.println("Uusi käyttäjä lisätty: " + username);
                userDao.add(username, password);
            }
            //Ohjaus etusivulle
            res.redirect("/");
            return "";
        });
        //Kirjautumissivu
        get("/login", (req, res) -> {
            HashMap map = new HashMap<>();
            if (req.queryParams("error") != null) {
                map.put("invalidCredentials", true);
            }
            return new ModelAndView(map, "login");
        }, new ThymeleafTemplateEngine());
        //Rekisteröitymissivu
        get("/register", (req, res) -> {
            HashMap map = new HashMap<>();
            return new ModelAndView(map, "register");
        }, new ThymeleafTemplateEngine());
        //Uloskirjautuminen
        get("/logout", (req, res) -> {
            //Hylätään istunto
            Session sess = req.session();
            sess.invalidate();
            res.redirect("/");
            return "";
        });

        get("/category/delete/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            List<SubCategory> subCategories = subCatDao.findAllByCategoryId(id);
            for (SubCategory c : subCategories) {
                msgDao.deleteAllFromSubCategory(c.getSubCategoryId());
                msgThreadDao.deleteAllFromSubCategory(c.getSubCategoryId());
                subCatDao.delete(c.getSubCategoryId());
            }
            catDao.delete(id);
            res.redirect("/");
            return "";
        });
        get("/subcategory/delete/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            msgDao.deleteAllFromSubCategory(id);
            msgThreadDao.deleteAllFromSubCategory(id);
            subCatDao.delete(id);
            res.redirect("/");
            return "";
        });
        get("/subcategory/new/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            return new ModelAndView(map, "addsubcategory");
        }, new ThymeleafTemplateEngine());
        post("/subcategory/new/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            String name = req.queryParams("subcategoryname");
            String desc = req.queryParams("subcategorydesc");
            SubCategory c = new SubCategory(id, name).setDescription(desc);
            subCatDao.add(c);
            res.redirect("/");
            return "";
        });
        get("/category/new", (req, res) -> {
            HashMap map = new HashMap<>();
            return new ModelAndView(map, "addcategory");
        }, new ThymeleafTemplateEngine());
        post("/category/new", (req, res) -> {
            String name = req.queryParams("categoryname");
            catDao.add(new Category(name));
            res.redirect("/");
            return "";
        });
    }
}
