package com.topmanager.oiltycoon.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailData {
    private String to;
    private String subject;
    private String body;
}
