package dev.osunolimits.modules.queries;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import dev.osunolimits.main.App;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class TurnstileQuery {
    private static final String VERIFY_URL = "https://challenges.cloudflare.com/turnstile/v0/siteverify";
    private static final Logger logger = LoggerFactory.getLogger("TurnstileQuery");
    private static final Gson gson = new Gson();
    private static final OkHttpClient client = new OkHttpClient();

    public static class TurnstileResponse {
        public boolean success;
        public String[] messages;
        public String hostname;
        public String[] errorCodes;
    }

    public @NotNull TurnstileResponse verifyCaptcha(String captchaResponse) {
        RequestBody formBody = new FormBody.Builder()
                .add("secret", App.env.get("TURNSTILE_SECRET"))
                .add("response", captchaResponse)
                .build();

        Request request = new Request.Builder()
                .url(VERIFY_URL)
                .post(formBody)
                .build();

        try {
            String response = client.newCall(request).execute().body().string();
            TurnstileResponse turnstileResponse = gson.fromJson(response, TurnstileResponse.class);
            return turnstileResponse;
        } catch (Exception e) {
            logger.error("Error while verifying captcha", e);
        }

        TurnstileResponse turnstileResponse = new TurnstileResponse();
        turnstileResponse.success = false;
        return turnstileResponse;
    }

}
