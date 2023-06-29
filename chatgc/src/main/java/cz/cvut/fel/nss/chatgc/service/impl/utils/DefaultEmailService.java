package cz.cvut.fel.nss.chatgc.service.impl.utils;

import cz.cvut.fel.nss.chatgc.exceptions.AccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.InternetAddress;

/**
 * Represents Email Service.
 */
@Service
public class DefaultEmailService{

    @Autowired
    public JavaMailSender emailSender;

    /**
     * Sends email from predefined email address.
     * @param toAddress message will be sent to this address
     * @param subject subject of email
     * @param message content of email
     */
    @Transactional
    public void sendSimpleEmail(String toAddress, String subject, String message) {

        try {
            MimeMessagePreparator preparator = (mimeMessage) -> {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
                helper.setFrom(new InternetAddress("test.meeting.scheduler@gmail.com", "Webchat Test"));
                helper.setTo(toAddress);
                helper.setSubject(subject);
                helper.setText(message, true);
            };
            emailSender.send(preparator);
        } catch (MailException exception) {
            throw new AccountException("email is not valid");
        }
    }

}

