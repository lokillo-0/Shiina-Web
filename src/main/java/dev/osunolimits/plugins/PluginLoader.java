package dev.osunolimits.plugins;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;
import lombok.AllArgsConstructor;
import lombok.Data;

public class PluginLoader {

    private static final String PLUGINS_DIR = "plugins/";
    private static Logger log = (Logger) LoggerFactory.getLogger("PluginLoader");
    private static List<ShiinaPlugin> currentLoadedPlugins = new ArrayList<>();
    private static HashMap<ShiinaPlugin, PluginMetadata> mainPluginMetadata = new HashMap<>();

    public static List<ShiinaPlugin> getCurrentLoadedPlugins() {
        return currentLoadedPlugins;
    }

    public void loadPlugins() {
        try {
            Path pluginsPath = Paths.get(PLUGINS_DIR);
            if (!Files.exists(pluginsPath)) {
                Files.createDirectories(pluginsPath);
                log.info("Created plugins directory: " + PLUGINS_DIR);
                return;
            }

            Map<String, PluginMetadata> pluginMetadataMap = new HashMap<>();
            Set<String> loadedPlugins = new HashSet<>();

            // Step 1: Read and parse all the plugin jars
            try (DirectoryStream<Path> jarFiles = Files.newDirectoryStream(pluginsPath, "*.jar")) {
                for (Path jarPath : jarFiles) {
                    PluginMetadata metadata = parsePluginMetadata(jarPath.toString());
                    if (metadata != null) {
                        pluginMetadataMap.put(metadata.name, metadata);
                    }
                }
            }

            // Step 2: Sort plugins based on dependencies
            List<PluginMetadata> sortedPlugins = sortPluginsByDependency(pluginMetadataMap);

            // Step 3: Load plugins one by one while respecting dependencies
            for (PluginMetadata metadata : sortedPlugins) {
                if (!loadedPlugins.contains(metadata.name)) {
                    loadPluginFromMetadata(metadata, loadedPlugins, pluginMetadataMap);
                }
            }
        } catch (Exception e) {
            log.error("Error loading plugins: ", e);
        }
    }

    private PluginMetadata parsePluginMetadata(String jarFilePath) {
        try (JarFile jarFile = new JarFile(jarFilePath)) {
            JarEntry pluginYmlEntry = jarFile.getJarEntry("plugin.yml");
            if (pluginYmlEntry == null) {
                log.error("No plugin.yml in " + jarFilePath);
                return null;
            }

            try (InputStream inputStream = jarFile.getInputStream(pluginYmlEntry)) {
                java.util.Properties properties = new java.util.Properties();
                properties.load(inputStream);

                String mainClass = properties.getProperty("main");
                String pluginName = properties.getProperty("name");
                String dependsOn = properties.getProperty("depends-on");
                

                if (mainClass == null || pluginName == null) {
                    log.error("plugin.yml missing required fields in " + jarFilePath);
                    return null;
                }

                List<String> dependencies = (dependsOn != null) ? List.of(dependsOn.split(",")) : List.of();
                return new PluginMetadata(pluginName.trim(), mainClass.trim(), dependencies, jarFilePath);
            }
        } catch (Exception e) {
            log.error("Error reading plugin metadata from " + jarFilePath, e);
            return null;
        }
    }

    private List<PluginMetadata> sortPluginsByDependency(Map<String, PluginMetadata> plugins) {
        List<PluginMetadata> sorted = new ArrayList<>();
        Set<String> visited = new HashSet<>();
        Set<String> stack = new HashSet<>();

        for (PluginMetadata plugin : plugins.values()) {
            if (!visited.contains(plugin.name)) {
                if (!topologicalSort(plugin, plugins, visited, stack, sorted)) {
                    log.error("Circular dependency detected involving " + plugin.name);
                    return List.of(); // Prevents loading in case of a cycle
                }
            }
        }

        return sorted;
    }

    private boolean topologicalSort(PluginMetadata plugin, Map<String, PluginMetadata> plugins,
            Set<String> visited, Set<String> stack, List<PluginMetadata> sorted) {
        if (stack.contains(plugin.name))
            return false; // Cycle detected
        if (visited.contains(plugin.name))
            return true;

        stack.add(plugin.name);
        for (String dep : plugin.dependencies) {
            PluginMetadata depPlugin = plugins.get(dep.trim());
            if (depPlugin != null && !topologicalSort(depPlugin, plugins, visited, stack, sorted)) {
                return false;
            }
        }

        stack.remove(plugin.name);
        visited.add(plugin.name);
        sorted.add(plugin);
        return true;
    }

    private void loadPluginFromMetadata(PluginMetadata metadata, Set<String> loadedPlugins,
            Map<String, PluginMetadata> pluginMetadataMap) {
        Logger logger = (Logger) LoggerFactory.getLogger("Plugin [" + metadata.name + "]");
        try {
            // Step 1: Load dependencies first
            for (String dependency : metadata.dependencies) {
                if (!loadedPlugins.contains(dependency)) {
                    PluginMetadata depMetadata = pluginMetadataMap.get(dependency);
                    if (depMetadata != null) {
                        loadPluginFromMetadata(depMetadata, loadedPlugins, pluginMetadataMap);
                    }
                }
            }

            

            // Step 2: Load the plugin itself
            loadAndEnablePlugin(metadata, logger);
            loadedPlugins.add(metadata.name);
          
            
            logger.info("Successfully loaded plugin: " + metadata.name);
        } catch (Exception e) {
            log.error("Error loading plugin [" + metadata.name + "]: ", e);
        }
    }

    @Data
    @AllArgsConstructor
    public static class PluginMetadata {
        private String name;
        private String mainClass;
        private List<String> dependencies;
        private String jarFilePath;
    }

    private void loadAndEnablePlugin(PluginMetadata metadata, Logger logger) {
        try {
            URL[] urls = { Paths.get(metadata.jarFilePath).toUri().toURL() };
            URLClassLoader classLoader = new URLClassLoader(urls, PluginLoader.class.getClassLoader());

            Class<?> clazz = Class.forName(metadata.mainClass, true, classLoader);

            if (!ShiinaPlugin.class.isAssignableFrom(clazz)) {
                logger.info(metadata.mainClass + " does not extend ShiinaPlugin");
                return;
            }

            copyModulesIfFound(metadata);

            ShiinaPlugin pluginInstance = (ShiinaPlugin) clazz.getDeclaredConstructor().newInstance();
            mainPluginMetadata.put(pluginInstance, metadata);
            pluginInstance.onEnable(metadata.name, logger);
            currentLoadedPlugins.add(pluginInstance);
        } catch (Exception e) {
            logger.error("Error loading and enabling plugin: ", e);
        }
    }

    private void copyModulesIfFound(PluginMetadata metadata) {
        try (JarFile jarFile = new JarFile(metadata.jarFilePath)) {
            // Check if there are any entries that start with "modules/"
            boolean hasModules = jarFile.stream()
                    .anyMatch(entry -> entry.getName().startsWith("modules/") && !entry.isDirectory());

            if (hasModules) {
                log.debug("Found modules directory in JAR for plugin: " + metadata.name);
                Path targetBasePath = Paths.get("templates/modules/plugins");
                Files.createDirectories(targetBasePath);

                // Extract all files from the modules directory 
                jarFile.stream()
                        .filter(entry -> entry.getName().startsWith("modules/") && !entry.isDirectory())
                        .forEach(entry -> {
                            try {
                                // Remove "modules/" prefix to get relative path
                                String relativePath = entry.getName().substring("modules/".length());
                                Path destFile = targetBasePath.resolve(relativePath);

                                // Create parent directories if needed
                                Files.createDirectories(destFile.getParent());

                                // Copy file content with REPLACE_EXISTING option
                                try (InputStream inputStream = jarFile.getInputStream(entry)) {
                                    Files.copy(inputStream, destFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                                }

                            } catch (IOException e) {
                                log.error("Error extracting module file: " + entry.getName(), e);
                            }
                        });

            }
        } catch (IOException e) {
            log.error("Error reading JAR file for modules: " + metadata.jarFilePath, e);
        }
    }

    public static List<PluginMetadata> getLoadedPluginMetadata() {
        return new ArrayList<>(mainPluginMetadata.values());
    }
}