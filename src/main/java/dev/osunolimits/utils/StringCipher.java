package dev.osunolimits.utils;

import java.nio.charset.StandardCharsets;

public class StringCipher {
    private String key;

    public StringCipher(String key) {
        this.key = key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    // Encode a string
    public String encode(String input) {
        byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);

        byte[] encodedBytes = new byte[inputBytes.length];
        for (int i = 0; i < inputBytes.length; i++) {
            encodedBytes[i] = (byte) (inputBytes[i] ^ keyBytes[i % keyBytes.length]);
        }

        return bytesToHex(encodedBytes);
    }

    // Decode a string
    public String decode(String encodedHex) {
        byte[] encodedBytes = hexToBytes(encodedHex);
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);

        byte[] decodedBytes = new byte[encodedBytes.length];
        for (int i = 0; i < encodedBytes.length; i++) {
            decodedBytes[i] = (byte) (encodedBytes[i] ^ keyBytes[i % keyBytes.length]);
        }

        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    // Helper: Convert bytes to hex
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    // Helper: Convert hex to bytes
    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }

}
