package dev.osunolimits.api;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import dev.osunolimits.common.APIRequest;
import dev.osunolimits.main.App;
import lombok.Data;
import okhttp3.Request;
import okhttp3.Response;

public class OnlineQuery extends APIQuery {

    @Data
    public class OnlineUser {
        private int id;
        private String name;
    }

    @Data
    public class OnlineResponse {
        private String status;
        private OnlineUser[] players;
        private OnlineUser[] bots;
    }

    public OnlineQuery() {
        super("online", 2, 20);
    }

    public OnlineResponse getOnline() {
        String url = "/v1/online";
        Request request = APIRequest.build(url);

        try {
            Response response = client.newCall(request).execute();
            JsonElement element = JsonParser.parseString(response.body().string());
            OnlineResponse userResponse = new Gson().fromJson(element, OnlineResponse.class);
            return userResponse;
        } catch (Exception e) {
            App.log.error("Failed to get Online EX", e);
        }

        return null;
    }

}
