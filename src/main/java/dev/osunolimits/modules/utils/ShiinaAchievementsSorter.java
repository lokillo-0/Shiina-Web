package dev.osunolimits.modules.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ShiinaAchievementsSorter {
    private static final Gson gson = new Gson();
    private static final Logger logger = LoggerFactory.getLogger("ShiinaAchievementsSorter");

    public static HashMap<String, List<Integer>> achievements = new HashMap<>();

    public static void initialize() {
        try {
            if (new File("data/achievements.json").exists()) {
                String json = Files.readString(new File("data/achievements.json").toPath());
                Type type = new TypeToken<HashMap<String, List<Integer>>>() {
                }.getType();
                achievements = gson.fromJson(json, type);
                return;
            }

            logger.info("Generating default achievements.json file");
            achievements.put("Mods", List.of(73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83));
            achievements.put("Full Combo",
                    List.of(65, 66, 67, 68, 69, 70, 71, 72, 49, 50, 51, 52, 53, 54, 55, 56, 33, 34,
                            35, 36, 37, 38, 39, 40, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20));
            achievements.put("Skill",
                    List.of(57, 58, 59, 60, 61, 62, 63, 64, 41, 42, 43, 44, 45, 46, 47, 48, 25, 26, 27,
                            28, 29, 30, 31, 32, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
            achievements.put("Combo", List.of(21, 22, 23, 24));

            String json = gson.toJson(achievements);
            Files.writeString(new File("data/achievements.json").toPath(), json);
            logger.info("Default achievements.json file created successfully");
        } catch (IOException e) {
            logger.error("Failed to handle achievements.json", e);
        }

    }
}
