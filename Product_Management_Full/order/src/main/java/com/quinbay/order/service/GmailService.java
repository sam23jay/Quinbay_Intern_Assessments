package com.quinbay.order.service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;

public interface GmailService {
    void sendMessage(String to, String from, String subject, String bodyText) throws MessagingException, IOException, GeneralSecurityException;
}