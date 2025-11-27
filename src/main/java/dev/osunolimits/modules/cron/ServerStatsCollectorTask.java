package dev.osunolimits.modules.cron;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import dev.osunolimits.api.BanchoStats;
import dev.osunolimits.api.BanchoStats.PlayerCountResponse;
import dev.osunolimits.main.App;
import dev.osunolimits.modules.cron.engine.RunnableCronTask;

public class ServerStatsCollectorTask extends RunnableCronTask {

    private static final String REDIS_KEY = "shiina:stats:histo";
    private static final long ONE_DAY_SECONDS = 86400;
    private static final long INTERVAL_SECONDS = 900; // 15 minutes

    private static PlayerStatsStore store = new PlayerStatsStore();

    public static PlayerStatsStore getStore() {
        return store;
    }

    private final BanchoStats stats;

    public ServerStatsCollectorTask() {
        stats = new BanchoStats(App.sharedClient);
    }

    @Override
    public void run() {
        PlayerCountResponse playerCount = stats.getPlayerCount();
        if (playerCount != null) {
            int onlinePlayers = playerCount.getOnline();
            store.addValue(onlinePlayers, logger);
            logger.debug("Added player count: {}", onlinePlayers);
        } else {
            logger.warn("Failed to fetch player count from BanchoStats");
        }

        logger.debug("PlayerStatsThread stopped");
    }

    public static class PlayerStatsStore {
        public void addValue(int value, Logger logger) {
            // Round timestamp to 15-minute boundary to ensure only one entry per interval
            long now = Instant.now().getEpochSecond();
            long intervalBoundary = (now / INTERVAL_SECONDS) * INTERVAL_SECONDS; // Round down to 15-min boundary
            String entry = intervalBoundary + ":" + value;

            // Add value with interval boundary timestamp as score (will overwrite if exists)
            App.appCache.zadd(REDIS_KEY, intervalBoundary, entry);
            
            logger.debug("Storing player count: {} at interval boundary: {} ({})", value, intervalBoundary, 
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(intervalBoundary * 1000)));

            // Remove entries older than 24 hours (keep exactly 24 hours of data)
            long cutoffTime = intervalBoundary - ONE_DAY_SECONDS;
            App.appCache.zremrangeByScore(REDIS_KEY, "-inf", String.valueOf(cutoffTime));

            // Additional safety: ensure we don't exceed 96 entries (24 hours * 4 intervals per hour)
            long count = App.appCache.zcard(REDIS_KEY);
            if (count > 96) {
                App.appCache.zremrangeByRank(REDIS_KEY, 0, count - 97); // Keep last 96
            }

            logger.debug("Total entries in AppCache: {}", App.appCache.zcard(REDIS_KEY));
        }

        public List<Map<String, Object>> getLast24Hours() {
            // Get all entries (up to 24 hours)
            List<String> entries = App.appCache.zrange(REDIS_KEY, 0, -1);

            return entries.stream().map(entry -> {
                String[] parts = entry.split(":");
                long timestampSec = Long.parseLong(parts[0]);
                int value = Integer.parseInt(parts[1]);

                Map<String, Object> map = new HashMap<>();
                map.put("time", timestampSec * 1000); // convert to milliseconds
                map.put("value", value);
                return map;
            }).collect(Collectors.toList());
        }

        public int getStoredIntervalsCount() {
            return (int) App.appCache.zcard(REDIS_KEY);
        }
    }

    @Override
    public String getName() {
        return "ServerStatsCollectorTask";
    }

}