package dev.osunolimits.routes.get.modular;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import dev.osunolimits.plugins.ModuleConfiguration;
import dev.osunolimits.plugins.ModuleConfiguration.ModuleSetting;
import spark.Request;
import spark.Response;

public class ModuleRegister {

    private static final List<ModulePath> modules = new ArrayList<>();
    private static final Map<String, List<ShiinaModule>> loadedModules = new HashMap<>();
    private static final Logger log = (Logger) LoggerFactory.getLogger("ModuleRegister");
    private static final ModuleConfiguration config = new ModuleConfiguration();

    public static void registerDefaultModule(String forPage, ShiinaModule module) {
        ModulePath modulePath = new ModulePath(forPage, module, true, false);
        modules.add(modulePath);
        log.debug("Registered default module for " + forPage + ": " + module.moduleName());
    }

    public static void registerInternalModule(String forPage, ShiinaModule module) {
        ModulePath modulePath = new ModulePath(forPage, module, true, true);
        modules.add(modulePath);
        log.debug("Registered default module for " + forPage + ": " + module.moduleName());
    }

    public static void registerModule(String forPage, ShiinaModule module) {
        ModulePath modulePath = new ModulePath(forPage, module, false, false);
        modules.add(modulePath);
        log.debug("Registered module for " + forPage + ": " + module.moduleName());
    }

    public static List<String> getPages() {
        Set<String> pagesSet = new HashSet<>();
        for(ModulePath modulePath : modules) {
            pagesSet.add(modulePath.getPath());
        }
        return new ArrayList<>(pagesSet);
    }

    public static ModuleConfiguration getConfig() {
        return config;
    }

    public static List<ShiinaModule> getModulesForPage(String page) {
        return loadedModules.get(page);
    }

    public static List<String> getModulesRawForPage(String page, Request req, Response res, ShiinaRequest shiina) {
         if (ModuleRegister.getModulesForPage(page) != null) {
            List<String> modulesRaw = new ArrayList<>();
            for (ShiinaModule module : ModuleRegister.getModulesForPage(page)) {
                modulesRaw.add(module.handle(req, res, shiina));
            }
            return modulesRaw;
        }
        return List.of();
    }

    public static void reloadModuleConfigurations() {
        log.debug("Reloading Module System 2.0...");
        
        loadedModules.clear();
        for (String page : getPages()) {
            List<ShiinaModule> modulesForPage = new ArrayList<>();
            for (ModulePath modulePath : modules) {
                if (modulePath.getPath().equals(page)) {
                    modulesForPage.add(modulePath.getModule());
                    log.debug("Loaded module for " + page + ": " + modulePath.getModule().moduleName());
                }
            }
            loadedModules.put(page, modulesForPage);
        }

        config.load();
        
        if (config.isEmpty()) {
            Map<String, List<String>> defaultModulesByPage = new HashMap<>();
            for (String page : getPages()) {
                List<String> moduleNames = new ArrayList<>();
                for (ShiinaModule module : loadedModules.get(page)) {
                    moduleNames.add(module.moduleName());
                }
                defaultModulesByPage.put(page, moduleNames);
            }
            config.createDefault(defaultModulesByPage);
        }

        boolean settingsUpdated = false;
        
        // Clean up obsolete pages from config
        if (config.cleanupObsoletePages(getPages())) {
            settingsUpdated = true;
        }
        
        // Check for new pages not in config and add them
        for (String page : loadedModules.keySet()) {
            if (!config.getAllSettings().containsKey(page)) {
                List<String> moduleNames = new ArrayList<>();
                for (ShiinaModule module : loadedModules.get(page)) {
                    moduleNames.add(module.moduleName());
                }
                if (config.addNewModules(page, moduleNames)) {
                    settingsUpdated = true;
                    log.info("Auto-added new page '" + page + "' to configuration");
                }
            }
        }
        
        // Process all pages (both existing and newly added)
        for (String page : loadedModules.keySet()) {
            List<String> currentModuleNames = new ArrayList<>();
            for (ShiinaModule module : loadedModules.get(page)) {
                currentModuleNames.add(module.moduleName());
            }
            
            // Clean up obsolete modules from config
            if (config.cleanupObsoleteModules(page, currentModuleNames)) {
                settingsUpdated = true;
            }
            
            // Auto-add new modules to existing pages
            if (config.addNewModules(page, currentModuleNames)) {
                settingsUpdated = true;
            }

            // Get the updated setting after cleanup and additions
            ModuleSetting setting = config.getSettingForPage(page);
            
            // Apply sorted and filtered modules
            List<ShiinaModule> configuredModules = new ArrayList<>();
            for (String moduleName : setting.modulesSorted) {
                for (ShiinaModule module : loadedModules.get(page)) {
                    if (module.moduleName().equals(moduleName) && !setting.modulesBlocked.contains(moduleName)) {
                        configuredModules.add(module);
                        break;
                    }
                }
            }

            loadedModules.put(page, configuredModules);
        }

        if (settingsUpdated) {
            config.save();
        }

        for(String page : loadedModules.keySet()) {
            log.info("Page '" + page + "' has " + loadedModules.get(page).size() + " modules loaded.");
        }
    }

    public static void blockModule(String page, String moduleName) {
        config.blockModule(page, moduleName);
        config.save();
        reloadModuleConfigurations();
    }

    public static void unblockModule(String page, String moduleName) {
        config.unblockModule(page, moduleName);
        config.save();
        reloadModuleConfigurations();
    }

    public static void setSortedModules(String page, List<String> sortedModuleNames) {
        config.setSortedModules(page, sortedModuleNames);
        config.save();
        reloadModuleConfigurations();
    }
}



