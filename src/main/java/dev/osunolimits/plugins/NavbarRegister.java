package dev.osunolimits.plugins;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import dev.osunolimits.plugins.models.NavbarAdminItem;
import dev.osunolimits.plugins.models.NavbarItem;
import dev.osunolimits.plugins.models.NavbarProfileItem;
import dev.osunolimits.plugins.models.NavbarSettingsItem;

public class NavbarRegister {

    private static final Logger log = (Logger) LoggerFactory.getLogger("NavbarRegister");
    private final static int START_ACTNAV = 50;
    private final static int START_SETTINGS_ACTNAV = 120;
    private static List<NavbarItem> items = new ArrayList<>();
    private static List<NavbarAdminItem> adminItems = new ArrayList<>();
    private static List<NavbarSettingsItem> settingsItems = new ArrayList<>();
    private static List<NavbarProfileItem> profileItems = new ArrayList<>();

    public static void register(NavbarItem item) {
        log.debug("Registering navbar item: '" + item.getName() + "' on Route (" + item.getUrl() + ") with actNav ("
                + item.getActNav() + ")");
        items.add(item);
    }

    public static void register(NavbarAdminItem item) {
        log.debug("Registering admin navbar item: '" + item.getName() + "' on Route (" + item.getUrl()
                + ") with actNav (" + item.getActNav() + ")");
        adminItems.add(item);
    }

    public static void register(NavbarSettingsItem item) {
        log.debug("Registering settings navbar item: '" + item.getName() + "' on Route (" + item.getUrl()
                + ") with actNav (" + item.getActNav() + ")");
        settingsItems.add(item);
    }

    public static void register(NavbarProfileItem item) {
        log.debug("Registering profile navbar item: '" + item.getName() + "' on Route (" + item.getUrl() + ")");
        profileItems.add(item);
    }

    public static void unregister(NavbarItem item) {
        items.remove(item);
        log.debug("Unregistered navbar item: " + item.getName());
    }

    public static void unregister(NavbarAdminItem item) {
        adminItems.remove(item);
        log.debug("Unregistered admin navbar item: " + item.getName());
    }

    public static void unregister(NavbarSettingsItem item) {
        settingsItems.remove(item);
        log.debug("Unregistered settings navbar item: " + item.getName());
    }

    public static void unregister(NavbarProfileItem item) {
        profileItems.remove(item);
        log.debug("Unregistered profile navbar item: " + item.getName());
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

    public static List<NavbarProfileItem> getProfileItems() {
        return profileItems;
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

    public static List<Object> getAllItems() {
        List<Object> allItems = new ArrayList<>();
        allItems.addAll(items);
        allItems.addAll(adminItems);
        allItems.addAll(settingsItems);
        allItems.addAll(profileItems);
        return allItems;
    }

    @Deprecated
    public static void registerAdmin(NavbarAdminItem item) {
        log.warn("Deprecated: Use register(NavbarAdminItem item) instead and update your plugins");
        adminItems.add(item);
    }

    @Deprecated
    public static void registerSettings(NavbarSettingsItem item) {
        log.warn("Deprecated: Use register(NavbarSettingsItem item) instead and update your plugins");
        settingsItems.add(item);
    }

    @Deprecated
    public static void registerProfile(NavbarProfileItem item) {
        log.warn("Deprecated: Use register(NavbarProfileItem item) instead and update your plugins");
        profileItems.add(item);
    }

}
