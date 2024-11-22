package dev.osunolimits.routes.get.user;

import java.sql.ResultSet;
import java.util.ArrayList;

import dev.osunolimits.modules.Shiina;
import dev.osunolimits.modules.ShiinaRoute;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import lombok.Data;
import spark.Request;
import spark.Response;

public class Relations extends Shiina {

    private final String RELATION_QUERY = "SELECT r.`user2` AS `id`, u.`latest_activity`, u.name, CASE WHEN EXISTS ( SELECT 1 FROM `relationships` r2 WHERE r2.`type` = 'friend' AND r2.`user1` = r.`user2` AND r2.`user2` = r.`user1` ) THEN 'mutual' ELSE \"friend\" END AS `relationship_status` FROM `relationships` r JOIN `users` u ON r.`user2` = u.`id` WHERE r.`type` = 'friend' AND (r.`user1` = ? OR r.`user2` = ?) AND r.`user1` < r.`user2` ORDER BY `relationship_status` DESC;";

    @Data
    public class Relation {
        public int id;
        public String name;
        public long latest_activity;
        public String relationship_status;
    }

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

        ResultSet relations = shiina.mysql.Query(RELATION_QUERY, shiina.user.id, shiina.user.id);
        ArrayList<Relation> relationsList = new ArrayList<>();
        while(relations.next()) {
            Relation relation = new Relation();
            relation.id = relations.getInt("id");
            relation.name = relations.getString("name");
            relation.latest_activity = relations.getLong("latest_activity");
            relation.relationship_status = relations.getString("relationship_status");
            relationsList.add(relation);
        }

        shiina.data.put("relations", relationsList);
        return renderTemplate("user/relations.html", shiina, res, req);
    }
    
}
