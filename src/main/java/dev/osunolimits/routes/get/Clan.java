package dev.osunolimits.routes.get;

import dev.osunolimits.api.ClanQuery;
import dev.osunolimits.api.ClanQuery.SingleClanResponse;
import dev.osunolimits.modules.Shiina;
import dev.osunolimits.modules.ShiinaRoute;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import dev.osunolimits.utils.Validation;
import spark.Request;
import spark.Response;

public class Clan extends Shiina {


    @Override
    public Object handle(Request req, Response res) throws Exception {
        ShiinaRequest shiina = new ShiinaRoute().handle(req, res);
        shiina.data.put("actNav", 0);
        shiina.data.put("actNav", 4);

        int mode = 0;
        if (req.queryParams("mode") != null && Validation.isNumeric(req.queryParams("mode"))) {
            mode = Integer.parseInt(req.queryParams("mode"));
        }

        Integer id = null;
        if (req.params("id") != null && Validation.isNumeric(req.params("id"))) {
            id = Integer.parseInt(req.params("id"));
        }

        if(id == null) {
            return null;
        }

        shiina.data.put("mode", mode);

        ClanQuery clanQuery = new ClanQuery(shiina.mysql);

        SingleClanResponse response = clanQuery.getClan(mode, id);

        if(response == null) {
            return null;
        }

        shiina.data.put("clan", response);
        shiina.data.put("members", clanQuery.getMembers(id));
        
        return renderTemplate("clan.html", shiina, res, req);
    }
    
}
