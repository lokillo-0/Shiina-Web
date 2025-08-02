package dev.osunolimits.main.init;

import dev.osunolimits.common.Database;
import dev.osunolimits.common.Database.ServerTimezone;
import dev.osunolimits.main.App;
import dev.osunolimits.main.init.engine.RunableInitTask;

public class StartupDatabaseTask extends RunableInitTask {
    @Override
    public void execute() throws Exception {
        Database database = new Database();
        database.setOptimizedSettings();
        
        Integer poolSize = Integer.parseInt(App.env.get("POOLSIZE"));
        if (poolSize < 10) {
            logger.warn("Pool size is set to a low value: " + poolSize + ". At least 10 is recommended.");
        }

        database.setMaximumPoolSize(Integer.parseInt(App.env.get("POOLSIZE")));
        database.setConnectionTimeout(Integer.parseInt(App.env.get("TIMEOUT")));
        database.connectToMySQL(logger, App.env.get("DBHOST"), App.env.get("DBUSER"), App.env.get("DBPASS"),
                App.env.get("DBNAME"),
                ServerTimezone.UTC);
    }

    @Override
    public String getName() {
        return "StartupSetupDatabaseTask";
    }
}
