
package dev.osunolimits.utils;

import java.security.SecureRandom;
import java.util.Random;

public class Validation {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new SecureRandom();

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }

        // Only allow optional leading minus and digits, no decimal
        if (!strNum.matches("-?\\d+")) {
            return false;
        }

        try {
            Integer.parseInt(strNum);
            // No need to check range, parseInt throws if out of bounds
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String randomString(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than 0");
        }

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}