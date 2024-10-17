package dev.osunolimits.routes.get;

import dev.osunolimits.api.ScoreQuery;
import dev.osunolimits.modules.Shiina;
import dev.osunolimits.modules.ShiinaRoute;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import dev.osunolimits.utils.Validation;
import spark.Request;
import spark.Response;

public class Score extends Shiina {


    @Override
    public Object handle(Request req, Response res) throws Exception {
        ShiinaRequest shiina = new ShiinaRoute().handle(req, res);
        shiina.data.put("actNav", 0);

        Integer id = null;
        if (req.params("id") != null && Validation.isNumeric(req.params("id"))) {
            id = Integer.parseInt(req.params("id"));
        }

        if(id == null) {
            return null;
        }

        ScoreQuery scoreQuery = new ScoreQuery(shiina.mysql);
        Object o = scoreQuery.getScore(id);
        if(o == null) {
            return null;
        }

        shiina.data.put("score", o);

        
        return renderTemplate("score.html", shiina, res, req);
    }
    
}
