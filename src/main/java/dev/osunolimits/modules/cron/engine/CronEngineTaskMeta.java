package dev.osunolimits.modules.cron.engine;

import lombok.Data;

@Data
public class CronEngineTaskMeta {
    private String name;
    private CronEngineTaskType type;
    private Long intervalMinutes =null; // For TIMED tasks
    private Integer targetHour = null; // For FIXED_TIMED tasks
    private Integer targetMinute = null; // For FIXED_TIMED tasks

    public static enum CronEngineTaskType {
        FIXED_TIMED,
        TIMED,
        FULL_HOUR,
    }
}
