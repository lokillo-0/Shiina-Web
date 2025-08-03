package dev.osunolimits.routes.post.settings.data;

import dev.osunolimits.modules.Shiina;
import dev.osunolimits.modules.ShiinaRoute;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import dev.osunolimits.modules.utils.AccountDeletion;
import spark.Request;
import spark.Response;

public class HandleAccountDeletion extends Shiina {
    @Override
    public Object handle(Request req, Response res) throws Exception {
        ShiinaRequest shiina = new ShiinaRoute().handle(req, res);

        if (!shiina.loggedIn) {
            return redirect(res, shiina, "/login?path=settings/data");
        }

        AccountDeletion deletion = new AccountDeletion(shiina.mysql);
        deletion.deleteAccount(shiina.user.id);
        res.removeCookie("shiina");
        return redirect(res, shiina, "/");
    }
}
