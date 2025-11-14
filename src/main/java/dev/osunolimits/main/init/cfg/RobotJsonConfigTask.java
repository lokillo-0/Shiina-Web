package dev.osunolimits.main.init.cfg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import com.google.gson.Gson;

import dev.osunolimits.main.App;
import dev.osunolimits.main.init.engine.RunableInitTask;
import lombok.Data;

public class RobotJsonConfigTask extends RunableInitTask {
    private final Gson gson = new Gson();
    public static Robots robots;

    @Override
    public void execute() throws Exception {
        File robotConfig = new File("data/robots.json");

        // Ensure data folder exists
        if (!robotConfig.getParentFile().exists()) {
            robotConfig.getParentFile().mkdirs();
        }

        // Create default robots.json if missing
        if (!robotConfig.exists()) {
            Robots defaultRobots = new Robots();
            defaultRobots.getRobots().add(new RobotEntry("Googlebot", RobotType.DISALLOW, "/shiina"));
            try (FileWriter writer = new FileWriter(robotConfig)) {
                writer.write(gson.toJson(defaultRobots));
                App.log.info("Created default robot configuration at {}", robotConfig.getPath());
            } catch (IOException e) {
                App.log.error("Failed to create default robot configuration", e);
            }
            robots = defaultRobots;
        } else {
            // Load existing robots.json
            try {
                String fileContent = Files.readString(robotConfig.toPath());
                robots = gson.fromJson(fileContent, Robots.class);
            } catch (IOException e) {
                App.log.error("Failed to read robot configuration", e);
            }
        }

        // Update robots.txt on startup
        updateRobotsTxt();
    }

    @Override
    public String getName() {
        return "RobotJsonConfigTask";
    }

    public static void updateRobotsTxt() {
        File robotsTxt = new File("static/robots.txt");
        try {
            if (!robotsTxt.getParentFile().exists()) {
                robotsTxt.getParentFile().mkdirs();
            }
            try (FileWriter writer = new FileWriter(robotsTxt)) {
                for (RobotEntry robotEntry : robots.getRobots()) {
                    writer.write("User-agent: " + robotEntry.getUserAgent() + "\n");
                    writer.write(
                        Character.toUpperCase(robotEntry.getType().name().charAt(0)) +
                        robotEntry.getType().name().substring(1).toLowerCase() +
                        ": " + robotEntry.getPath() + "\n"
                    );
                }
            }
        } catch (IOException e) {
            App.log.error("Failed to update robots.txt", e);
        }
    }

    @Data
    public static class Robots {
        private java.util.List<RobotEntry> robots = new java.util.ArrayList<>();
    }

    @Data
    public static class RobotEntry {
        private String userAgent;
        private RobotType type;
        private String path;

        public RobotEntry(String userAgent, RobotType type, String path) {
            this.userAgent = userAgent;
            this.type = type;
            this.path = path;
        }
    }

    public enum RobotType {
        DISALLOW,
        ALLOW
    }
}
