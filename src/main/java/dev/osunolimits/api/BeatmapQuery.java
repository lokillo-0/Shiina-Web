package dev.osunolimits.api;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import dev.osunolimits.common.APIRequest;
import dev.osunolimits.main.App;
import dev.osunolimits.utils.CacheInterceptor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BeatmapQuery {

    public class BeatmapResponse {
        @Getter @Setter
        private String status;

        @Getter @Setter
        private BeatmapItem[] data;

        @Getter @Setter
        private BeatmapMeta meta;
    }

    @Data
    public class BeatmapItem {
        private int id;
        private String server;
        private int set_id;
        private int status;
        private String md5;
        private String artist;
        private String title;
        private String version;
        private String creator;
        private String filename;
        private String last_update;
        private int totalLength;
        private int maxCombo;
        private boolean frozen;
        private int plays;
        private int passes;
        private int mode;
        private int bpm;
        private float cs;
        private float ar;
        private float od;
        private float hp;
        private float diff;
    }

    public class BeatmapMeta {
        @Getter @Setter
        private int total;
        @Getter @Setter
        private int page;
        @Getter @Setter
        private int page_size;
    }

    int maxSize;

    private static OkHttpClient client;
    public BeatmapQuery() {
        BeatmapQuery.client = new OkHttpClient.Builder()
        .addNetworkInterceptor(new CacheInterceptor(30, TimeUnit.MINUTES))
        .readTimeout(10, TimeUnit.SECONDS)
        .cache(new Cache(new File(".cache/beatmaps"), 100L * 1024L * 1024L))
        .connectionPool(new ConnectionPool(200, 10, TimeUnit.SECONDS)).build(); 
    }

    private int parameter = 0;

    public String getParameter() {
        if (parameter == 0) {
            parameter++;
            return "?";
        } else {
            return "&";
        }
    }

    public BeatmapResponse getBeatmaps(int page, int pageSize, int status, Optional<String> artist, Optional<String> creator, int mode) {
        String url = "/v2/maps";
        url += getParameter() + "page=" + page;
        url += getParameter() + "page_size=" + pageSize;

        if (status != 999) {
            url += getParameter() + "status=" + status;
        }
        // Use isPresent() to check if Optional has a value
        if (artist.isPresent()) {
            url += getParameter() + "artist=" + artist.get();
        }
        // Use isPresent() to check if Optional has a value
        if (creator.isPresent()) {
            url += getParameter() + "creator=" + creator.get();
        }
        if (mode != 999) {
            url += getParameter() + "mode=" + mode;
        }

        Request request = APIRequest.build(url);

        try {
            Response response = client.newCall(request).execute();
            JsonElement element = JsonParser.parseString(response.body().string());
            BeatmapResponse beatmapResponse = new Gson().fromJson(element, BeatmapResponse.class);
            return beatmapResponse;

        } catch (Exception e) {
            App.log.error("Failed to get Beatmaps", e);
            return null;
        }
    }
}
