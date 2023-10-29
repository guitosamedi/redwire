package dev.back.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String sender;

    @Autowired
    private JavaMailSender emailSender;

    /**
     * envoie un mail avec les paramètres d'entrées
     *
     *
     * @param destinataire
     * @param message
     * @param sujet
     */
    public void sendSimpleMail(String destinataire, String message,String sujet) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(sender);
        mailMessage.setTo(destinataire);
        mailMessage.setText(message);
        mailMessage.setSubject(sujet);
        emailSender.send(mailMessage);
    }
}