package com.viettel.captcha.service.realistic;

import com.viettel.captcha.dto.CaptchaVerification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
public class ReCaptchaV3CaptchaService implements CaptchaService {

    private static final String SITEVERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    private final RestClient restClient;
    private final String siteKey;
    private final String secretKey;
    private final double minScore;

    public ReCaptchaV3CaptchaService(RestClient.Builder builder,
                                      @Value("${captcha.recaptcha.site-key}") String siteKey,
                                      @Value("${captcha.recaptcha.secret-key}") String secretKey,
                                      @Value("${captcha.recaptcha.min-score:0.5}") double minScore) {
        this.restClient = builder.baseUrl(SITEVERIFY_URL).build();
        this.siteKey = siteKey;
        this.secretKey = secretKey;
        this.minScore = minScore;
    }

    @Override
    public String provider() {
        return "recaptcha-v3";
    }

    public String siteKey() {
        return siteKey;
    }

    @Override
    public boolean verify(CaptchaVerification v) {
        String token = v.response();
        if (token == null || token.isBlank()) {
            return false;
        }
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("secret", secretKey);
        form.add("response", token);
        if (v.remoteIp() != null) {
            form.add("remoteip", v.remoteIp());
        }
        try {
            SiteVerifyResponse result = restClient.post()
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(SiteVerifyResponse.class);
            log.info("reCAPTCHA v3 result: {}", result);
            return result != null && result.success() && result.score() >= minScore;
        } catch (Exception e) {
            log.warn("reCAPTCHA siteverify call failed; rejecting captcha", e);
            return false;
        }
    }

    record SiteVerifyResponse(boolean success, double score, String action, String challenge_ts, String hostname) {}
}
