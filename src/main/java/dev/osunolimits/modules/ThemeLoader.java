package dev.osunolimits.modules;

import java.io.File;
import java.util.ArrayList;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

import dev.osunolimits.models.Theme;

public class ThemeLoader {
    
    public static Logger logger = (Logger) LoggerFactory.getLogger(ThemeLoader.class);
    public static ArrayList<Theme> themes = new ArrayList<Theme>();
    
    public static void loadThemes() {
        File directory = new File("themes/");

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        
                        File f = new File("themes/" + file.getName() + "/theme.yml");
                        if(f.exists() && !f.isDirectory()) {
                            Theme theme = Theme.loadTheme(f);
                            if (theme != null) {
                                themes.add(theme);
                                logger.info("Loaded theme: [" + file.getName() + "]");
                            }

                        } else {
                            logger.error("Theme [" + file.getName() + "] does not have a theme.yml file.");
                        }
                    }
                }
            } else {
                logger.warn("Themes directory is empty.");
            }
        } else {
            logger.error("Themes directory does not exist.");
        }

    }
}
