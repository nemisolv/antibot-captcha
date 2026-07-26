package com.viettel.captcha.service.realistic;

import com.viettel.captcha.dto.CaptchaVerification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@Slf4j
public class TurnstileCaptchaService implements CaptchaService
{

    private static final String SITEVERIFY_URL = "https://challenges.cloudflare.com/turnstile/v0/siteverify";

    private final RestClient restClient;
    private final String siteKey;
    private final String secretKey;


    public TurnstileCaptchaService(RestClient.Builder builder,
                                   @Value("${captcha.turnstile.site-key}") String siteKey,
                                   @Value("${captcha.turnstile.secret-key}") String secretKey) {
        this.restClient = builder.baseUrl(SITEVERIFY_URL).build();
        this.siteKey = siteKey;
        this.secretKey = secretKey;
    }


    @Override
    public String provider() {
        return "turnstile";
    }

    public String siteKey() {
        return siteKey;
    }

    @Override
    public boolean verify(CaptchaVerification v) {
        String token = v.response();
        if(token == null || token.isBlank()) {
            return false;
        }
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("secret", secretKey);
        form.add("response", token);
        if(v.remoteIp() != null ) {
            form.add("remoteip", v.remoteIp());
        }
        try {
            SiteVerifyResponse result= restClient.post()
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(SiteVerifyResponse.class);
            log.info("Turnstile result: {}", result);
            return result != null && result.success();
        } catch (Exception e) {
            log.warn("Turnstile siteverify call failed; rejecting captcha", e);
            return false;
        }

    }

    record SiteVerifyResponse(        boolean success,
                                      String challenge_ts,
                                      String hostname,
                                      List<String> error_codes) {}
}

