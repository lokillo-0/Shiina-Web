package dev.osunolimits.plugins;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import dev.osunolimits.plugins.models.NavbarAdminItem;
import dev.osunolimits.plugins.models.NavbarItem;
import dev.osunolimits.plugins.models.NavbarSettingsItem;

public class NavbarRegister {

    private static final Logger log = (Logger) LoggerFactory.getLogger("NavbarRegister");
    private final static int START_ACTNAV = 22;
    private final static int START_SETTINGS_ACTNAV = 120;
    private static List<NavbarItem> items = new ArrayList<>();
    private static List<NavbarAdminItem> adminItems = new ArrayList<>();
    private static List<NavbarSettingsItem> settingsItems = new ArrayList<>();

    public static void register(NavbarItem item) {
        log.debug("Registering navbar item: '" + item.getName() + "' on Route (" + item.getUrl()+ ") with actNav (" + item.getActNav() + ")");
        items.add(item);
    }

    public static void registerAdmin(NavbarAdminItem item) {
        log.debug("Registering admin navbar item: '" + item.getName() + "' on Route (" + item.getUrl()+ ") with actNav (" + item.getActNav() + ")");
        adminItems.add(item);
    }

    public static void registerSettings(NavbarSettingsItem item) {
        log.debug("Registering settings navbar item: '" + item.getName() + "' on Route (" + item.getUrl()+ ") with actNav (" + item.getActNav() + ")");
        settingsItems.add(item);
    }

    public static List<NavbarItem> getItems() {
        return items;
    }

    public static List<NavbarAdminItem> getAdminItems() {
        return adminItems;
    }

    public static List<NavbarSettingsItem> getSettingsItems() {
        return settingsItems;
    }

    public static int getActNav() {
        return START_ACTNAV + items.size();
    }

    public static int getAdminActNav() {
        return START_ACTNAV + adminItems.size();
    }

    public static int getSettingsActNav() {
        return START_SETTINGS_ACTNAV + settingsItems.size();
    }

}
