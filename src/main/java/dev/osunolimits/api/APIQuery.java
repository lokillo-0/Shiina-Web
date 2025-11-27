package dev.osunolimits.api;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import dev.osunolimits.main.App;
import dev.osunolimits.utils.CacheInterceptor;
import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

public class APIQuery {
    protected OkHttpClient client;

    protected static Logger logger = (Logger) LoggerFactory.getLogger("APIQuery");

    public APIQuery(OkHttpClient client) {
        this.client = client;
    }

    public APIQuery() {
        this.client = App.sharedClient;
    }

    protected APIQuery(String cacheDir, int minutes, int maxIdleConnection) {
        this.client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new CacheInterceptor(minutes, TimeUnit.MINUTES))
                .cache(new Cache(new File(".cache/" + cacheDir), 100L * 1024L * 1024L))
                .connectionPool(new ConnectionPool(maxIdleConnection, 1, TimeUnit.MINUTES)).build();
    }

    protected int parameter = 0;

    protected String getParameter() {
        if (parameter == 0) {
            parameter++;
            return "?";
        } else {
            return "&";
        }
    }


}
