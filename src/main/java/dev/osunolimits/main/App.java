package dev.osunolimits.main;

import java.util.Map;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import dev.osunolimits.modules.ShiinaDocs;
import dev.osunolimits.routes.api.get.GetFirstPlaces;
import dev.osunolimits.routes.api.get.GetPlayerScores;
import dev.osunolimits.routes.get.Beatmap;
import dev.osunolimits.routes.get.Beatmaps;
import dev.osunolimits.routes.get.Clan;
import dev.osunolimits.routes.get.Clans;
import dev.osunolimits.routes.get.Home;
import dev.osunolimits.routes.get.Leaderboard;
import dev.osunolimits.routes.get.Score;
import dev.osunolimits.routes.get.User;
import dev.osunolimits.routes.get.errors.NotFound;
import dev.osunolimits.routes.get.simple.Login;
import dev.osunolimits.routes.get.simple.Register;
import dev.osunolimits.routes.get.simple.Settings;
import dev.osunolimits.routes.post.HandleAvatarChange;
import dev.osunolimits.routes.post.HandleLogin;
import dev.osunolimits.routes.post.HandleLogout;
import dev.osunolimits.routes.post.HandleRegister;
import io.github.cdimascio.dotenv.Dotenv;
import redis.clients.jedis.JedisPooled;

/**
 * Shiina a attemp to make a modular bancho.py full stack application
 * By Marc Andre Herpers
 */
public class App {
    public static final Logger log = (Logger) LoggerFactory.getLogger(App.class);
    public static final Dotenv loggerEnv = Dotenv.configure().directory(".config/").filename("logger.env").load();
    public static final Dotenv env = Dotenv.configure().directory(".config/").load();
    public static Map<String, Object> customization;
    public static JedisPooled jedisPool;
    public static WebServer webServer;

    public static String version = "1.0sh";

    public static void main(String[] args) {
        log.info("Shiina-Web Rewrite "+version);

        Init init = new Init();
        init.initializeRedisConfiguration();
        init.initializeJettyLogging();
        init.initializeDatabase();
        init.initializeJedis();
        customization = init.initializeCustomizations();
        webServer = new WebServer();
        init.initializeWebServer(webServer);
        init.initializeOkHttpCacheReset();
        WebServer.get("/", new Home());
        WebServer.get("/beatmaps", new Beatmaps());
        WebServer.get("/leaderboard", new Leaderboard());
        WebServer.get("/clans", new Clans());
        WebServer.get("/clan/:id", new Clan());
        WebServer.get("/scores/:id", new Score());
        WebServer.get("/b/:id", new Beatmap());
        WebServer.get("/u/:id", new User());
        WebServer.get("/settings", new Settings());
        WebServer.post("/settings/avatar", new HandleAvatarChange());
        WebServer.get("/login", new Login());
        WebServer.get("/register", new Register());
        WebServer.post("/register", new HandleRegister());
        WebServer.post("/login", new HandleLogin());
        WebServer.post("/logout", new HandleLogout());
        WebServer.notFound(new NotFound());

        WebServer.get("/api/v1/get_first_places", new GetFirstPlaces());
        WebServer.get("/api/v1/get_player_scores", new GetPlayerScores());

        ShiinaDocs shiinaDocs = new ShiinaDocs();
        shiinaDocs.initializeDocs();
        try {
            shiinaDocs.watchDirectory();
        } catch (Exception e) {
            log.error("Error while running docs", e);
        }

    }

    

}
