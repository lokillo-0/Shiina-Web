package dev.osunolimits.plugins.models;

import dev.osunolimits.plugins.NavbarRegister;
import lombok.Data;

@Data
public class NavbarSettingsItem {
    private String name;
    private String url;
    private int actNav;
    private String icon;

    public NavbarSettingsItem(String name, String url) {
        this.name = name;
        this.url = url;
        actNav = NavbarRegister.getSettingsActNav();
    }
}
