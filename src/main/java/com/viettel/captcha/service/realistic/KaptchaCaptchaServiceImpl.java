package com.viettel.captcha.service.realistic;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.code.kaptcha.Producer;
import com.viettel.captcha.dto.CaptchaChallenge;
import com.viettel.captcha.dto.CaptchaChallengeVerifyRequest;
import com.viettel.captcha.service.naive.NaiveCaptchaService;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

@Service
public class KaptchaCaptchaServiceImpl implements NaiveCaptchaService {

    private final Producer producer;

    public KaptchaCaptchaServiceImpl(Producer producer) {
        this.producer = producer;
    }


    private final Cache<String, String> captchaCache =
            Caffeine.newBuilder()
                    .expireAfterWrite(Duration.ofMinutes(2))
                    .maximumSize(20_000)
                    .build();

    @Override
    public CaptchaChallenge generate() {

        String answer = producer.createText();

        BufferedImage image = producer.createImage(answer);

        String captchaId = UUID.randomUUID().toString();

        captchaCache.put(captchaId, answer);

        return new CaptchaChallenge(
                captchaId,
                "data:image/png;base64," + toBase64(image)
        );
    }

    @Override
    public boolean verify(CaptchaChallengeVerifyRequest req) {

        if (req.captchaId() == null || req.answer() == null) {
            return false;
        }

        String expected =
                captchaCache.asMap().remove(req.captchaId());

        return expected != null
                && expected.equalsIgnoreCase(req.answer().trim());
    }

    private String toBase64(BufferedImage image) {

        try (ByteArrayOutputStream out =
                     new ByteArrayOutputStream()) {

            ImageIO.write(image, "png", out);

            return Base64.getEncoder()
                    .encodeToString(out.toByteArray());

        } catch (IOException e) {

            throw new IllegalStateException(e);

        }
    }
}