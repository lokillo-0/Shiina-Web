package dev.osunolimits.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.security.crypto.bcrypt.BCrypt;

import dev.osunolimits.main.App;
import lombok.Data;

public class Auth {

    @Data
    public class User {
        public Integer id;
        public String name;
        public String safe_name;
        public String email;
        public Integer priv;
        public Integer created;
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
            // Create an instance of the MD5 MessageDigest
            MessageDigest md = MessageDigest.getInstance("MD5");

            // Hash the input string and return the result as a hex string directly
            return BCrypt.checkpw(new BigInteger(1, md.digest(raw.getBytes())).toString(16), bcrypt);
        } catch (Exception e) {
            App.log.error("Failed to encode MD5", e);
            return null;
        }
    }
}
