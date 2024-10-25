package dev.osunolimits.routes.ap.post;

import dev.osunolimits.modules.Shiina;
import dev.osunolimits.modules.ShiinaRoute;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import dev.osunolimits.modules.ThemeLoader;
import dev.osunolimits.utils.osu.PermissionHelper;
import spark.Request;
import spark.Response;

public class ChangeTheme extends Shiina {


    @Override
    public Object handle(Request req, Response res) throws Exception {
        ShiinaRequest shiina = new ShiinaRoute().handle(req, res);

        if(!shiina.loggedIn) {
            res.redirect("/login");
            return null;
        }

        if(!PermissionHelper.hasPrivileges(shiina.user.priv, PermissionHelper.Privileges.ADMINISTRATOR)) {
            res.redirect("/");
            return null;
        }

        String theme = req.queryParams("name");

        ThemeLoader.selectTheme(theme);

        res.redirect("/ap/themes");
        return null;
    }
    
}
