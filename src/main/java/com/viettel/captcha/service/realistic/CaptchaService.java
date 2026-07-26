package com.viettel.captcha.service.realistic;

import com.viettel.captcha.dto.CaptchaVerification;

public interface CaptchaService {
    String provider();

    boolean verify(CaptchaVerification verification);
}
