package com.server.ecommerce.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServices {
    @Autowired
    private JavaMailSender javaMailSender;

    public EmailServices(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendVerificationCode(String to, int vertificationCode) {
        try {
            jakarta.mail.internet.MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom("khacngoc6969@gmail.com");
            helper.setTo(to);
            helper.setSubject("Your verification code");
            message.setText("This verification code is valid for 5 minutes, please verify it quickly: "
                    + vertificationCode);
            javaMailSender.send(message);
        } catch (Exception e) {

        }

    }

    public void sendMessage(String to, String content) {
        try {
            jakarta.mail.internet.MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom("khacngoc6969@gmail.com");
            helper.setTo(to);
            helper.setSubject("SwapHub support");
            message.setText(content);
            javaMailSender.send(message);

        } catch (Exception e) {

        }
    }

}
