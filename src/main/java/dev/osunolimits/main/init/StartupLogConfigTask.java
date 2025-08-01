package dev.osunolimits.main.init;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import dev.osunolimits.main.App;
import dev.osunolimits.main.init.engine.RunableInitTask;

public class StartupLogConfigTask extends RunableInitTask {
    @Override
    public void execute() throws Exception {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        if (App.loggerEnv.get("HIKARI_LOG").equalsIgnoreCase("FALSE")) {
            Logger hikariLogger = loggerContext.getLogger("com.zaxxer.hikari");
            hikariLogger.setLevel(Level.OFF);
        }

        if (App.loggerEnv.get("JETTY_LOG").equalsIgnoreCase("FALSE")) {
            Logger jettyLogger = loggerContext.getLogger("org.eclipse.jetty");
            jettyLogger.setLevel(Level.OFF);

            Logger serverLogger = loggerContext.getLogger("org.eclipse.jetty.server");
            serverLogger.setLevel(Level.OFF);

            Logger handlerLogger = loggerContext.getLogger("org.eclipse.jetty.server.handler");
            handlerLogger.setLevel(Level.OFF);
        }
    }

    @Override
    public String getName() {
        return "StartupLogConfigTask";
    }
}
