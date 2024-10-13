package dev.osunolimits.routes;

import java.sql.ResultSet;

import dev.osunolimits.api.BanchoStats;
import dev.osunolimits.api.BanchoStats.CustomCountResponse;
import dev.osunolimits.modules.Shiina;
import dev.osunolimits.modules.ShiinaRoute;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import spark.Request;
import spark.Response;

public class Home extends Shiina {

    private final String STATS_SQL = "SELECT (SELECT COUNT(`id`) FROM `scores`) AS `scores`,(SELECT COUNT(`id`) FROM `maps`) AS `maps`, (SELECT COUNT(`id`) FROM `clans`) AS `clans`;";

    @Override
    public Object handle(Request req, Response res) throws Exception {
        ShiinaRequest shiina = new ShiinaRoute().handle(req, res);
        shiina.data.put("actNav", 1);
        BanchoStats banchoStats = new BanchoStats();
        shiina.data.put("playerCount", banchoStats.getPlayerCount());
        CustomCountResponse customCountResponse = banchoStats.new CustomCountResponse();
        ResultSet statsResultSet = shiina.mysql.Query(STATS_SQL);
        if (statsResultSet.next()) {
            customCountResponse.setPlays(statsResultSet.getInt("scores"));
            customCountResponse.setBeatmaps(statsResultSet.getInt("maps"));
            customCountResponse.setClans(statsResultSet.getInt("clans"));
        }
        shiina.data.put("customCount", customCountResponse);
        return renderTemplate("home.html", shiina, res, req);
    }
    
}
