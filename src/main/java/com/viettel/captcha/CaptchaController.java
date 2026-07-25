package com.viettel.captcha;

import com.viettel.captcha.dto.CaptchaChallenge;
import com.viettel.captcha.dto.CaptchaChallengeVerifyRequest;
import com.viettel.captcha.service.naive.NaiveCaptchaService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CaptchaController {

    private final NaiveCaptchaService naiveCaptchaService;

    public CaptchaController(@Qualifier("kaptchaCaptchaServiceImpl") NaiveCaptchaService naiveCaptchaService) {
        this.naiveCaptchaService = naiveCaptchaService;
    }

    @GetMapping("/")
    public String displayCaptchaUI(Model model) {
        CaptchaChallenge generatedCaptchaChallenge = naiveCaptchaService.generate();
        model.addAttribute("captchaChallenge", generatedCaptchaChallenge);
        return "index";
    }

    @PostMapping("/verify")
    public String verify(CaptchaChallengeVerifyRequest request, RedirectAttributes redirectAttributes) {
        try {
            boolean success = naiveCaptchaService.verify(request);

            if (success) {
                redirectAttributes.addFlashAttribute("message", "Authenticated");
                redirectAttributes.addFlashAttribute("success", true);
            } else {
                redirectAttributes.addFlashAttribute("message", "❌ Captcha is incorrect");
                redirectAttributes.addFlashAttribute("success", false);
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "⚠ Captcha unavailable");
            redirectAttributes.addFlashAttribute("success", false);
        }
        return "redirect:/";
    }

}
