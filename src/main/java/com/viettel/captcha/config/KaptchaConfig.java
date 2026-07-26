package com.viettel.captcha.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {

    @Bean
    public Producer kaptchaProducer() {

        Properties props = new Properties();

        props.put("kaptcha.border", "no");
        props.put("kaptcha.image.width", "160");
        props.put("kaptcha.image.height", "60");

        props.put("kaptcha.textproducer.char.length", "5");
        props.put("kaptcha.textproducer.char.string",
                "23456789ABCDEFGHJKLMNPQRSTUVWXYZ");

        props.put("kaptcha.textproducer.font.names",
                "Arial,Courier");

        props.put("kaptcha.textproducer.font.size", "40");

        props.put("kaptcha.noise.impl",
                "com.google.code.kaptcha.impl.DefaultNoise");

        props.put("kaptcha.obscurificator.impl",
                "com.google.code.kaptcha.impl.WaterRipple");

        props.put("kaptcha.background.clear.from", "255,255,255");
        props.put("kaptcha.background.clear.to", "255,255,255");

        Config config = new Config(props);

        return config.getProducerImpl();
    }
}