package dev.osunolimits.routes.get;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import dev.osunolimits.models.FullBeatmap;
import dev.osunolimits.modules.Shiina;
import dev.osunolimits.modules.ShiinaRoute;
import dev.osunolimits.modules.ShiinaRoute.ShiinaRequest;
import dev.osunolimits.utils.Validation;
import dev.osunolimits.utils.osu.OsuConverter;
import spark.Request;
import spark.Response;

public class Beatmap extends Shiina {


    @Override
    public Object handle(Request req, Response res) throws Exception {
        ShiinaRequest shiina = new ShiinaRoute().handle(req, res);
        shiina.data.put("actNav", 3);

        Integer id = null;
        if (req.params("id") != null && Validation.isNumeric(req.params("id"))) {
            id = Integer.parseInt(req.params("id"));
        }

        if(id == null) {
            return notFound(res, shiina);
        }

        Integer mode = 0;
        if (req.queryParams("mode") != null && Validation.isNumeric(req.queryParams("mode"))) {
            mode = Integer.parseInt(req.queryParams("mode"));
        }

        FullBeatmap fullBeatmap = new FullBeatmap();
        ArrayList<FullBeatmap.MapDiff> diffs = new ArrayList<>();

        ResultSet queryDiffs = shiina.mysql.Query("SELECT `id`, `version` FROM `maps` WHERE set_id = ( SELECT set_id FROM `maps` WHERE id = ? ) ORDER BY `maps`.`diff` ASC;", id);
        while (queryDiffs.next()) {
            FullBeatmap.MapDiff diff = new FullBeatmap().new MapDiff();
            diff.setId(queryDiffs.getInt("id"));
            diff.setVersion(queryDiffs.getString("version"));
            diffs.add(diff);
        }

        fullBeatmap.setDiffs(diffs.toArray(new FullBeatmap.MapDiff[0]));

        if(diffs.size() == 0) {
            return notFound(res, shiina);
        }

        ResultSet beatmapQuery = shiina.mysql.Query("SELECT `md5`, `set_id`, `title`, `status`, `artist`, `version`, `creator`, `last_update`, `total_length`, `max_combo`, `plays`, `passes`, `mode`, `bpm`, `cs`, `ar`, `od`, `hp`, `diff` FROM `maps` WHERE `id` = ?", id);

        if(!beatmapQuery.next()) {
            return notFound(res, shiina);
        }

        fullBeatmap.setMd5(beatmapQuery.getString("md5"));
        fullBeatmap.setSetId(beatmapQuery.getInt("set_id"));
        fullBeatmap.setStatus(beatmapQuery.getInt("status"));
        fullBeatmap.setTitle(beatmapQuery.getString("title"));
        fullBeatmap.setArtist(beatmapQuery.getString("artist"));
        fullBeatmap.setVersion(beatmapQuery.getString("version"));
        fullBeatmap.setCreator(beatmapQuery.getString("creator"));
        fullBeatmap.setLastUpdate(beatmapQuery.getString("last_update"));
        fullBeatmap.setTotalLength(beatmapQuery.getInt("total_length"));
        fullBeatmap.setMaxCombo(beatmapQuery.getInt("max_combo"));
        fullBeatmap.setPlays(beatmapQuery.getInt("plays"));
        fullBeatmap.setPasses(beatmapQuery.getInt("passes"));
        fullBeatmap.setMode(beatmapQuery.getInt("mode"));
        fullBeatmap.setBpm(beatmapQuery.getDouble("bpm"));
        fullBeatmap.setCs(beatmapQuery.getDouble("cs"));
        fullBeatmap.setAr(beatmapQuery.getDouble("ar"));
        fullBeatmap.setOd(beatmapQuery.getDouble("od"));
        fullBeatmap.setHp(beatmapQuery.getDouble("hp"));
        fullBeatmap.setDiff(beatmapQuery.getDouble("diff"));
        
        if(fullBeatmap.getMode() != 0 && !(mode >= 4)) {
            mode = fullBeatmap.getMode();
        }
        List<FullBeatmap.BeatmapScore> scores = new ArrayList<>();

        ResultSet scoreQuery = shiina.mysql.Query("SELECT s.id, s.pp, s.grade, s.play_time, s.userid, s.mods, u.name, u.country, u.priv FROM scores AS `s` LEFT JOIN users AS `u` ON s.userid = u.id WHERE s.map_md5 = ? AND s.pp = (SELECT MAX(pp) FROM scores WHERE map_md5 = s.map_md5 AND s.status = 2 AND userid = s.userid) AND s.mode = ? ORDER BY `s`.`pp` DESC, `s`.`play_time`  ASC LIMIT 0, 50;", fullBeatmap.getMd5(),mode);

        while(scoreQuery.next()) {
            FullBeatmap.BeatmapScore score = new FullBeatmap().new BeatmapScore();
            // Get priv and clan?? 
            score.setId(scoreQuery.getInt("id"));
            score.setPp(scoreQuery.getInt("pp"));
            score.setGrade(scoreQuery.getString("grade"));
            score.setPlayTime(scoreQuery.getString("play_time"));
            score.setMods(OsuConverter.convertMods(scoreQuery.getInt("mods")));
            score.setUserId(scoreQuery.getInt("userid"));
            score.setName(scoreQuery.getString("name"));
            score.setCountry(scoreQuery.getString("country"));
            scores.add(score);
        }

        fullBeatmap.setScores(scores.toArray(new FullBeatmap.BeatmapScore[0]));
        shiina.data.put("beatmap", fullBeatmap); 
        shiina.data.put("id", id);
        shiina.data.put("mode", mode);

        return renderTemplate("beatmap.html", shiina, res, req);
    }
    
}
