package com.education.education.auth.deo.responses;

public record MfaSetupRes(
        String secret,
        String qrUrl
) {
}
