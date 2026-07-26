package com.viettel.captcha.service.naive.impl;

import com.viettel.captcha.dto.CaptchaChallenge;
import com.viettel.captcha.dto.CaptchaChallengeVerifyRequest;
import com.viettel.captcha.dto.CaptchaInfo;
import com.viettel.captcha.service.naive.NaiveCaptchaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
@EnableScheduling
public class LocalCacheCleanupNaiveCaptchaServiceImpl implements NaiveCaptchaService {

    private static final Logger log = LoggerFactory.getLogger(LocalCacheCleanupNaiveCaptchaServiceImpl.class);
    private final ConcurrentHashMap<String, CaptchaInfo> cache =
            new ConcurrentHashMap<>();

    @Override
    public CaptchaChallenge generate() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        int a = rnd.nextInt(1, 10);
        int b = rnd.nextInt(1, 10);
        boolean plus = rnd.nextBoolean();
        String text = (plus ? (a + " + " + b) : (Math.max(a, b) + " - " + Math.min(a, b))) + "=";
        int answer = plus ? a + b : Math.max(a, b) - Math.min(a, b);
        String captchaId = UUID.randomUUID().toString();
        cache.put(
                captchaId,
                new CaptchaInfo(
                        String.valueOf(answer),
                        System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(2)
                )
        );

        return new CaptchaChallenge(captchaId, "data:image/png;base64," + render(text));
    }

    @Override
    public boolean verify(CaptchaChallengeVerifyRequest req) {
        if (req.captchaId() == null || req.answer() == null) {
            return false;
        }

        CaptchaInfo info = cache.remove(req.captchaId());

        if (info == null) {
            return false;
        }

        if (System.currentTimeMillis() > info.expireAt()) {
            log.info("EXPIRED {}", info);
            return false;
        }

        return info.answer().equals(req.answer().trim());
    }

    @Scheduled(fixedDelay = 60000)
    public void cleanup() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<String, CaptchaInfo>> iterator = cache.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, CaptchaInfo> entry = iterator.next();
            if (entry.getValue().expireAt() <= now) {
                log.info("Removing expired captcha: {} -> {}", entry.getKey(), entry.getValue());
                iterator.remove();
            }
        }
    }

}
