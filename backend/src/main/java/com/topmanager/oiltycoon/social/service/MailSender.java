package com.topmanager.oiltycoon.social.service;

import com.topmanager.oiltycoon.social.model.MailData;

public interface MailSender {
    void send(MailData mailData);
}
