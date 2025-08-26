package com.naveen.journalApp.service;

import com.naveen.journalApp.Constants.PlaceHolders;
import com.naveen.journalApp.api.response.WeatherResponse;
import com.naveen.journalApp.cache.AppCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WeatherServices {

    @Value("${weather.api.key}")
    public String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AppCache appCache;

    @Autowired
    private RedisService redisService;

    public WeatherResponse getWeather(String City){
        WeatherResponse weatherResponse = redisService.get("Weather_of_" + City, WeatherResponse.class);
        if (weatherResponse != null){
            return weatherResponse;
        }else {
            String finalAPI = appCache.AppCache.get(AppCache.keys.WEATHER_API.toString()).replace(PlaceHolders.API_KEYS, apiKey).replace(PlaceHolders.CITY, City);
            ResponseEntity<WeatherResponse> response = restTemplate.exchange(finalAPI, HttpMethod.POST, null, WeatherResponse.class);
            WeatherResponse body = response.getBody();
            if(body != null){
                redisService.set("Weather_of_" + City, body, 300l);
            }
            return body;
        }
    }
}
