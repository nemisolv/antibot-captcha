package com.viettel.captcha.service.naive.impl;

import com.viettel.captcha.dto.CaptchaChallenge;
import com.viettel.captcha.dto.CaptchaChallengeVerifyRequest;
import com.viettel.captcha.service.naive.NaiveCaptchaService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class LocalCacheNaiveCaptchaServiceImpl implements NaiveCaptchaService {
    private final Map<String, String> answerByCaptchaId = new HashMap<>();

    @Override
    public CaptchaChallenge generate() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        int a = rnd.nextInt(1, 10);
        int b = rnd.nextInt(1, 10);
        boolean plus = rnd.nextBoolean();
        String text = (plus ? (a + " + " + b) : (Math.max(a, b) + " - " + Math.min(a, b))) + "=";
        int answer = plus ? a + b : Math.max(a, b) - Math.min(a, b);
        String captchaId = UUID.randomUUID().toString();
        answerByCaptchaId.put(captchaId, String.valueOf(answer));

        return new CaptchaChallenge(captchaId, "data:image/png;base64," + render(text));
    }

    @Override
    public boolean verify(CaptchaChallengeVerifyRequest req) {
        String captchaId = req.captchaId();
        String answer = req.answer();
        if(captchaId == null || answer == null ) {
            return false;
        }
        String expected = answerByCaptchaId.remove(captchaId);
        if(expected == null ) {
            return false;
        }
        return expected.equals(answer.trim());
    }


}
