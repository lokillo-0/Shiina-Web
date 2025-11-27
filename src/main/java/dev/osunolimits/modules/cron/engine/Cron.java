package dev.osunolimits.modules.cron.engine;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cron {
    public static ScheduledExecutorService scheduler;
    private final List<CronTask> tasks = new ArrayList<>();
    private final Logger logger = (Logger) LoggerFactory.getLogger("Cron");
    public static final List<CronEngineTaskMeta> taskMetas = new ArrayList<>();
    /**
     * Runs task every X minutes.
     */
    public void registerTimedTask(long intervalMinutes, CronTask task) {
        tasks.add(task);
        logger.debug("Registering timed task: " + task.getName() + " to run every " + intervalMinutes + " minutes");

        scheduler.scheduleAtFixedRate(() -> {
            try {
                logger.debug("Running task: " + task.getName());
                task.run();
            } catch (Exception e) {
                logger.error("Error running task: " + task.getName(), e);
            }
        }, 0, intervalMinutes, TimeUnit.MINUTES);

        CronEngineTaskMeta meta = new CronEngineTaskMeta();
        meta.setName(task.getName());
        meta.setType(CronEngineTaskMeta.CronEngineTaskType.TIMED);
        meta.setIntervalMinutes(intervalMinutes);
        taskMetas.add(meta);
    }

    /**
     * Runs task every day at specified hour:minute.
     */
    public void registerFixedRateTask(int targetHour, int targetMinute, CronTask task) {
        tasks.add(task);
        logger.debug("Registering fixed rate task: " + task.getName() + " to run every day at " + targetHour + ":"
                + targetMinute);

        long initialDelay = computeInitialDelay(targetHour, targetMinute);
        long oneDay = TimeUnit.DAYS.toSeconds(1);

        scheduler.scheduleAtFixedRate(() -> {
            try {
                logger.debug("Running task: " + task.getName());
                task.run();
            } catch (Exception e) {
                logger.error("Error running task: " + task.getName(), e);
            }
        }, initialDelay, oneDay, TimeUnit.SECONDS);

        CronEngineTaskMeta meta = new CronEngineTaskMeta();
        meta.setName(task.getName());
        meta.setType(CronEngineTaskMeta.CronEngineTaskType.FIXED_TIMED);
        meta.setTargetHour(targetHour);
        meta.setTargetMinute(targetMinute);
        taskMetas.add(meta);
    }

    public void registerTaskEachFullHour(CronTask task) {
        logger.debug("Registering task: " + task.getName() + " to run every full hour");

        long initialDelay = fullHourDelay();
        long oneHour = TimeUnit.HOURS.toSeconds(1);
        
        scheduler.scheduleAtFixedRate(() -> {
            try {
                logger.debug("Running task: " + task.getName());
                task.run();
            }
            catch (Exception e) {
                logger.error("Error running task: " + task.getName(), e);
            }
        }, initialDelay, oneHour, TimeUnit.SECONDS);

        CronEngineTaskMeta meta = new CronEngineTaskMeta();
        meta.setName(task.getName());
        meta.setType(CronEngineTaskMeta.CronEngineTaskType.FULL_HOUR);
        taskMetas.add(meta);
    }

    public void registerTaskEach15Minutes(CronTask task) {
        logger.debug("Registering task: " + task.getName() + " to run every 15 minutes");

        long initialDelay = quarterHourDelay();
        long fifteenMinutes = TimeUnit.MINUTES.toSeconds(15);
        
        scheduler.scheduleAtFixedRate(() -> {
            try {
                logger.debug("Running task: " + task.getName());
                task.run();
            }
            catch (Exception e) {
                logger.error("Error running task: " + task.getName(), e);
            }
        }, initialDelay, fifteenMinutes, TimeUnit.SECONDS);

        CronEngineTaskMeta meta = new CronEngineTaskMeta();
        meta.setName(task.getName());
        meta.setType(CronEngineTaskMeta.CronEngineTaskType.TIMED);
        meta.setIntervalMinutes(15L);
        taskMetas.add(meta);
    }

    public List<CronTask> getTasks() {
        return tasks;
    }

    private long fullHourDelay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.truncatedTo(ChronoUnit.HOURS).plusHours(1);
        return Duration.between(now, nextRun).getSeconds();
    }

    private long quarterHourDelay() {
        LocalDateTime now = LocalDateTime.now();
        int currentMinute = now.getMinute();
        int minutesUntilNext15 = 15 - (currentMinute % 15);
        if (minutesUntilNext15 == 15) {
            minutesUntilNext15 = 0; // Already at 15-minute boundary, run immediately
        }
        LocalDateTime nextRun = now.plusMinutes(minutesUntilNext15).truncatedTo(ChronoUnit.MINUTES);
        return Duration.between(now, nextRun).getSeconds();
    }

    private long computeInitialDelay(int hour, int minute) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextRun = now.withHour(hour).withMinute(minute).withSecond(0);

        if (now.isAfter(nextRun)) {
            nextRun = nextRun.plusDays(1); // Schedule for tomorrow
        }

        return Duration.between(now, nextRun).getSeconds();
    }

    public void shutdown() {
        for (CronTask task : tasks) {
            task.shutdown();
        }
        scheduler.shutdown();
    }
}
