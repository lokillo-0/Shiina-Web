package dev.osunolimits.modules.geoloc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dev.osunolimits.main.App;
import okhttp3.Request;
import okhttp3.Response;

public class DefaultGeoLocQuery implements GeoLocProvider {

    public final String URL = "http://ip-api.com/json/%ip%?fields=status,message,countryCode";
    public final String URL_SESSION = "http://ip-api.com/json/%ip%?fields=status,message,city,country";

    public String getCountryCode(String ip) {

        String url;
        try {
            url = URL.replace("%ip%", URLEncoder.encode(ip, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            url = URL.replace("%ip%", ip);
        }
        Request request = new Request.Builder().url(url).build();

        try (Response response = App.sharedClient.newCall(request).execute()) {
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
            url = URL_SESSION.replace("%ip%", URLEncoder.encode(ip, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            url = URL_SESSION.replace("%ip%", ip);
        }
        Request request = new Request.Builder().url(url).build();

        try (Response response = App.sharedClient.newCall(request).execute()) {
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
}
