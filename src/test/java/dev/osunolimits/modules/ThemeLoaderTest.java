package dev.osunolimits.modules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.osunolimits.main.App;
import dev.osunolimits.main.Init;
import dev.osunolimits.modules.utils.ThemeLoader;

class ThemeLoaderTest {

    @BeforeEach
    void setUp() throws Exception {
        Init init = new Init();
        App.customization = init.initializeCustomizations();
    }

    @Test
    void testLoadExistingTheme() {
        ThemeLoader.loadThemes();
        assertTrue(ThemeLoader.themes.stream().anyMatch(t -> t.getName().equals("modern")),
                "Expected theme 'modern' to be loaded.");
    }

    @Test
    void testLoadNonExistingTheme() {
        ThemeLoader.loadThemes();
        assertFalse(ThemeLoader.themes.stream().anyMatch(t -> t.getName().equals("nonexistent")),
                "Theme 'nonexistent' should not be loaded.");
    }

    @Test
    void testCurrentThemeSelection() {
        App.customization.put("theme", "modern");
        ThemeLoader.loadThemes();
        assertNotNull(ThemeLoader.currentTheme, "Current theme should be set.");
        assertEquals("modern", ThemeLoader.currentTheme.getName(), "Expected current theme to be 'modern'.");
    }
} 

