package dev.osunolimits.main.init.cfg;

import java.io.IOException;
import java.net.URISyntaxException;

import dev.osunolimits.common.Database;
import dev.osunolimits.common.MySQL;
import dev.osunolimits.main.App;
import dev.osunolimits.main.init.engine.RunableInitTask;
import dev.osunolimits.utils.SQLFileLoader;

public class AutorunSQLTask extends RunableInitTask {
    @Override
    public void execute() throws Exception {
        if (App.devMode)
            return;

        MySQL mysql = Database.getConnection();
        try {
            for (String s : new SQLFileLoader("autorun_sql/", App.class.getClassLoader()).loadSQLFiles()) {
                mysql.Exec(s);
            }
        } catch (IOException | URISyntaxException e) {
            logger.error("Failed to autorun sql files", e);
            System.exit(1);
        } finally {
            mysql.close();
        }

    }

    @Override
    public String getName() {
        return "AutorunSQLTask";
    }
}
