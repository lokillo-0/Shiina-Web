package dev.osunolimits.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.security.crypto.bcrypt.BCrypt;

import com.google.gson.annotations.SerializedName;

import dev.osunolimits.main.App;
import lombok.Data;

public class Auth {

    @Data
    public static class User {
        public Integer id;
        public String name;
        public String safe_name;
        public String email;
        public Integer priv;
        public Integer created;
    }

    @Data
    public static class SessionUser {
        @SerializedName("id")
        public Integer id;
        @SerializedName("created")
        public Integer created;
        @SerializedName("ip")
        public String ip;
        @SerializedName("userAgent")
        public String userAgent = "Unknown";
        @SerializedName("city")
        public String city = null;
        @SerializedName("country")
        public String country = null;
    }

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    public static String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    public static Boolean checkPw(String raw, String bcrypt) {
        try {
            return BCrypt.checkpw(md5(raw), bcrypt);
        } catch (Exception e) {
            App.log.error("Failed to check hash using BCrypt", e);
            return null;
        }
    }

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return new BigInteger(1, md.digest(input.getBytes())).toString(16);
        } catch (Exception e) {
            App.log.error("Failed to hash using MD5", e);
            return null;
        }
    }

    public static String bcrypt(String input) {
        return BCrypt.hashpw(input, BCrypt.gensalt());
    }
}
