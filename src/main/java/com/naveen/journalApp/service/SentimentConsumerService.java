package com.naveen.journalApp.service;

import com.naveen.journalApp.model.SentimentData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class SentimentConsumerService {

    @Autowired
    private EmailService emailService;


     // Temporarily disabling kafka listener enable it after if you want
     //@KafkaListener(topics = "weekly-sentiments", groupId = "weekly-sentiment-group")
     public void consume(SentimentData sentimentData){
        sendEmail(sentimentData);
     }

    public void sendEmail(SentimentData sentimentData){
        emailService.sendMail(sentimentData.getEmail(), "Sentiment For Previous Week", sentimentData.getSentiment());
    }
}
