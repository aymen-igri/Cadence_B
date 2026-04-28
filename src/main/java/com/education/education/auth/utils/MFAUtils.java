package com.education.education.auth.utils;

import org.jboss.aerogear.security.otp.api.Base32;

public class MFAUtils {
    private static final String APP_NAME = "EducationApp";

    public static String generateSecretKey() {
        return Base32.random();
    }

    public static String generateQrUrl(String secret, String userEmail) {
        String format = "otpauth://totp/%s:%s?secret=%s&issuer=%s";
        return String.format(format,
                APP_NAME,
                userEmail,
                secret,
                APP_NAME
            );
    }
}
