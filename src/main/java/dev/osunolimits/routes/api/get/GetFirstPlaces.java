package dev.osunolimits.routes.api.get;

import java.sql.ResultSet;
import java.util.ArrayList;

import com.google.gson.Gson;

import dev.osunolimits.modules.Shiina;
import dev.osunolimits.modules.ShiinaRoute;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import dev.osunolimits.utils.Validation;
import lombok.Data;
import spark.Request;
import spark.Response;

public class GetFirstPlaces extends Shiina {

    private final String GET_FIRST_PLACES = "SELECT s.userid, s.map_md5, m.id AS map_id, s.score AS max_score, s.pp, s.acc, s.max_combo, s.n300, s.n100, s.n50, s.nmiss, s.ngeki, s.nkatu, s.grade, s.play_time FROM scores s JOIN (SELECT map_md5, MAX(score) AS max_score FROM scores WHERE status = 2 GROUP BY map_md5) AS max_scores ON s.map_md5 = max_scores.map_md5 AND s.score = max_scores.max_score JOIN maps m ON s.map_md5 = m.md5 WHERE s.userid = ? AND s.status = 2 AND s.mode = ? ORDER BY s.play_time DESC LIMIT ? OFFSET ?;";

    @Override
    public Object handle(Request req, Response res) throws Exception {
        ShiinaRequest shiina = new ShiinaRoute().handle(req, res);

        int mode = 0;
        if (req.queryParams("mode") != null && Validation.isNumeric(req.queryParams("mode"))) {
            mode = Integer.parseInt(req.queryParams("mode"));
        }

        Integer id = null;
        if (req.queryParams("id") != null && Validation.isNumeric(req.queryParams("id"))) {
            id = Integer.parseInt(req.queryParams("id"));
        }

        Integer offset = 0;
        if (req.queryParams("offset") != null && Validation.isNumeric(req.queryParams("offset"))) {
            id = Integer.parseInt(req.queryParams("offset"));
        }

        if(id == null) {
            return "Invalid ID";
        }
        ArrayList<FirstPlace> firstPlaces = new ArrayList<>();
        FirstPlacesResponse response = new FirstPlacesResponse();
        boolean hasNextPage = false;
        ResultSet firstPlacesQuery = shiina.mysql.Query(GET_FIRST_PLACES, id,mode, 11, offset);
        while (firstPlacesQuery.next()) {
            FirstPlace firstPlace = new FirstPlace();
            firstPlace.setUser_id(firstPlacesQuery.getInt("userid"));
            firstPlace.setMap_md5(firstPlacesQuery.getString("map_md5"));
            firstPlace.setMap_id(firstPlacesQuery.getInt("map_id"));
            firstPlace.setMax_score(firstPlacesQuery.getInt("max_score"));
            firstPlace.setPp(firstPlacesQuery.getInt("pp"));
            firstPlace.setAcc(firstPlacesQuery.getInt("acc"));
            firstPlace.setMax_combo(firstPlacesQuery.getInt("max_combo"));
            firstPlace.setN300(firstPlacesQuery.getInt("n300"));
            firstPlace.setN100(firstPlacesQuery.getInt("n100"));
            firstPlace.setN50(firstPlacesQuery.getInt("n50"));
            firstPlace.setNmiss(firstPlacesQuery.getInt("nmiss"));
            firstPlace.setNgeki(firstPlacesQuery.getInt("ngeki"));
            firstPlace.setNkatu(firstPlacesQuery.getInt("nkatu"));
            firstPlace.setGrade(firstPlacesQuery.getString("grade"));
            firstPlace.setPlay_time(firstPlacesQuery.getString("play_time"));
            firstPlaces.add(firstPlace);
            // check if it is 11 to see if there is a next page
            if (firstPlaces.size() == 11) {
                hasNextPage = true;
                firstPlaces.remove(10);
            }
            
           
        }
        response.setFirstPlaces(firstPlaces.toArray(new FirstPlace[0]));
        response.setStatus("success");
        response.setHasNextPage(hasNextPage);


        // Convert response to json with gson
        res.type("application/json");
        Gson gson = new Gson();

        return gson.toJson(response);
    }

    @Data
    public class FirstPlacesResponse {
        private String status;
        private boolean hasNextPage;
        private FirstPlace[] firstPlaces;
    }

    @Data
    public class FirstPlace {
        private int user_id;
        private String map_md5;
        private int map_id;
        private int max_score;
        private int pp;
        private int acc;
        private int max_combo;
        private int n300;
        private int n100;
        private int n50;
        private int nmiss;
        private int ngeki;
        private int nkatu;
        private String grade;
        private String play_time;
    }

}

