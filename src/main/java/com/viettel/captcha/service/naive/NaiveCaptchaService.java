package com.viettel.captcha.service.naive;

import com.viettel.captcha.dto.CaptchaChallenge;
import com.viettel.captcha.dto.CaptchaChallengeVerifyRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;

public interface NaiveCaptchaService {
    CaptchaChallenge generate();
    boolean verify( CaptchaChallengeVerifyRequest req );

    default String render(String text) {
        int w = 150, h = 48;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        for (int i = 0; i < 6; i++) {
            g.setColor(new Color(rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
            g.drawLine(rnd.nextInt(w), rnd.nextInt(h), rnd.nextInt(w), rnd.nextInt(h));
        }
        g.setFont(new Font("SansSerif", Font.BOLD, 28));
        g.setColor(new Color(rnd.nextInt(120), rnd.nextInt(120), rnd.nextInt(120)));
        g.drawString(text, 12, 34);
        g.dispose();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(img, "png", out);
            return Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to render captcha image", e);
        }
    }
}
