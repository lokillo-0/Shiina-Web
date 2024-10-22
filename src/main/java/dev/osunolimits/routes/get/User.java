package dev.osunolimits.routes.get;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import dev.osunolimits.api.UserQuery;
import dev.osunolimits.api.UserStatusQuery;
import dev.osunolimits.models.FullUser;
import dev.osunolimits.models.UserStatus;
import dev.osunolimits.models.FullUser.Player;
import dev.osunolimits.modules.Shiina;
import dev.osunolimits.modules.ShiinaRoute;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import dev.osunolimits.utils.LevelCalculator;
import dev.osunolimits.utils.OsuConverter;
import dev.osunolimits.utils.Validation;
import lombok.Data;
import spark.Request;
import spark.Response;

public class User extends Shiina {

    private final String ACH_QUERY = "SELECT `achievements`.`file`, `achievements`.`name`, `achievements`.`desc` FROM `user_achievements` LEFT JOIN `achievements` ON `user_achievements`.`achid` = `achievements`.`id` WHERE `user_achievements`.`userid` = ? AND (`achievements`.`file` LIKE ? OR `achievements`.`file` LIKE 'all%');";


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

        Player player = user.getPlayer();

        if(player == null) return null;

        UserStatusQuery userStatusQuery = new UserStatusQuery();
        UserStatus userStatus = userStatusQuery.getUserStatus(id);
        
        LinkedHashMap<String, Integer> userStats = new LinkedHashMap<>();
        ResultSet playCountGraphRs = shiina.mysql.Query("WITH RECURSIVE month_list AS ( SELECT DATE_FORMAT(DATE_SUB(MIN(play_time), INTERVAL 1 MONTH), '%Y-%m-01') AS month FROM scores WHERE userid = 391 UNION ALL SELECT DATE_ADD(month, INTERVAL 1 MONTH) FROM month_list WHERE month < DATE_FORMAT(CURRENT_DATE, '%Y-%m-01') ) SELECT ml.month, COALESCE(COUNT(s.play_time), 0) AS play_count FROM month_list ml LEFT JOIN scores s ON DATE_FORMAT(s.play_time, '%Y-%m') = DATE_FORMAT(ml.month, '%Y-%m') AND s.userid = ? AND s.`mode` = ? GROUP BY ml.month ORDER BY ml.month ASC;", id,mode);
        while (playCountGraphRs.next()) {
            userStats.put(playCountGraphRs.getString("month"), playCountGraphRs.getInt("play_count"));
        }

        ResultSet achRs = shiina.mysql.Query(ACH_QUERY, id, OsuConverter.convertModeBackNoRx(String.valueOf(mode)).toLowerCase() + "%");
        ArrayList<Achievements> achievements = new ArrayList<>();
        while (achRs.next()) {
            Achievements ach = new Achievements();
            ach.setFile(achRs.getString("file"));
            ach.setName(achRs.getString("name"));
            ach.setDesc(achRs.getString("desc"));
            achievements.add(ach);
            
        }

        shiina.data.put("achievements", achievements);
        shiina.data.put("id", id);
        shiina.data.put("level", LevelCalculator.getLevelPrecise(user.getPlayer().getStats().get(String.valueOf(mode)).getTscore()));
        shiina.data.put("levelProgress", LevelCalculator.getPercentageToNextLevel(user.getPlayer().getStats().get(String.valueOf(mode)).getTscore()));
        shiina.data.put("playCountGraph", userStats);
        shiina.data.put("u", user);
        shiina.data.put("mode", mode);
        shiina.data.put("status", userStatus);
        return renderTemplate("user.html", shiina, res, req);
    }

    @Data
    public class Achievements {
        private String file;
        private String name;
        private String desc;
    }
    
}
