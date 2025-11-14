package dev.osunolimits.routes.api.get.auth;

import dev.osunolimits.modules.Shiina;
import dev.osunolimits.modules.ShiinaRoute;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import spark.Request;
import spark.Response;

public class HandleClanLeave extends Shiina {

    @Override
    public Object handle(Request req, Response res) throws Exception {
        ShiinaRequest shiina = new ShiinaRoute().handle(req, res);

        if(shiina.user == null) {
            return notFound(res, shiina);
        }

        shiina.mysql.Exec("UPDATE `users` SET `clan_id`=0,`clan_priv`=0 WHERE `id` = ?", shiina.user.id);
        
        return raw(res, shiina, "success");
    }
}
