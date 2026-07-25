package com.viettel.captcha.dto;


public record CaptchaInfo(
        String answer,
        long expireAt
) {
    @Override
    public String toString()     {
        return "answer: " + answer + "- expireAT: " + expireAt;
    }
}