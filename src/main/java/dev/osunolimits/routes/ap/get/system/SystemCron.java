package dev.osunolimits.routes.ap.get.system;

import dev.osunolimits.modules.Shiina;
import dev.osunolimits.modules.ShiinaRoute;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import dev.osunolimits.modules.cron.engine.Cron;
import dev.osunolimits.utils.osu.PermissionHelper;
import spark.Request;
import spark.Response;

public class SystemCron extends Shiina {

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

        shiina.data.put("tasks", Cron.taskMetas);
        return renderTemplate("ap/system/cron.html", shiina, res, req);
    }
}
