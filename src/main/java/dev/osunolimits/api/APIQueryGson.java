package dev.osunolimits.api;

import com.google.gson.Gson;

public class APIQueryGson extends APIQuery {

    protected Gson gson;

    public APIQueryGson() {
        super();
        this.gson = new Gson();
    }

    public APIQueryGson(String cacheDir, int minutes, int maxIdleConnection) {
        super(cacheDir, minutes, maxIdleConnection);
        this.gson = new Gson();
    }

    public Gson getGson() {
        return gson;
    }
    
}
