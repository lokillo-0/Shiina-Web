package dev.osunolimits.utils;

import java.math.BigInteger;
import java.security.MessageDigest;

import org.springframework.security.crypto.bcrypt.BCrypt;

import dev.osunolimits.main.App;

public class Auth {
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
