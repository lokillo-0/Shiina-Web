package dev.osunolimits.plugins;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;

import dev.osunolimits.common.Database;
import dev.osunolimits.common.MySQL;

/**
 * Utility class for loading and executing SQL files from plugin JARs.
 * Used to automatically run SQL scripts bundled with plugins (e.g., for migrations or setup).
 */
public class PluginAutorunSQL {

    /** Logger for debug/info/error output. */
    private Logger logger;

    /**
     * Constructs a PluginAutorunSQL instance with the provided logger.
     * @param logger Logger instance for output
     */
    public PluginAutorunSQL(Logger logger) {
        this.logger = logger;
    }

    /**
     * Loads and executes all SQL files found in the 'autorun_sql/' directory inside the given plugin JAR.
     * Each .sql file is executed using the application's MySQL connection.
     *
     * @param pluginJarPath Path to the plugin JAR file use getClass().getProtectionDomain().getCodeSource().getLocation().getPath() in plugin main class
     */
    public void loadfromPlugin(String pluginJarPath) {
        logger.debug("Loading and executing SQL files from plugin: " + pluginJarPath);
        MySQL sql = Database.getConnection();
        try {
            // Open the plugin JAR file
            JarFile jarFile = new JarFile(pluginJarPath);
            Enumeration<JarEntry> entries = jarFile.entries();
            // Iterate through all entries in the JAR
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                // Check for SQL files in the autorun_sql directory
                if (entry.getName().startsWith("autorun_sql/") && entry.getName().endsWith(".sql")
                        && !entry.isDirectory()) {
                    try (InputStream is = jarFile.getInputStream(entry)) {
                        String content;
                        // Read the SQL file content as a string
                        try (Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A")) {
                            content = scanner.next();
                        }
                        logger.info("Executing SQL file from JAR: " + entry.getName());
                        // Execute the SQL content
                        sql.Exec(content);
                    } catch (Exception ex) {
                        logger.error("Failed to execute SQL file: " + entry.getName(), ex);
                    }
                }
            }
            jarFile.close();
        } catch (IOException e) {
            logger.error("Failed to load SQL files from plugin JAR for votifier", e);
        }
        // Close the MySQL connection
        sql.close();
    }

}
