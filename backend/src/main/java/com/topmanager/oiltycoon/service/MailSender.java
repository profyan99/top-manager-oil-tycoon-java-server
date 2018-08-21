package com.topmanager.oiltycoon.service;

import com.topmanager.oiltycoon.model.MailData;

public interface MailSender {
    void send(MailData mailData);
}
