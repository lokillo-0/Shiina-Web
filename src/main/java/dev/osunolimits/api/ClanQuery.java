package dev.osunolimits.api;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import dev.osunolimits.common.MySQL;
import dev.osunolimits.main.App;
import lombok.Data;

public class ClanQuery {

    private MySQL mysql;

    public ClanQuery(MySQL mysql) {
        this.mysql = mysql;
    }

    @Data
    public class ClanResponse {
        private int id;
        private String name;
        private String tag;
        private int owner;
        private int members;
        private double competitionValue;
    }

    public enum CompetitionType {
        TOTALPP,
        AVGPP,
        RANKEDSCORE,
        ACC;

        public static CompetitionType fromName(String name) {
            for (CompetitionType type : values()) {
                if (type.name().equalsIgnoreCase(name)) {
                    return type;
                }
            }
            return null;
        }
    }

    private final String GETCLAN_TOTALPP = "SELECT c.*, (SELECT COUNT(*) FROM users u WHERE u.clan_id = c.id) AS memberCount, (SELECT COALESCE(SUM(s.pp), 0) FROM users u JOIN stats s ON u.id = s.id WHERE u.clan_id = c.id AND s.mode = ?) AS totalPP FROM clans c ORDER BY `totalPP` DESC LIMIT ? OFFSET ?;";
    private final String GETCLAN_AVGPP = "SELECT c.*, (SELECT COUNT(*) FROM users u WHERE u.clan_id = c.id) AS memberCount, (SELECT COALESCE(AVG(s.pp), 0) FROM users u JOIN stats s ON u.id = s.id WHERE u.clan_id = c.id AND s.mode = ?) AS avgPP FROM clans c ORDER BY `avgPP` DESC LIMIT ? OFFSET ?;";
    private final String GETCLAN_RANKEDSCORE = "SELECT c.*, (SELECT COUNT(*) FROM users u WHERE u.clan_id = c.id) AS memberCount, (SELECT COALESCE(SUM(s.rscore), 0) FROM users u JOIN stats s ON u.id = s.id WHERE u.clan_id = c.id AND s.mode = ?) AS rankedScore FROM clans c ORDER BY `rankedScore` DESC LIMIT ? OFFSET ?;";
    private final String GETCLAN_ACC = "SELECT c.*, (SELECT COUNT(*) FROM users u WHERE u.clan_id = c.id) AS memberCount, (SELECT ROUND(AVG(s.acc), 2) FROM users u JOIN stats s ON u.id = s.id WHERE u.clan_id = c.id AND s.mode = ?) AS acc FROM clans c ORDER BY `acc` DESC LIMIT ? OFFSET ?;";

    public List<ClanResponse> getClan(CompetitionType type, int mode, int limit, int offset) {
        List<ClanResponse> responses = new ArrayList<>();
        ResultSet rs = null;
        String competitionValueName = "";

        switch (type) {
            case TOTALPP:
                rs = mysql.Query(GETCLAN_TOTALPP, String.valueOf(mode), String.valueOf(limit), String.valueOf(offset));
                competitionValueName = "totalPP";
                break;
            case AVGPP:
                rs = mysql.Query(GETCLAN_AVGPP, String.valueOf(mode), String.valueOf(limit), String.valueOf(offset));
                competitionValueName = "avgPP";
                break;
            case RANKEDSCORE:
                rs = mysql.Query(GETCLAN_RANKEDSCORE, String.valueOf(mode), String.valueOf(limit), String.valueOf(offset));
                competitionValueName = "rankedScore";
                break;
            case ACC:
                rs = mysql.Query(GETCLAN_ACC, String.valueOf(mode), String.valueOf(limit), String.valueOf(offset));
                competitionValueName = "acc";
                break;
        }
        try {
            while (rs.next()) {
                ClanResponse response = new ClanResponse();
                response.setId(rs.getInt("id"));
                response.setName(rs.getString("name"));
                response.setTag(rs.getString("tag"));
                response.setOwner(rs.getInt("owner"));
                response.setMembers(rs.getInt("memberCount"));
                response.setCompetitionValue(rs.getDouble(competitionValueName));
                responses.add(response);
            }
        } catch (Exception e) {
            App.log.error("Failed to get clan data for [" + type.name() + "]", e);

        }
        return responses;

    }
}
