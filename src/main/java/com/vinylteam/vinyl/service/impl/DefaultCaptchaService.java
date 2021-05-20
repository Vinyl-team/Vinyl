package com.vinylteam.vinyl.service.impl;

import com.vinylteam.vinyl.util.CaptchaService;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Slf4j
public class DefaultCaptchaService {
    public byte[] getCaptcha(String captchaId) {
        try (ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream()) {
            BufferedImage challenge =
                    CaptchaService.getInstance().getImageChallengeForID(captchaId);

            ImageIO.write(challenge, "jpeg", jpegOutputStream);
            return jpegOutputStream.toByteArray();
        } catch (Exception e) {
            log.error("Error during captcha generation!");
            e.printStackTrace();
        }
        return null;
    }

    public boolean validateCaptcha(String captchaId, String response) {
        return CaptchaService.getInstance().validateResponseForID(captchaId,
                response);
    }
}