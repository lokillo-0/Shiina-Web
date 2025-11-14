package dev.osunolimits.routes.api.get.auth;

import dev.osunolimits.modules.Shiina;
import dev.osunolimits.modules.ShiinaRoute;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import spark.Request;
import spark.Response;

public class HandleClanDisband extends Shiina {

    @Override
    public Object handle(Request req, Response res) throws Exception {
        ShiinaRequest shiina = new ShiinaRoute().handle(req, res);

        if(shiina.user == null) {
            return notFound(res, shiina);
        }

        var clanResult = shiina.mysql.Query("SELECT `clan_id`, `clan_priv` FROM `users` WHERE `id` = ?", shiina.user.id);
        if(!clanResult.next()) {
            return raw(res, shiina, "error");
        }

        if(clanResult.getInt("clan_id") == 0 || clanResult.getInt("clan_priv") != 3) {
            return raw(res, shiina, "no clan or no leader");
        }

        int clanId = clanResult.getInt("clan_id");

        shiina.mysql.Exec("UPDATE `users` SET `clan_id`=0,`clan_priv`=0 WHERE `clan_id` = ?", clanId);
        shiina.mysql.Exec("DELETE FROM `clans` WHERE `id` = ?", clanId);
        shiina.mysql.Exec("DELETE FROM `sh_clan_denied` WHERE `clanid` = ?", clanId);
        shiina.mysql.Exec("DELETE FROM `sh_clan_pending` WHERE `clanid` = ?", clanId);
        
        return redirect(res, shiina, "/clans");
    }
}
