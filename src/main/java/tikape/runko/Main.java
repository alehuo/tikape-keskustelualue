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
import tikape.runko.database.SubCategoryDao;
import tikape.runko.database.TopicDao;
import tikape.runko.database.UserDao;
import tikape.runko.domain.Category;
import tikape.runko.domain.Message;
import tikape.runko.domain.MessageThread;
import tikape.runko.domain.SubCategory;
import tikape.runko.domain.User;

/**
 * Tietokantasovellus
 */
public class Main {

    /**
     * Tietokantasovellus
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        //Tietokannan alustus
        //Käytetään oletuksena paikallista sqlite-tietokantaa
        String jdbcOsoite = "jdbc:sqlite:keskustelualue.db";
        //Jos heroku antaa käyttöömme tietokantaosoitteen, otetaan se käyttöön
        if (System.getenv("DATABASE_URL") != null) {
            jdbcOsoite = System.getenv("DATABASE_URL");
        }

        Spark.staticFileLocation("/static");

        Database database = new Database(jdbcOsoite);
        database.init();

        UserDao userDao = new UserDao(database);
        CategoryDao catDao = new CategoryDao(database);
        SubCategoryDao subCatDao = new SubCategoryDao(database);
        TopicDao topicDao = new TopicDao(database);
        MessageDao msgDao = new MessageDao(database);
        Scanner sc = new Scanner(System.in);

        //Tekstikäyttöliittymän alustus
        TextUi textUi = new TextUi(sc, userDao, catDao, subCatDao, topicDao, msgDao);
        //Näytä tekstikäyttöliittymä
        //textUi.show();

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
            map.put("user", req.session().attribute("user"));

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
        //Uudelleenohjaa pois vääristä sivuista
        get("/subcategory", (req, res) -> {
            res.redirect("/");
            return "";
        });
        get("/thread", (req, res) -> {
            res.redirect("/");
            return "";
        });
        //Näytä viestiketju
        get("/thread/:threadId", (req, res) -> {
            HashMap map = new HashMap<>();
            int id;
            try {
                id = Integer.parseInt(req.params("threadId"));
            } catch (NumberFormatException e) {
                id = -1;
            }
            map.put("messageThread", topicDao.findOne(id));
            map.put("viestit", msgDao.findAllFromTopic(id));
            map.put("user", req.session().attribute("user"));
            //Tähän näkymä, jossa näytetään viestiketju
            return new ModelAndView(map, "messages");
        }, new ThymeleafTemplateEngine());
        //Lähetä viestiketjuun uusi vastaus
        post("/thread/:threadId", (req, res) -> {
            int id;
            try {
                id = Integer.parseInt(req.params("threadId"));
            } catch (NumberFormatException e) {
                id = -1;
            }
            User u = req.session().attribute("user");
            if (Auth.isLoggedIn(u)) {
                String ts = new java.sql.Timestamp(new java.util.Date().getTime()).toString();
                String body = req.queryParams("message");
                Message m = new Message(u.getId(), body, ts);
                m.setThreadId(id);
                msgDao.add(m);
                //Käsitellään tässä POST-pyynnön data ja lisätään tietokantaan
                res.redirect("/thread/" + id);
                return "Vastaus viestiketjuun, jolla id: " + id;
            } else {
                return "Sinulla ei ole oikeuksia suorittaa kyseistä toimintoa.";
            }

        });
        //Näytä alakategorian viestit:
        get("/subcategory/:subCategoryId", (req, res) -> {
            HashMap map = new HashMap<>();
            int id;
            try {
                id = Integer.parseInt(req.params("subCategoryId"));
            } catch (NumberFormatException e) {
                id = -1;
            }

            map.put("subcategoryId", id);
            map.put("subcategory", subCatDao.findOne(id));
            map.put("viestiketjut", topicDao.findAllFromSubCategory(id));
            map.put("user", req.session().attribute("user"));
            //Tähän näkymä, jossa näytetään alakategorian viestit
            return new ModelAndView(map, "topics");
        }, new ThymeleafTemplateEngine());
        //Uuden viestiketjun lähettäminen:
//        post("/subcategory/:subCategoryId", (req, res) -> {
//            int id = Integer.parseInt(req.params("subCategoryId"));
//            //Käsitellään tässä POST-pyynnön data ja lisätään tietokantaan
//            return "Tällä käsitellään viestiketjun data alakategoriaan " + id + ".";
//        });
        //Uuden viestiketjun luominen:
        get("/thread/new/:subCategoryId", (req, res) -> {
            HashMap map = new HashMap<>();
            int id;
            try {
                id = Integer.parseInt(req.params("subCategoryId"));
            } catch (NumberFormatException e) {
                return new ModelAndView(map, "unauthorized");
            }
            User u = req.session().attribute("user");
            if (!Auth.isLoggedIn(u)) {
                return new ModelAndView(map, "unauthorized");
            }
            map.put("user", u);
            //Näytetään tässä lomake käyttäjälle
            return new ModelAndView(map, "new");
        }, new ThymeleafTemplateEngine());

        //Uuden viestiketjun luominen:
        post("/thread/new/:subCategoryId", (req, res) -> {
            int id;
            try {
                id = Integer.parseInt(req.params("subCategoryId"));
            } catch (NumberFormatException e) {
                id = -1;
            }
            HashMap map = new HashMap<>();
            User u = req.session().attribute("user");
            if (Auth.isLoggedIn(u)) {
                String timeStamp = new java.sql.Timestamp(new java.util.Date().getTime()).toString();
                MessageThread tmpThread = new MessageThread(id, u.getId(), req.queryParams("title"), timeStamp);
                tmpThread.addMessage(new Message(-1, u.getId(), req.queryParams("body"), timeStamp));
                topicDao.add(tmpThread);
                res.redirect("/subcategory/" + id);
                return "";
            } else {
                return "Sinulla ei ole oikeuksia suorittaa kyseistä toimintoa.";
            }

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
                    //Jos JavaScript ei ole päällä, niin ohjataan sivu automaattisesti
                    if (req.queryParams("jslogin") == null) {
                        res.redirect("/");
                    }

                    return "ok";
                } else {
                    //Väärä salasana!
                    //Jos JavaScript ei ole päällä, niin ohjataan sivu automaattisesti
                    if (req.queryParams("jslogin") == null) {
                        res.redirect("/login?error");
                    }
                    return "error";
                }
            } else {
                //Käyttäjätunnusta ei ole olemassa!

                //Jos JavaScript ei ole päällä, niin ohjataan sivu automaattisesti
                if (req.queryParams("jslogin") == null) {
                    res.redirect("/login?error");
                }
                return "error";
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
            if (username != null && password != null && username.length() > 3 && password.length() > 3) {

                User userList = userDao.findByUsername(username);
                //Jos käyttäjänimellä ei löydy tietokannasta käyttäjää, lisätään se tietokantaan
                if (userList == null) {
                    userDao.add(username, password);
                    //Ohjaus etusivulle
                    res.redirect("/");
                    return "Rekisteröinti onnistui";
                } else {
                    res.redirect("/register?error");
                    return "Käyttäjätunnus jo käytössä";
                }

            } else {
                res.redirect("/register?error2");
                return "Käyttäjätunnus tai salasana liian lyhyt";
            }
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
            if (req.queryParams("error") != null) {
                map.put("invalidCredentials", true);
            } else if (req.queryParams("error2") != null) {
                map.put("invalidCredentials2", true);
            }
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

        //Kategorian poisto
        get("/category/delete/:id", (req, res) -> {
            User u = req.session().attribute("user");
            if (Auth.isAdmin(u)) {
                try {
                    int id = Integer.parseInt(req.params("subCategoryId"));
                    List<SubCategory> subCategories = subCatDao.findAllByCategoryId(id);
                    for (SubCategory c : subCategories) {
                        msgDao.deleteAllFromSubCategory(c.getSubCategoryId());
                        topicDao.deleteAllFromSubCategory(c.getSubCategoryId());
                        subCatDao.delete(c.getSubCategoryId());
                    }
                    catDao.delete(id);
                    res.redirect("/");
                    return "";
                } catch (NumberFormatException e) {
                    return "Sinulla ei ole oikeuksia suorittaa kyseistä toimintoa.";
                }

            } else {
                return "Sinulla ei ole oikeuksia suorittaa kyseistä toimintoa.";
            }

        });
        //Alakategorian poisto
        get("/subcategory/delete/:id", (req, res) -> {
            User u = req.session().attribute("user");
            if (Auth.isAdmin(u)) {
                try {
                    int id = Integer.parseInt(req.params(":id"));
                    msgDao.deleteAllFromSubCategory(id);
                    topicDao.deleteAllFromSubCategory(id);
                    subCatDao.delete(id);
                    res.redirect("/");
                    return "";
                } catch (NumberFormatException e) {
                    return "Sinulla ei ole oikeuksia suorittaa kyseistä toimintoa.";
                }
            } else {
                return "Sinulla ei ole oikeuksia suorittaa kyseistä toimintoa.";
            }

        });
        //Uuden alakategorian lisäys
        get("/subcategory/new/:id", (req, res) -> {
            HashMap map = new HashMap<>();
            User u = req.session().attribute("user");
            if (!Auth.isAdmin(u)) {
                return new ModelAndView(map, "unauthorized");
            }
            return new ModelAndView(map, "addsubcategory");
        }, new ThymeleafTemplateEngine());
        //Uuden alakategorian lisäys
        post("/subcategory/new/:id", (req, res) -> {
            User u = req.session().attribute("user");
            if (Auth.isAdmin(u)) {
                try {
                    int id = Integer.parseInt(req.params(":id"));
                    String name = req.queryParams("subcategoryname");
                    String desc = req.queryParams("subcategorydesc");
                    SubCategory c = new SubCategory(id, name).setDescription(desc);
                    subCatDao.add(c);
                    res.redirect("/");
                    return "";
                } catch (NumberFormatException e) {
                    return "Sinulla ei ole oikeuksia suorittaa kyseistä toimintoa.";
                }
            } else {
                return "Sinulla ei ole oikeuksia suorittaa kyseistä toimintoa.";
            }

        });
        //Uuden kategorian lisäys
        get("/category/new", (req, res) -> {
            HashMap map = new HashMap<>();
            User u = req.session().attribute("user");
            if (!Auth.isAdmin(u)) {
                return new ModelAndView(map, "unauthorized");
            }
            return new ModelAndView(map, "addcategory");
        }, new ThymeleafTemplateEngine());
        //Uuden kategorian lisäys
        post("/category/new", (req, res) -> {
            User u = req.session().attribute("user");
            if (Auth.isAdmin(u)) {
                String name = req.queryParams("categoryname");
                catDao.add(new Category(name));
                res.redirect("/");
                return "";
            } else {
                return "Sinulla ei ole oikeuksia suorittaa kyseistä toimintoa.";
            }

        });
    }
}
