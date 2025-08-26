package com.naveen.journalApp.schedular;

import com.naveen.journalApp.Entity.JournalEntry;
import com.naveen.journalApp.Entity.User;
import com.naveen.journalApp.Repository.UserRepositoryImpl;
import com.naveen.journalApp.cache.AppCache;
import com.naveen.journalApp.enums.Sentiment;
import com.naveen.journalApp.model.SentimentData;
import com.naveen.journalApp.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserSchedular {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepositoryImpl userRepository;

//    @Autowired
//    private Sentiment sentiment;

    @Autowired
    private AppCache appCache;

    @Autowired
    private KafkaTemplate<String, SentimentData> kafkaTemplate;

    @Scheduled(cron = "0 0 9 * * SUN")
    public void fetchUserAndSendSAMails(){
        List<User> users = userRepository.getUsersForSA();
        for(User user : users){
            List<JournalEntry> journalEntries = user.getJournalEntries();
            List<Sentiment> sentiments = journalEntries.stream().filter(x -> x.getDate().isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS))).map(x ->x.getSentiment()).collect(Collectors.toList());
            Map<Sentiment, Integer> sentimentCounts = new HashMap<>();
            for(Sentiment sentiment : sentiments){
                if (sentiment != null){
                    sentimentCounts.put(sentiment, sentimentCounts.getOrDefault(sentiment, 0) + 1);
                }
            }
            Sentiment mostFrequentSentiment = null;
            int maxCount = 0;
            for (Map.Entry<Sentiment, Integer> Entry : sentimentCounts.entrySet()){
                if (Entry.getValue() > maxCount){
                    maxCount = Entry.getValue();
                    mostFrequentSentiment = Entry.getKey();
                }
            }
            if (mostFrequentSentiment != null){
                SentimentData sentimentData = SentimentData.builder().email(user.getEMail()).sentiment("Sentiment for Last 7 Days "+ mostFrequentSentiment).build();
                try{
                    kafkaTemplate.send("weekly-sentiments", sentimentData.getEmail(), sentimentData);
                }catch (Exception e){
                    emailService.sendMail(sentimentData.getEmail(), "Sentiment for Previous Week ", mostFrequentSentiment.toString());
                }
            }
        }
    }

    @Scheduled(cron = "0 0/10 * ? * *")
    public void clearAppCache(){
        appCache.init();
    }
}
