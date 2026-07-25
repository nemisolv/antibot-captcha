package com.viettel.captcha.dto;

public record CaptchaChallengeVerifyRequest(String captchaId, String answer) {
}
