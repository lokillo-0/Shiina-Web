package dev.osunolimits.modules;

import java.util.HashMap;

import com.google.gson.Gson;

import dev.osunolimits.common.Database;
import dev.osunolimits.common.MySQL;
import dev.osunolimits.main.App;
import dev.osunolimits.utils.Auth;
import spark.Request;
import spark.Response;

public class ShiinaRoute {

    public class ShiinaRequest {
        public MySQL mysql;
        public HashMap<String, Object> data = new HashMap<>();
        public boolean loggedIn = false;
        public Auth.User user;

    }

    public ShiinaRequest handle(Request req, Response res) throws Exception {
        ShiinaRequest request = new ShiinaRequest();
        request.mysql = Database.getConnection();
        if(req.cookie("shiina") != null) {
            String userJson = App.jedisPool.get("shiina:" + req.cookie("shiina"));
            Gson gson = new Gson();
            Auth.User user = gson.fromJson(userJson, Auth.User.class);
            if(user != null) {
                request.loggedIn = true;
                request.user = user;
                request.data.put("user", user);
            }
        }
        request.data.put("assetsUrl", App.env.get("ASSETSURL"));
        request.data.put("apiUrlPub", App.env.get("APIURLPUBLIC"));
        request.data.put("apiUrl", App.env.get("APIURL"));
        request.data.put("c", App.customization);
        request.data.put("turnstilePublic", App.env.get("TURNSTILE_KEY"));
        request.data.put("avatarServer", App.env.get("AVATARSRV"));
        request.data.put("loggedIn", request.loggedIn);
        return request;
    }
    
}
