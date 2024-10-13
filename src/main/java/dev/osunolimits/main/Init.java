package dev.osunolimits.main;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import dev.osunolimits.common.Database;
import dev.osunolimits.common.Database.ServerTimezone;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPooled;

public class Init {

    public void initializeWebServer(WebServer webServer) {
        webServer.setThreadPool(10, 20, 60000);
        webServer.createDefaultDirectories();
        webServer.setDefaultDirectories();
        
        try {
            webServer.ignite(App.env.get("HOST"), Integer.parseInt(App.env.get("PORT")), 3000);
        } catch (Exception e) {
            App.log.error("Failed to ignite WebServer", e);
            return;
        }
    }

    public void initializeRedisConfiguration() {
         if (App.loggerEnv.get("HIKARI_LOG").equalsIgnoreCase("FALSE")) {
            App.log.info("Disabling HikariCP logging");
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            Logger hikariLogger = loggerContext.getLogger("com.zaxxer.hikari");
            hikariLogger.setLevel(ch.qos.logback.classic.Level.OFF);
        }
    }

    public void initializeJettyLogging() {
        if (App.loggerEnv.get("JETTY_LOG").equalsIgnoreCase("FALSE")) {
            App.log.info("Disabling Jetty logging");
            LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
            
            // Disable logging for Jetty
            Logger jettyLogger = loggerContext.getLogger("org.eclipse.jetty");
            jettyLogger.setLevel(ch.qos.logback.classic.Level.OFF);
            
            // Optionally, disable specific Jetty components if needed
            Logger serverLogger = loggerContext.getLogger("org.eclipse.jetty.server");
            serverLogger.setLevel(ch.qos.logback.classic.Level.OFF);
            
            Logger handlerLogger = loggerContext.getLogger("org.eclipse.jetty.server.handler");
            handlerLogger.setLevel(ch.qos.logback.classic.Level.OFF);
        }
    }

    public void initializeDatabase() {
        try {
            Database database = new Database();
            database.setDefaultSettings();
            database.setMaximumPoolSize(Integer.parseInt(App.env.get("POOLSIZE")));
            database.setConnectionTimeout(Integer.parseInt(App.env.get("TIMEOUT")));
            database.connectToMySQL(App.env.get("DBHOST"), App.env.get("DBUSER"), App.env.get("DBPASS"), App.env.get("DBNAME"),
                    ServerTimezone.UTC);
        } catch (Exception e) {
            App.log.error("Failed to configure Database", e);
            return;
        }
    }

    public void initializeJedis() {
         try {
            HostAndPort hostAndPort = new HostAndPort(App.env.get("REDISHOST"), Integer.parseInt(App.env.get("REDISPORT")));

            DefaultJedisClientConfig clientConfig = DefaultJedisClientConfig.builder()
            .connectionTimeoutMillis(Integer.parseInt(App.env.get("REDISTIMEOUT")))
            .database(Integer.parseInt(App.env.get("REDISDB")))
            .password(App.env.get("REDISPASS"))
            .user(App.env.get("REDISUSER"))
            .build();

            App.jedisPool = new JedisPooled(hostAndPort, clientConfig);
            App.log.info("Connected to Redis: " + App.jedisPool.ping());
            
        } catch (Exception e) {
            App.log.error("Failed to configure Jedis", e);
            return;
        }
    }
    
}
