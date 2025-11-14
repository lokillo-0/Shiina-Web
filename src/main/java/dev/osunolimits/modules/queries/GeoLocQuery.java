package dev.osunolimits.modules.queries;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dev.osunolimits.main.App;
import lombok.Data;

public class GeoLocQuery {

    public final String URL = "http://ip-api.com/json/%ip%?fields=status,message,countryCode";
    public final String URL_SESSION = "http://ip-api.com/json/%ip%?fields=status,message,city,country";

    private OkHttpClient client;

    public GeoLocQuery() {
        client = new OkHttpClient.Builder().build(); 
    }

  

    public String getCountryCode(String ip) {
        
        String url;
        try {
            url = URL.replace("%ip%", java.net.URLEncoder.encode(ip, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            url = URL.replace("%ip%", ip);
        }
        Request request = new Request.Builder().url(url).build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return "XX";
            }
            String responseBody = response.body().string();
            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
            if ("success".equals(json.get("status").getAsString())) {
                return json.get("countryCode").getAsString();
            }
        } catch (IOException e) {
            App.log.error("Failed to request country code from geoloc");
        }
        return "XX"; 
    }

    public GeoLocResponse getCountryCodeSession(String ip) {
        
        String url;
        try {
            url = URL_SESSION.replace("%ip%", java.net.URLEncoder.encode(ip, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            url = URL_SESSION.replace("%ip%", ip);
        }
        Request request = new Request.Builder().url(url).build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return null;
            }
            String responseBody = response.body().string();
            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
            if ("success".equals(json.get("status").getAsString())) {
                GeoLocResponse geoLocResponse = new GeoLocResponse();
                geoLocResponse.setStatus(json.get("status").getAsString());
                geoLocResponse.setCity(json.has("city") ? json.get("city").getAsString() : "");
                geoLocResponse.setCountry(json.has("country") ? json.get("country").getAsString() : "");
                return geoLocResponse;
            }
        } catch (IOException e) {
            App.log.error("Failed to request country code from geoloc");
        }
        return null;
    }

    @Data   
    public static class GeoLocResponse {
        private String status;
        private String city;
        private String country;
    }
}
