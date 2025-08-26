package com.naveen.journalApp.cache;

import com.naveen.journalApp.Entity.ConfigJournalAppEntity;
import com.naveen.journalApp.Repository.ConfigJournalAppRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppCache {

    public enum keys{
        WEATHER_API
    }

    @Autowired
    private ConfigJournalAppRepository configJournalAppRepository;

    public Map<String, String> AppCache;

    @PostConstruct
    public void init(){
        AppCache = new HashMap<>();
        List<ConfigJournalAppEntity> all = configJournalAppRepository.findAll();
        for(ConfigJournalAppEntity configJournalAppEntity: all){
            AppCache.put(configJournalAppEntity.getKey(), configJournalAppEntity.getValue());
        }
    }
}
