package dev.osunolimits.routes.get.simple;

import java.sql.ResultSet;

import dev.osunolimits.main.App;
import dev.osunolimits.modules.Shiina;
import dev.osunolimits.modules.ShiinaRoute;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import dev.osunolimits.modules.monetization.MonetizationConfig;
import dev.osunolimits.modules.utils.SEOBuilder;
import spark.Request;
import spark.Response;

public class Donate extends Shiina {
    public MonetizationConfig monetizationConfig;
    private int actNav;
    public Donate(MonetizationConfig monetizationConfig, int actNav) {
        this.monetizationConfig = monetizationConfig;
        this.actNav = actNav;   
    }


    @Override
    public Object handle(Request req, Response res) throws Exception {
        ShiinaRequest shiina = new ShiinaRoute().handle(req, res);
        shiina.data.put("actNav", actNav);

        if(!monetizationConfig.ENABLED) {
            return redirect(res, shiina, "/");
        }

        if(!shiina.loggedIn) {
            return redirect(res, shiina, "/login?path=/donate");
        }

        shiina.data.put("kofiConfig", MonetizationConfig.KOFI_CONFIG);

        ResultSet donorEndRs = shiina.mysql.Query("SELECT donor_end FROM users WHERE id = ?", shiina.user.id);
        while (donorEndRs.next()) {
            shiina.data.put("donorEnd", donorEndRs.getString("donor_end"));
        }

        shiina.data.put("seo", new SEOBuilder("Donate", App.customization.get("homeDescription").toString()));
        return renderTemplate("donate.html", shiina, res, req);
    }
    
}
