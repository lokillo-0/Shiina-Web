package dev.osunolimits.main;

import java.util.Map;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import dev.osunolimits.models.Action;
import dev.osunolimits.modules.ShiinaDocs;
import dev.osunolimits.modules.ThemeLoader;
import dev.osunolimits.routes.ap.get.Commands;
import dev.osunolimits.routes.ap.get.Multiaccounts;
import dev.osunolimits.routes.ap.get.Start;
import dev.osunolimits.routes.ap.get.System;
import dev.osunolimits.routes.ap.get.Themes;
import dev.osunolimits.routes.ap.get.groups.ManageGroup;
import dev.osunolimits.routes.ap.get.groups.Groups;
import dev.osunolimits.routes.ap.post.ChangeTheme;
import dev.osunolimits.routes.ap.post.ProcessManageGroup;
import dev.osunolimits.routes.api.get.GetBmThumbnail;
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
import dev.osunolimits.routes.redirects.GucchoBmRedirect;
import dev.osunolimits.routes.redirects.GucchoUserRedirect;
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

    public static String version = "1.1rc1";
    public static String dbVersion = "1.0";

    public static void main(String[] args) {
        log.info("Shiina-Web Rewrite "+version);

        Init init = new Init();
        init.initializeDataDirectory();
        init.initializeRedisConfiguration();
        init.initializeJettyLogging();
        init.initializeDatabase();
        init.initializeConnectionWatcher();
        init.initializeAutorunSQL();
        init.initializeJedis();
        customization = init.initializeCustomizations();

        ThemeLoader.loadThemes();

        webServer = new WebServer();
        init.initializeWebServer(webServer);
        init.initializeOkHttpCacheReset();

        WebServer.get("/user/:handle", new GucchoUserRedirect());
        WebServer.get("/beatmapset/:set", new GucchoBmRedirect());

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
        WebServer.get("/api/v1/thumb", new GetBmThumbnail());

        WebServer.get("/ap/multiaccs", new Multiaccounts());
        WebServer.get("/ap/start", new Start());
        WebServer.get("/ap/commands", new Commands());
        WebServer.get("/ap/themes", new Themes());
        WebServer.get("/ap/system", new System());
        WebServer.get("/ap/groups", new Groups());
        WebServer.get("/ap/groups/create", new ManageGroup(Action.CREATE));
        WebServer.get("/ap/groups/edit", new ManageGroup(Action.EDIT));
        WebServer.get("/ap/groups/delete", new ManageGroup(Action.DELETE));
        WebServer.post("/ap/groups/manage", new ProcessManageGroup());

        WebServer.post("/ap/themes/change", new ChangeTheme());

        ShiinaDocs shiinaDocs = new ShiinaDocs();
        shiinaDocs.initializeDocs();
        try {
            shiinaDocs.watchDirectory();
        } catch (Exception e) {
            log.error("Error while running docs", e);
        }

    }

    

}
