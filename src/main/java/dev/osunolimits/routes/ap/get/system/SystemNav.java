package dev.osunolimits.routes.ap.get.system;

import java.util.ArrayList;
import java.util.List;

import dev.osunolimits.modules.Shiina;
import dev.osunolimits.modules.ShiinaRoute;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import dev.osunolimits.plugins.NavbarRegister;
import dev.osunolimits.plugins.models.NavbarAdminItem;
import dev.osunolimits.plugins.models.NavbarItem;
import dev.osunolimits.plugins.models.NavbarProfileItem;
import dev.osunolimits.plugins.models.NavbarSettingsItem;
import dev.osunolimits.utils.osu.PermissionHelper;
import lombok.Data;
import spark.Request;
import spark.Response;

public class SystemNav extends Shiina {

    @Override
    public Object handle(Request req, Response res) throws Exception {
        ShiinaRequest shiina = new ShiinaRoute().handle(req, res);
        shiina.data.put("actNav", 10);

        if (!shiina.loggedIn) {
            return redirect(res, shiina, "/");
        }

        if (!PermissionHelper.hasPrivileges(shiina.user.priv, PermissionHelper.Privileges.ADMINISTRATOR)) {
            return redirect(res, shiina, "/");
        }

        List<SystemNavItem> navItems = new ArrayList<>();
        for(Object o : NavbarRegister.getAllItems()) {
            SystemNavItem item = new SystemNavItem();
            if(o instanceof NavbarItem ni) {
                item.target = "NavbarItem";
                item.name = ni.getName();
                item.link = ni.getUrl();
                item.auth = ni.isLoggedInOnly();
            }else if(o instanceof NavbarAdminItem nai) {
                item.target = "NavbarAdminItem";
                item.name = nai.getName();
                item.link = nai.getUrl();
                item.privilege = nai.getPermission();
                item.icon = nai.getIcon();
            }else if(o instanceof NavbarProfileItem npi) {
                item.target = "NavbarProfileItem";
                item.name = npi.getName();
                item.link = npi.getUrl();
            }else if(o instanceof NavbarSettingsItem nsi) {
                item.target = "NavbarSettingsItem";
                item.name = nsi.getName();
                item.link = nsi.getUrl();
                item.icon = nsi.getIcon();
            }
            navItems.add(item);
        }

        shiina.data.put("navItems", navItems);
        return renderTemplate("ap/system/nav.html", shiina, res, req);
    }

    @Data
    public class SystemNavItem {
        public String target;
        public String name;
        public String link;
        public String icon = null;
        public Boolean auth = null;
        public String privilege = null;
    }
}
