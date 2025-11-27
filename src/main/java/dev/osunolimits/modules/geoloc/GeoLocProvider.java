package dev.osunolimits.modules.geoloc;

import lombok.Data;

public interface GeoLocProvider {
    public String getCountryCode(String ip);
    public GeoLocResponse getCountryCodeSession(String ip);

    @Data
    public static class GeoLocResponse {
        private String status;
        private String city;
        private String country;
    }
}
