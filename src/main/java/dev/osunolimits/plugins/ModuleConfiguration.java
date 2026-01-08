package dev.osunolimits.plugins;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import ch.qos.logback.classic.Logger;
import lombok.Data;
import lombok.Getter;

public class ModuleConfiguration {
    
    private static final Logger log = (Logger) LoggerFactory.getLogger("ModuleConfiguration");
    private static final File modulesDir = new File("data/modules");
    private static final File settingsFile = new File(modulesDir, "settings.json");
    @Getter
    public Map<String, ModuleSetting> settings = new HashMap<>();

    public ModuleConfiguration() {
        if (!modulesDir.exists()) {
            modulesDir.mkdirs();
        }
    }

    public void load() {
        if (!settingsFile.exists()) {
            log.info("No module configuration file found at " + settingsFile.getAbsolutePath());
            settings = new HashMap<>();
            return;
        }

        try {
            String json = Files.readString(settingsFile.toPath());
            settings = new Gson().fromJson(json, new TypeToken<Map<String, ModuleSetting>>(){}.getType());
            log.info("Loaded module configuration from " + settingsFile.getAbsolutePath());
        } catch (Exception e) {
            log.error("Failed to read module configuration file: " + e.getMessage());
            settings = new HashMap<>();
        }
    }

    public void save() {
        try {
            Files.writeString(settingsFile.toPath(), new Gson().toJson(settings));
            log.info("Saved module configuration to " + settingsFile.getAbsolutePath());
        } catch (Exception e) {
            log.error("Failed to save module configuration file: " + e.getMessage());
        }
    }

    public void createDefault(Map<String, List<String>> defaultModulesByPage) {
        settings.clear();
        for (Map.Entry<String, List<String>> entry : defaultModulesByPage.entrySet()) {
            ModuleSetting setting = new ModuleSetting();
            setting.modulesSorted.addAll(entry.getValue());
            settings.put(entry.getKey(), setting);
        }
        save();
        log.info("Created default module configuration file at " + settingsFile.getAbsolutePath());
    }

    public ModuleSetting getSettingForPage(String page) {
        return settings.computeIfAbsent(page, k -> new ModuleSetting());
    }

    public Map<String, ModuleSetting> getAllSettings() {
        return settings;
    }

    public boolean addNewModules(String page, List<String> moduleNames) {
        ModuleSetting setting = getSettingForPage(page);
        boolean updated = false;
        
        for (String moduleName : moduleNames) {
            if (!setting.modulesSorted.contains(moduleName)) {
                setting.modulesSorted.add(moduleName);
                updated = true;
                log.info("Auto-added new module '" + moduleName + "' to page '" + page + "'");
            }
        }
        
        return updated;
    }

    public void blockModule(String page, String moduleName) {
        ModuleSetting setting = getSettingForPage(page);
        if (!setting.modulesBlocked.contains(moduleName)) {
            setting.modulesBlocked.add(moduleName);
            log.info("Blocked module '" + moduleName + "' on page '" + page + "'");
            save();
        }
    }

    public void unblockModule(String page, String moduleName) {
        ModuleSetting setting = getSettingForPage(page);
        if (setting.modulesBlocked.remove(moduleName)) {
            log.info("Unblocked module '" + moduleName + "' on page '" + page + "'");
            save();
        }
    }

    public void setSortedModules(String page, List<String> sortedModuleNames) {
        ModuleSetting setting = getSettingForPage(page);
        setting.modulesSorted.clear();
        setting.modulesSorted.addAll(sortedModuleNames);
        log.info("Updated sorted module list for page '" + page + "'");
        save();
    }

    public boolean isEmpty() {
        return settings.isEmpty();
    }

    @Data
    public static class ModuleSetting {
        public List<String> modulesSorted = new ArrayList<>();
        public List<String> modulesBlocked = new ArrayList<>();
    }
}
