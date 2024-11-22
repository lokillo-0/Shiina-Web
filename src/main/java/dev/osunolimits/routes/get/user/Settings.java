package dev.osunolimits.routes.get.user;

import dev.osunolimits.modules.Shiina;
import dev.osunolimits.modules.ShiinaRoute;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import spark.Request;
import spark.Response;

public class Settings extends Shiina {


    @Override
    public Object handle(Request req, Response res) throws Exception {
        ShiinaRequest shiina = new ShiinaRoute().handle(req, res);
        shiina.data.put("actNav", 0);

        if(!shiina.loggedIn) {
            res.redirect("/login");
            return notFound(res, shiina);
        }

        if(req.queryParams("info") != null) {
            shiina.data.put("info", req.queryParams("info"));
        }

        if(req.queryParams("error") != null) {
            shiina.data.put("error", req.queryParams("error"));
        }   
        
        return renderTemplate("user/settings.html", shiina, res, req);
    }
    
}
