package dev.osunolimits.plugins;

import java.util.ArrayList;
import java.util.List;

import dev.osunolimits.plugins.models.NavbarItem;

public class NavbarRegister {

    private final static int START_ACTNAV = 20;
    private static List<NavbarItem> items = new ArrayList<>();

    public static void register(NavbarItem item) {
        items.add(item);
    }

    public static List<NavbarItem> getItems() {
        return items;
    }

    public static int getActNav() {
        return START_ACTNAV + items.size();
    }
    
}
