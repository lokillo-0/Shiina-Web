package dev.osunolimits.common;

import dev.osunolimits.main.App;
import okhttp3.Request;

public class APIRequest {
    public static Request build(String url) {
        if(Boolean.parseBoolean(App.loggerEnv.get("API_LOG")))
            App.log.info("| Bancho.py | API Request | " + App.env.get("APIURL") + url);
        return new Request.Builder()
                .url(App.env.get("APIURL") + url)
                .build();
    }
}
