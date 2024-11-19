package dev.osunolimits.routes.api.get;

import okhttp3.OkHttpClient;

import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;

public class GetBmThumbnail implements Route {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3";
    public static final HashMap<String, byte[]> thumbnailCache = new HashMap<>();
    private static final OkHttpClient client = new OkHttpClient();

    @Override
    public Object handle(Request req, Response res) throws Exception {
        String setId = req.queryParams("setId");
        if (setId == null) return "Invalid set ID";

        if (thumbnailCache.containsKey(setId)) {
            res.type("image/jpeg");
            return thumbnailCache.get(setId);
        }

        String reqString = "https://assets.ppy.sh/beatmaps/" + setId + "/covers/card.jpg";
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(reqString)
                .header("User-Agent", USER_AGENT)
                .build();

        okhttp3.Response response = client.newCall(request).execute();
        byte[] imageBytes;
        if (response.code() == 200) {
            imageBytes = response.body().bytes();
        } else {
            java.nio.file.Path imagePath = java.nio.file.Paths.get("static/img/nobeatmapicon.png");
            imageBytes = java.nio.file.Files.readAllBytes(imagePath);
        }
        thumbnailCache.put(setId, imageBytes);

        res.type("image/jpeg");
        return imageBytes;
    }
}
