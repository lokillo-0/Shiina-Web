package dev.osunolimits.main;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import dev.osunolimits.modules.ShiinaDocs;
import dev.osunolimits.routes.Beatmaps;
import dev.osunolimits.routes.Clans;
import dev.osunolimits.routes.Home;
import dev.osunolimits.routes.Leaderboard;
import dev.osunolimits.routes.Login;
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

        webServer = new WebServer(log);
        init.initializeWebServer(webServer);

        WebServer.get("/", new Home());
        WebServer.get("/beatmaps", new Beatmaps());
        WebServer.get("/leaderboard", new Leaderboard());
        WebServer.get("/clans", new Clans());
        WebServer.get("/login", new Login());

        ShiinaDocs shiinaDocs = new ShiinaDocs();
        shiinaDocs.initializeDocs();
        try {
            shiinaDocs.watchDirectory();
        } catch (Exception e) {
            log.error("Error while running docs", e);
        }

    }

    

}
