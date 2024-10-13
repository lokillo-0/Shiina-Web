package dev.osunolimits.routes;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dev.osunolimits.api.LeaderboardQuery;
import dev.osunolimits.api.LeaderboardQuery.LeaderboardResponse;
import dev.osunolimits.modules.Shiina;
import dev.osunolimits.modules.ShiinaRoute;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import dev.osunolimits.utils.Validation;
import spark.Request;
import spark.Response;

public class Leaderboard extends Shiina {

    @Override
    public Object handle(Request req, Response res) throws Exception {
        ShiinaRequest shiina = new ShiinaRoute().handle(req, res);
        shiina.data.put("actNav", 2);

        LeaderboardQuery leaderboardQuery = new LeaderboardQuery();

        int page = 1;
        int offset = 0;
        if (req.queryParams("page") != null && Validation.isNumeric(req.queryParams("page"))) {
            page = Integer.parseInt(req.queryParams("page"));
        }
        if(page == 1) {
            offset = 0;
        } else {
            offset = (page - 1) * 50;
        }

        int mode = 0;
        if (req.queryParams("mode") != null && Validation.isNumeric(req.queryParams("mode"))) {
            mode = Integer.parseInt(req.queryParams("mode"));
        }

        Optional<String> country;
        if (req.queryParams("country") != null) {
            country = Optional.of(req.queryParams("country"));
        } else {
            country = Optional.empty();
        }

        String sort = "pp";
        if (req.queryParams("sort") != null) {
            sort = req.queryParams("sort");
        }

        LeaderboardResponse leaderboardResponse = leaderboardQuery.getLeaderboard(sort, mode, 50, offset, country);

        List<String> countries = new ArrayList<>();
        ResultSet countriesResultSet = shiina.mysql.Query("SELECT COUNT(`id`) AS `people`, `country` FROM `users` WHERE `id` IN ( SELECT `userid` FROM `scores` WHERE `mode` = ? AND `scores`.`status`!= 0 ) GROUP BY `country` ORDER BY `people` DESC;", String.valueOf(mode));
        while (countriesResultSet.next()) {
            countries.add(countriesResultSet.getString("country"));
        }

        shiina.data.put("countries", countries);
        shiina.data.put("leaderboard", leaderboardResponse.getLeaderboard());

        shiina.data.put("sort", sort);
        shiina.data.put("mode", mode);
        shiina.data.put("offset", offset + 1);
        shiina.data.put("country", country.orElse(null));
        shiina.data.put("page", page);
        shiina.data.put("pageSize", leaderboardResponse.getLeaderboard().length);
        return renderTemplate("leaderboard.html", shiina, res, req);
    }

}
