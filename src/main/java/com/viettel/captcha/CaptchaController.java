package com.viettel.captcha;

import com.viettel.captcha.dto.CaptchaChallengeVerifyRequest;
import com.viettel.captcha.dto.CaptchaVerification;
import com.viettel.captcha.service.naive.NaiveCaptchaService;
import com.viettel.captcha.service.realistic.ReCaptchaV2CaptchaService;
import com.viettel.captcha.service.realistic.ReCaptchaV3CaptchaService;
import com.viettel.captcha.service.realistic.TurnstileCaptchaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CaptchaController {

    private final NaiveCaptchaService naiveCaptchaService;
    private final TurnstileCaptchaService turnstileCaptchaService;
    private final ReCaptchaV3CaptchaService reCaptchaV3CaptchaService;
    private final ReCaptchaV2CaptchaService reCaptchaV2CaptchaService;

    public CaptchaController(@Qualifier("kaptchaCaptchaServiceImpl") NaiveCaptchaService naiveCaptchaService,
                              TurnstileCaptchaService turnstileCaptchaService,
                              ReCaptchaV3CaptchaService reCaptchaV3CaptchaService,
                              ReCaptchaV2CaptchaService reCaptchaV2CaptchaService) {
        this.naiveCaptchaService = naiveCaptchaService;
        this.turnstileCaptchaService = turnstileCaptchaService;
        this.reCaptchaV3CaptchaService = reCaptchaV3CaptchaService;
        this.reCaptchaV2CaptchaService = reCaptchaV2CaptchaService;
    }

    @GetMapping("/")
    public String displayCaptchaUI(@RequestParam(name = "tab", defaultValue = "demo") String tab, Model model) {
        model.addAttribute("captchaChallenge", naiveCaptchaService.generate());
        model.addAttribute("activeTab", tab);
        model.addAttribute("turnstileSiteKey", turnstileCaptchaService.siteKey());
        model.addAttribute("recaptchaSiteKey", reCaptchaV3CaptchaService.siteKey());
        model.addAttribute("recaptchaV2SiteKey", reCaptchaV2CaptchaService.siteKey());
        return "index";
    }

    @PostMapping("/demo/verify")
    public String verifyDemo(CaptchaChallengeVerifyRequest request, RedirectAttributes redirectAttributes) {
        try {
            boolean success = naiveCaptchaService.verify(request);
            flash(redirectAttributes, success, success ? "Authenticated" : "Captcha is incorrect");
        } catch (Exception e) {
            flash(redirectAttributes, false, "Captcha unavailable");
        }
        return "redirect:/?tab=demo";
    }

    @PostMapping("/turnstile/verify")
    public String verifyTurnstile(@RequestParam(name = "cf-turnstile-response", required = false) String token,
                                   HttpServletRequest httpRequest,
                                   RedirectAttributes redirectAttributes) {
        boolean success = turnstileCaptchaService.verify(new CaptchaVerification(null, token, httpRequest.getRemoteAddr()));
        flash(redirectAttributes, success, success ? "Authenticated" : "Turnstile verification failed");
        return "redirect:/?tab=turnstile";
    }

    @PostMapping("/recaptcha/verify")
    public String verifyRecaptcha(@RequestParam(name = "g-recaptcha-response", required = false) String token,
                                   HttpServletRequest httpRequest,
                                   RedirectAttributes redirectAttributes) {
        boolean success = reCaptchaV3CaptchaService.verify(new CaptchaVerification(null, token, httpRequest.getRemoteAddr()));
        flash(redirectAttributes, success, success ? "Authenticated" : "reCAPTCHA verification failed");
        return "redirect:/?tab=recaptcha";
    }

    @PostMapping("/recaptcha-v2/verify")
    public String verifyRecaptchaV2(@RequestParam(name = "g-recaptcha-response", required = false) String token,
                                     HttpServletRequest httpRequest,
                                     RedirectAttributes redirectAttributes) {
        boolean success = reCaptchaV2CaptchaService.verify(new CaptchaVerification(null, token, httpRequest.getRemoteAddr()));
        flash(redirectAttributes, success, success ? "Authenticated" : "reCAPTCHA v2 verification failed");
        return "redirect:/?tab=recaptcha-v2";
    }

    private void flash(RedirectAttributes redirectAttributes, boolean success, String message) {
        redirectAttributes.addFlashAttribute("message", message);
        redirectAttributes.addFlashAttribute("success", success);
    }

}
