package dev.osunolimits.modules.cron.engine;

public interface CronTask {

    public void initialize();

    public void run();

    public void shutdown();

    public String getName();
}
