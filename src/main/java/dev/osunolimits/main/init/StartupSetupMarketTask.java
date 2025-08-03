package dev.osunolimits.main.init;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import dev.osunolimits.main.App;
import dev.osunolimits.main.init.engine.RunableInitTask;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class StartupSetupMarketTask extends RunableInitTask {

    private static final String MARKETPLACE_URL = "https://osunolimits.dev/marketplace.jar";
    private static final String PLUGINS_DIR = "plugins";
    private static final String PLUGIN_FILE_NAME = "marketplace.jar";

    @Override
    public void execute() throws Exception {

        Boolean isMarketplaceEnabled = Boolean.parseBoolean(App.env.get("DOWNLOAD_MARKETPLACE_PLUGIN", "true"));

        if (!isMarketplaceEnabled) {
            App.log.warn("Marketplace plugin download is disabled.");
            return;
        }

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(MARKETPLACE_URL)
                .build();

        Call call = client.newCall(request);
        try (Response response = call.execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to download: " + response);
            }

            ResponseBody body = response.body();
            if (body == null) {
                throw new RuntimeException("Empty response body for: " + MARKETPLACE_URL);
            }

            // Ensure plugins directory exists
            File pluginsDir = new File(PLUGINS_DIR);
            if (!pluginsDir.exists() && !pluginsDir.mkdirs()) {
                throw new RuntimeException("Could not create plugins directory at " + pluginsDir.getAbsolutePath());
            }

            // File to write to
            File pluginFile = new File(pluginsDir, PLUGIN_FILE_NAME);

            try (InputStream in = body.byteStream();
                 FileOutputStream out = new FileOutputStream(pluginFile)) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            logger.debug("Marketplace plugin downloaded successfully to: " + pluginFile.getAbsolutePath());
        }
    }

    @Override
    public String getName() {
        return "StartupSetupMarketTask";
    }
}
