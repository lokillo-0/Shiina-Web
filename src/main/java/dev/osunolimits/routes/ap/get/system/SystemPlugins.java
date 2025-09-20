package dev.osunolimits.routes.ap.get.system;

import dev.osunolimits.modules.Shiina;
import dev.osunolimits.modules.ShiinaRoute;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import dev.osunolimits.plugins.PluginLoader;
import dev.osunolimits.utils.osu.PermissionHelper;
import spark.Request;
import spark.Response;

public class SystemPlugins extends Shiina {

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

        shiina.data.put("plugins", PluginLoader.getLoadedPluginMetadata());
        return renderTemplate("ap/system/plugins.html", shiina, res, req);
    }
}
