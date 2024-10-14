package dev.osunolimits.main;

import java.io.File;
import java.io.IOException;

import ch.qos.logback.classic.Logger;
import dev.osunolimits.utils.OsuConverter;
import freemarker.template.Configuration;
import freemarker.template.TemplateModelException;
import spark.Spark;

public class WebServer extends Spark {

    private Logger logger = null;
    public WebServer(Logger logger) {
        this.logger = logger;
    }

    public static Configuration freemarkerCfg = new Configuration(Configuration.VERSION_2_3_23);

    public void setThreadPool(int minThreads, int maxThreads, int timeOutMillis) {
        threadPool(maxThreads, minThreads, timeOutMillis);
    }

    public void setTemplateUpdateDelay(int delay) {
        freemarkerCfg.setTemplateUpdateDelayMilliseconds(delay);
    }

    public void createDefaultDirectories() {
        if (new File("templates/").mkdirs()) {
            logger.info("Created templates directory 'templates/'");
        }
        if (new File("static/").mkdirs()) {
            logger.info("Created templates directory 'static/'");
        }
    }

    public void setDefaultDirectories() {
        Spark.staticFiles.externalLocation("static/");
    }

    public void ignite(String ip, int port, int updateDelay) {
        ipAddress(ip);
        port(port);

        try {
            freemarkerCfg.setDirectoryForTemplateLoading(new File("templates/"));
        } catch (IOException e) {
            logger.error("Failed to load templates directory", e);
        }
        freemarkerCfg.setTemplateUpdateDelayMilliseconds(updateDelay);
        try {
            freemarkerCfg.setSharedVariable("OsuConverter", OsuConverter.class);
        } catch (TemplateModelException e) {
            App.log.error("Error injecting OsuConverter into Freemarker");
        }
        after((req, res) -> {
            res.header("Server", "Shiina-Web");
            
            logger.info("| Web | " + req.ip()  + " | " + req.requestMethod() + " (" + res.status() + ") | " + req.url() + " | " + req.userAgent() + " | " + req.headers("Referer"));
        });
        awaitInitialization();

        logger.info("WebServer ignited on -> (" + ip + ":" + port + ")");
    }

}
