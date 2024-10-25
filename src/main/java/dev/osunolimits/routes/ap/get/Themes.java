package dev.osunolimits.routes.ap.get;

import dev.osunolimits.modules.Shiina;
import dev.osunolimits.modules.ShiinaRoute;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import dev.osunolimits.modules.ThemeLoader;
import dev.osunolimits.utils.osu.PermissionHelper;
import spark.Request;
import spark.Response;

public class Themes extends Shiina {


    @Override
    public Object handle(Request req, Response res) throws Exception {
        ShiinaRequest shiina = new ShiinaRoute().handle(req, res);
        shiina.data.put("actNav", 12);

        if(!shiina.loggedIn) {
            res.redirect("/login");
            return null;
        }

        if(!PermissionHelper.hasPrivileges(shiina.user.priv, PermissionHelper.Privileges.ADMINISTRATOR)) {
            res.redirect("/");
            return null;
        }

        shiina.data.put("themes", ThemeLoader.themes);
        return renderTemplate("ap/themes.html", shiina, res, req);
    }
    
}
