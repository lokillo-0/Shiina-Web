package dev.osunolimits.main.init;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import dev.osunolimits.main.App;
import dev.osunolimits.main.init.engine.RunableInitTask;
import dev.osunolimits.modules.cron.engine.Cron;

public class StartupSetupCronTask extends RunableInitTask {
    @Override
    public void execute() throws Exception {
        int cronThreads = Integer.parseInt(App.env.get("CRON_THREADS", "3"));

        ThreadFactory threadFactory = new ThreadFactory() {
            private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();
            private int count = 1;

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = defaultFactory.newThread(r);
                thread.setName("ShiinaCron-" + count++);
                return thread;
            }
        };
        Cron.scheduler = Executors.newScheduledThreadPool(cronThreads, threadFactory);
    }

    @Override
    public String getName() {
        return "StartupSetupCronTask";
    }
}
