package com.viettel.captcha.dto;

public record CaptchaVerification(String captchaId, String response, String remoteIp) {
}
