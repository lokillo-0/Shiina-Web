package dev.osunolimits.routes.get.ap;

import dev.osunolimits.modules.Shiina;
import dev.osunolimits.modules.ShiinaRoute;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import dev.osunolimits.utils.osu.PermissionHelper;
import spark.Request;
import spark.Response;

public class Start extends Shiina {


    @Override
    public Object handle(Request req, Response res) throws Exception {
        ShiinaRequest shiina = new ShiinaRoute().handle(req, res);
        shiina.data.put("actNav", 1);

        if(!shiina.loggedIn) {
            res.redirect("/login");
            return null;
        }

        if(!PermissionHelper.hasPrivileges(shiina.user.priv, PermissionHelper.Privileges.STAFF)) {
            res.redirect("/");
            return null;
        }
        
        return renderTemplate("ap/start.html", shiina, res, req);
    }
    
}
