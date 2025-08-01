package dev.osunolimits.modules.cron;

import dev.osunolimits.common.Database;
import dev.osunolimits.common.MySQL;
import dev.osunolimits.modules.cron.engine.RunnableCronTask;

public class DatabaseCleanUpTask extends RunnableCronTask {

    @Override
    public void run() {
        int cleanedUp = 0;
        int trashed = 0;
        for (int i = 0; i < Database.runningConnections.size(); i++) {
            MySQL mysql = Database.runningConnections.get(i);
            if (mysql == null) {
                Database.runningConnections.remove(i);
                i--;
                cleanedUp++;
                continue;
            } else {
                long curTime = System.currentTimeMillis();
                if (mysql.connectionCreated + 600000 < curTime) {
                    try {
                        mysql.close();
                        Database.runningConnections.remove(i);
                        i--;
                        trashed++;
                        continue;
                    } catch (Exception e) {
                        logger.warn("Failed to close MySQL connection");
                    }
                }
            }
        }

        if (cleanedUp > 0 || trashed > 0) {
            logger.info("Cleaned up {} and trashed {} MySQL connections", cleanedUp, trashed);
        }
    }

    @Override
    public String getName() {
        return "DatabaseCleanUpTask";
    }

}
