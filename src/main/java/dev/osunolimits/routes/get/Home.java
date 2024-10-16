package dev.osunolimits.routes.get;

import java.sql.ResultSet;

import dev.osunolimits.api.BanchoStats;
import dev.osunolimits.api.BanchoStats.CustomCountResponse;
import dev.osunolimits.main.App;
import dev.osunolimits.modules.Shiina;
import dev.osunolimits.modules.ShiinaRoute;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import spark.Request;
import spark.Response;

public class Home extends Shiina {

    private final String STATS_SQL = "SELECT (SELECT COUNT(`id`) FROM `scores`) AS `scores`,(SELECT COUNT(`id`) FROM `maps`) AS `maps`, (SELECT COUNT(`id`) FROM `clans`) AS `clans`;";
    private final String ADD_STATS_SQL = "SELECT (SELECT COUNT(`id`) FROM `scores`) AS `scores`, (SELECT COUNT(`id`) FROM `ingame_logins`) AS `total_logins`, (SELECT COUNT(`id`) FROM `ingame_logins` WHERE DATE(`datetime`) = CURDATE()) AS `logins_today`, (SELECT COUNT(`id`) FROM `maps`) AS `maps`, (SELECT COUNT(`id`) FROM `clans`) AS `clans`, (SELECT MAX(`datetime`) FROM `startups`) AS `startup`, (SELECT COUNT(`id`) FROM `users` WHERE (`priv` & (4096 | 8192 | 16384 | 2048 | 1024)) > 0) AS `staff_count`, (SELECT COUNT(`id`) FROM `users` WHERE (`priv` & 32768) > 0) AS `banned_count`;";
    @Override
    public Object handle(Request req, Response res) throws Exception {
        ShiinaRequest shiina = new ShiinaRoute().handle(req, res);
        shiina.data.put("actNav", 1);
        BanchoStats banchoStats = new BanchoStats();
        shiina.data.put("playerCount", banchoStats.getPlayerCount());
        CustomCountResponse customCountResponse = banchoStats.new CustomCountResponse();
        ResultSet statsResultSet; 
        Boolean showAdditionalStat = (Boolean)App.customization.get("showAdditionalStats");
        if(showAdditionalStat) {
            statsResultSet = shiina.mysql.Query(ADD_STATS_SQL);
        } else {
            statsResultSet = shiina.mysql.Query(STATS_SQL);
        }


        if (statsResultSet.next()) {
            customCountResponse.setPlays(statsResultSet.getInt("scores"));
            customCountResponse.setBeatmaps(statsResultSet.getInt("maps"));
            customCountResponse.setClans(statsResultSet.getInt("clans"));

            if(showAdditionalStat) {
                customCountResponse.setTotalLogins(statsResultSet.getInt("total_logins"));
                customCountResponse.setLoginsToday(statsResultSet.getInt("logins_today"));
                customCountResponse.setStaff(statsResultSet.getInt("staff_count"));
                customCountResponse.setRestricted(statsResultSet.getInt("banned_count"));
                shiina.data.put("startup", statsResultSet.getString("startup"));
            }
        }
        shiina.data.put("customCount", customCountResponse);
        return renderTemplate("home.html", shiina, res, req);
    }
    
}
