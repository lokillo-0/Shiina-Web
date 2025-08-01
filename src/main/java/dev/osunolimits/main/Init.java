package dev.osunolimits.main;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import com.google.gson.Gson;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import dev.osunolimits.common.Database;
import dev.osunolimits.common.Database.ServerTimezone;
import dev.osunolimits.common.MySQL;
import dev.osunolimits.models.DbVersion;
import dev.osunolimits.utils.SQLFileLoader;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPooled;

public class Init {

    public final Logger log = (Logger) LoggerFactory.getLogger("Igniter");

    public void initializeWebServer(WebServer webServer) {

        try {
            webServer.setThreadPool(Integer.parseInt(App.env.get("MIN_THREADS")),
                    Integer.parseInt(App.env.get("MAX_THREADS")), Integer.parseInt(App.env.get("TIMEOUT_MS")));
        } catch (Exception e) {
            log.warn("Failed to set WebServer Thread Configuration");
            System.exit(1);
        }

        webServer.createDefaultDirectories();
        webServer.setDefaultDirectories();
        try {
            webServer.setTemplateUpdateDelay(Integer.parseInt(App.env.get("TEMPLATE_UPDATE_DELAY")));
        } catch (Exception e) {
            log.warn("Failed to set Template Update Delay (Caching)");
            System.exit(1);
        }

        try {
            if (App.devMode) {
                webServer.ignite(App.env.get("HOST"), Integer.parseInt(App.env.get("DEV_PORT")), 3010);
            } else {
                webServer.ignite(App.env.get("HOST"), Integer.parseInt(App.env.get("PORT")), 3000);
            }

        } catch (Exception e) {
            log.error("Failed to ignite WebServer", e);
            System.exit(1);
        }
    }

    public void initializeOkHttpCacheReset() {
        try {
            Files.walk(Paths.get(".cache/"))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {

        }
    }

    public Map<String, Object> initializeCustomizations() {
        Yaml yaml = new Yaml();
        try {
            String yamlContent = Files.readString(Paths.get(".config/customization.yml"));
            Map<String, Object> obj = yaml.load(yamlContent);
            log.info("Loaded customization.yml");
            return obj;
        } catch (IOException e) {
            log.error("Failed to load customization.yml", e);
            System.exit(1);
            return null;
        }
    }


    public void initializeJedis() {
        try {
            HostAndPort hostAndPort = new HostAndPort(App.env.get("REDISHOST"),
                    Integer.parseInt(App.env.get("REDISPORT")));

            DefaultJedisClientConfig clientConfig = DefaultJedisClientConfig.builder()
                    .connectionTimeoutMillis(Integer.parseInt(App.env.get("REDISTIMEOUT")))
                    .database(Integer.parseInt(App.env.get("REDISDB")))
                    .password(App.env.get("REDISPASS"))
                    .user(App.env.get("REDISUSER"))
                    .build();

            App.jedisPool = new JedisPooled(hostAndPort, clientConfig);

            log.info("Connected to Redis: " + App.jedisPool.ping());

        } catch (Exception e) {
            log.error("Failed to configure Jedis", e);
            System.exit(1);
        }
    }

    public void initializeAutorunSQL() {
        MySQL mysql = Database.getConnection();

        try {
            for (String s : new SQLFileLoader("autorun_sql/").loadSQLFiles()) {
                mysql.Exec(s);
            }
        } catch (IOException | URISyntaxException e) {
            log.error("Failed to autorun sql files", e);
            System.exit(1);
        }
        mysql.close();
    }


}
