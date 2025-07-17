package br.com.gitmatch.gitmatch.service.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public String sendCodeEmail(String toEmail) {
        String code = generateCode();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Seu código de verificação");
        message.setText("Seu código é: " + code);
        message.setFrom("gitmatch4@gmail.com");

        mailSender.send(message);

        return code;
    }

    private String generateCode() {
        Random rand = new Random();
        int code = 100000 + rand.nextInt(900000); // 6 dígitos
        return String.valueOf(code);
    }
}

