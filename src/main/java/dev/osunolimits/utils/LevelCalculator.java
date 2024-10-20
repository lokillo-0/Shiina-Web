package dev.osunolimits.utils;

public class LevelCalculator {
    private static final long[] LEVEL_GRAPH = {
        0L,
        30000L,
        130000L,
        340000L,
        700000L,
        1250000L,
        2030000L,
        3080000L,
        4440000L,
        6150000L,
        8250000L,
        10780000L,
        13780000L,
        17290000L,
        21350000L,
        26000000L,
        31280000L,
        37230000L,
        43890000L,
        51300000L,
        59500000L,
        68530000L,
        78430000L,
        89240000L,
        101000000L,
        113750000L,
        127530000L,
        142380000L,
        158340000L,
        175450000L,
        193750000L,
        213280000L,
        234080000L,
        256190000L,
        279650000L,
        304500000L,
        330780000L,
        358530000L,
        387790000L,
        418600000L,
        451000000L,
        485030000L,
        520730000L,
        558140000L,
        597300000L,
        638250000L,
        681030000L,
        725680000L,
        772240000L,
        820750000L,
        871250000L,
        923780000L,
        978380000L,
        1035090000L,
        1093950000L,
        1155000000L,
        1218280000L,
        1283830000L,
        1351690001L,
        1421900001L,
        1494500002L,
        1569530004L,
        1647030007L,
        1727040013L,
        1809600024L,
        1894750043L,
        1982530077L,
        2072980138L,
        2166140248L,
        2262050446L,
        2360750803L,
        2462281446L,
        2566682603L,
        2673994685L,
        2784258433L,
        2897515180L,
        3013807324L,
        3133179183L,
        3255678529L,
        3381359353L,
        3510286835L,
        3642546304L,
        3778259346L,
        3917612824L,
        4060911082L,
        4208669948L,
        4361785907L,
        4521840633L,
        4691649139L,
        4876246450L,
        5084663609L,
        5333124496L,
        5650800094L,
        6090166168L,
        6745647103L,
        7787174786L,
        9520594614L,
        12496396305L,
        17705429349L,
        26931190829L
    };

    public static int getLevel(long xp) {
        if (xp < 0) return 1; // Starting level
        if (xp >= LEVEL_GRAPH[LEVEL_GRAPH.length - 1]) return 100; // Max level

        for (int i = 0; i < LEVEL_GRAPH.length; i++) {
            if (xp < LEVEL_GRAPH[i]) {
                return i;
            }
        }
        return 1; // Default case
    }

    public static double getPercentageToNextLevel(long xp) {
        int level = getLevel(xp);
        long currentLevelXP = LEVEL_GRAPH[level - 1];
        long nextLevelXP = (level < LEVEL_GRAPH.length) ? LEVEL_GRAPH[level] : Long.MAX_VALUE;

        // Calculate percentage to next level
        return ((double) (xp - currentLevelXP) / (nextLevelXP - currentLevelXP)) * 100;
    }
}
