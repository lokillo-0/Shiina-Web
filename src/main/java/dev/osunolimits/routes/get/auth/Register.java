package dev.osunolimits.routes.get.auth;

import dev.osunolimits.main.App;
import dev.osunolimits.modules.Shiina;
import dev.osunolimits.modules.ShiinaRoute;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import dev.osunolimits.modules.utils.SEOBuilder;
import dev.osunolimits.routes.get.modular.ModuleRegister;
import spark.Request;
import spark.Response;

public class Register extends Shiina {


    @Override
    public Object handle(Request req, Response res) throws Exception {
        ShiinaRequest shiina = new ShiinaRoute().handle(req, res);
        shiina.data.put("actNav", 0);

        String path = req.queryParams("path");
        if (path != null && !path.isEmpty()) {
            shiina.data.put("refPath", path);
        }

        shiina.data.put("modules", ModuleRegister.getModulesRawForPage("register", req, res, shiina));

        shiina.data.put("seo", new SEOBuilder("Register", App.customization.get("homeDescription").toString()));
        return renderTemplate("register.html", shiina, res, req);
    }
    
}
