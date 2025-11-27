package dev.osunolimits.plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import dev.osunolimits.modules.captcha.CaptchaProvider;
import dev.osunolimits.modules.captcha.DefaultCaptchaProvider;
import dev.osunolimits.modules.geoloc.DefaultGeoLocQuery;
import dev.osunolimits.modules.geoloc.GeoLocProvider;

public class ShiinaRegistry {
    private static Logger log = (Logger) LoggerFactory.getLogger("ShiinaRegistry");
    private static List<ShiinaEventListener> listeners = new ArrayList<>();
    private static List<PluginExporter> exporters = new ArrayList<>();
    private static CaptchaProvider captchaProvider = new DefaultCaptchaProvider();
    private static GeoLocProvider geoLocProvider = new DefaultGeoLocQuery();
    private static HashMap<String, String> settingsIcons = new HashMap<>();

    public static void registerListener(ShiinaEventListener listener) {
        listeners.add(listener);
        log.debug("Registered listener: " + listener.getClass().getSimpleName());
    }

    public static void unregisterListener(ShiinaEventListener listener) {
        listeners.remove(listener);
        log.debug("Unregistered listener: " + listener.getClass().getSimpleName());
    }

    public static void registerExporter(PluginExporter exporter) {
        exporters.add(exporter);
        log.debug("Registered exporter: " + exporter.getClass().getSimpleName());
    }

    public static void unregisterExporter(PluginExporter exporter) {
        exporters.remove(exporter);
        log.debug("Unregistered exporter: " + exporter.getClass().getSimpleName());
    }

    public static void setCaptchaProvider(CaptchaProvider provider) {
        captchaProvider = provider;
        log.debug("Set captcha provider: " + provider.getClass().getSimpleName());
    }

    public static void setGeoLocProvider(GeoLocProvider provider) {
        geoLocProvider = provider;
        log.debug("Set geolocation provider: " + provider.getClass().getSimpleName());
    }

    public static void registerIconToSettingsGroup(String key, String icon) {
        settingsIcons.put(key, icon);
    }

    public static HashMap<String, String> getIconSettingsMap() {
        return settingsIcons;
    }

    public static GeoLocProvider getGeoLocProvider() {
        return geoLocProvider;
    }

    public static CaptchaProvider getCaptchaProvider() {
        return captchaProvider;
    }

    public static List<PluginExporter> getExporters() {
        return exporters;
    }

    public static List<ShiinaEventListener> getListeners() {
        return listeners;
    }
}
