package dev.osunolimits.main.init.logger;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import dev.osunolimits.main.App;
import dev.osunolimits.main.init.engine.RunableInitTask;

public class StartupLoggerLevelTask extends RunableInitTask {

    @Override
    public void execute() throws Exception {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.valueOf(App.loggerEnv.get("LEVEL", "INFO").toUpperCase()));
    }

    @Override
    public String getName() {
        return "StartupLoggerLevelTask";
    }
    
}
