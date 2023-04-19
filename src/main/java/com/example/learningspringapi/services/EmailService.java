package com.example.learningspringapi.services;

import com.example.learningspringapi.entity.EmailDetails;
import org.springframework.stereotype.Service;


@Service
public interface EmailService {
    String sendSimpleMail(EmailDetails details);
    String sendMailWithAttachment(EmailDetails details);
}
