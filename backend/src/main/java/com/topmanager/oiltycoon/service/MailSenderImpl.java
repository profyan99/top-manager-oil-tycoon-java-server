package com.topmanager.oiltycoon.service;

import com.topmanager.oiltycoon.model.MailData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailSenderImpl implements MailSender {

    private final Logger logger = LoggerFactory.getLogger(MailSenderImpl.class);

    private JavaMailSender javaMailSender;

    @Autowired
    public void setJavaMailSender(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    @Async
    public void send(MailData mailData) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(mailData.getTo());
        simpleMailMessage.setSubject(mailData.getSubject());
        simpleMailMessage.setText(mailData.getBody());
        javaMailSender.send(simpleMailMessage);
        logger.info("Sending SMTP message...");
    }
}
