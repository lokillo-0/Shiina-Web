package dev.osunolimits.routes.get;

import dev.osunolimits.api.UserQuery;
import dev.osunolimits.api.UserStatusQuery;
import dev.osunolimits.models.FullUser;
import dev.osunolimits.models.UserStatus;
import dev.osunolimits.modules.Shiina;
import dev.osunolimits.modules.ShiinaRoute;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import dev.osunolimits.utils.Validation;
import spark.Request;
import spark.Response;

public class User extends Shiina {


    @Override
    public Object handle(Request req, Response res) throws Exception {
        ShiinaRequest shiina = new ShiinaRoute().handle(req, res);
        shiina.data.put("actNav", 0);

        Integer id = null;
        if (req.params("id") != null && Validation.isNumeric(req.params("id"))) {
            id = Integer.parseInt(req.params("id"));
        }

        int mode = 0;
        if (req.queryParams("mode") != null && Validation.isNumeric(req.queryParams("mode"))) {
            mode = Integer.parseInt(req.queryParams("mode"));
        }

        if(id == null) {
            return null;
        }

        FullUser user = new UserQuery().getUser(id);
        if(user == null) {
            return null;
        }

        UserStatusQuery userStatusQuery = new UserStatusQuery();
        UserStatus userStatus = userStatusQuery.getUserStatus(id);
        


        shiina.data.put("u", user);
        shiina.data.put("mode", mode);
        shiina.data.put("status", userStatus);
        return renderTemplate("user.html", shiina, res, req);
    }
    
}
