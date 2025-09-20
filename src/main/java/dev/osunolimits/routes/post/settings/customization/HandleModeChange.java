package dev.osunolimits.routes.post.settings.customization;

import dev.osunolimits.modules.Shiina;
import dev.osunolimits.modules.ShiinaRoute;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import dev.osunolimits.utils.osu.OsuConverter;
import spark.Request;
import spark.Response;

public class HandleModeChange extends Shiina {

    @Override
    public Object handle(Request req, Response res) throws Exception {
        ShiinaRequest shiina = new ShiinaRoute().handle(req, res);

        if (!shiina.loggedIn) {
            return redirect(res, shiina, "/login?path=/settings/customization");
        }

        int userId = shiina.user.id;
        String mode = req.queryParams("mode");

        if (mode == null || mode.isEmpty()) {
            return redirect(res, shiina, "/settings?error=Invalid flag");
        }

        if (!OsuConverter.checkForValidMode(mode)) {
            return redirect(res, shiina, "/settings?error=Invalid mode");
        }

        shiina.mysql.Exec("UPDATE `users` SET `preferred_mode`=? WHERE `id` = ?", mode, userId);

        return redirect(res, shiina, "/settings/customization?info=Favorite mode was changed");
    }
}
