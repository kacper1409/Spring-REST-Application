package com.lab2.service;

import com.lab2.model.AchievementDto;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class WOTApiService {

    private final RestTemplate restTemplate;

    public WOTApiService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public String getPlayerId(String nickname) {

        StringBuilder stringBuilder = new StringBuilder()
                .append("https://api.worldoftanks.eu/wot/account/list/?application_id=1862e24b5e7c7f7afc2914ada61df56d")
                .append("&search=")
                .append(nickname)
                .append("&type=exact");

        String url = stringBuilder.toString();

        return restTemplate.getForObject(url, String.class);
    }

    @Async("asyncExecutor")
    public CompletableFuture<String> getAchievements(String accountId) {
        StringBuilder stringBuilder = new StringBuilder()
                .append("https://api.worldoftanks.eu/wot/account/achievements/?application_id=1862e24b5e7c7f7afc2914ada61df56d")
                .append("&account_id=")
                .append(accountId);

        String url = stringBuilder.toString();
        String achievements = restTemplate.getForObject(url, String.class);

        return CompletableFuture.completedFuture(achievements);
    }

    public CompletableFuture<String> getPersonalData(String accountId) {
        StringBuilder stringBuilder = new StringBuilder()
                .append("https://api.worldoftanks.eu/wot/account/info/?application_id=1862e24b5e7c7f7afc2914ada61df56d")
                .append("&account_id=")
                .append(accountId);

        String url = stringBuilder.toString();
        String achievements = restTemplate.getForObject(url, String.class);

        return CompletableFuture.completedFuture(achievements);
    }
}
