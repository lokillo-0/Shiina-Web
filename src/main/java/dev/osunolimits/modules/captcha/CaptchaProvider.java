package dev.osunolimits.modules.captcha;

import org.jetbrains.annotations.NotNull;

import lombok.Data;

public interface CaptchaProvider {

    public @NotNull CaptchaResponse verifyCaptcha(String captchaResponse);
    
    @Data
    public static class CaptchaResponse {
        public boolean success;
        public int status;
    }
}
