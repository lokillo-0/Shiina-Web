package dev.osunolimits.models;

import java.util.Map;

import lombok.Data;

@Data
public class FullUser {

    private String status;
    private Player player;

    @Data
    public class Player {
        private UserInfo info;
        private Map<String, UserStats> stats;
    }

    @Data
    public class UserStats {
        private int id;
        private int mode;
        private int tscore;
        private int rscore;
        private int pp;
        private int plays;
        private int playtime;
        private int acc;
        private int maxCombo;
        private int totalHits;
        private int replayViews;
        private int xhCount;
        private int xCount;
        private int shCount;
        private int sCount;
        private int aCount;
        private int rank;
        private int countryRank;
    }

    @Data
    public class UserInfo {
        private int id;
        private String name;
        private String safeName;
        private int priv;
        private String country;
        private int silenceEnd;
        private int donorEnd;
        private int creationTime;
        private int latestActivity;
        private int clanId;
        private int clanPriv;
        private int preferredMode;
        private int playStyle;
        private String customBadgeName;
        private String customBadgeIcon;
        private String userpageContent;
    }

}
