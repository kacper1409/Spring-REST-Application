package com.lab2.service;

import com.lab2.model.AchievementDto;
import com.lab2.model.PersonalDataDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Data
@Slf4j
public class BrowserRequestService {

    public static final String sniperAchievement = "sniper";
    public static final String supporterAchievement = "supporter";
    public static final String defenderAchievement = "defender";

    private final WOTApiService wotApiService;

    public String getPlayerData(String nickname) {
        try {
            String accountId = retrieveAccountIdFromJSON(wotApiService.getPlayerId(nickname));

            CompletableFuture<String> achievements = wotApiService.getAchievements(accountId);
            CompletableFuture<String> personalData = wotApiService.getPersonalData(accountId);

            CompletableFuture.allOf(achievements, personalData).join();

            AchievementDto achievementDto = retrieveAchievementsFromJSON(achievements.get(), accountId);
            PersonalDataDto personalDataDto = retrievePersonalDataFromJSON(personalData.get(), accountId);

            return getHTMLTemplate(achievementDto, personalDataDto);
        } catch (Exception e) {
            log.info(e.getMessage(), e);
        }
        return "";
    }

    private String getHTMLTemplate(AchievementDto achievementDto, PersonalDataDto personalDataDto) {
        StringBuilder stringBuilder = new StringBuilder()
                .append("<h1>Rezultat:</h1>")
                .append("<h2>Konto zalozono: " + personalDataDto.getCreatedAt() + "</h2>")
                .append("<h2>Ocena osobista: " + personalDataDto.getGlobalRating() + "</h2>");


        return stringBuilder.toString();
    }

    private String retrieveAccountIdFromJSON(String json) {
        JSONObject obj = new JSONObject(json);

        String accountId = "";
        JSONArray jsonArray = obj.getJSONArray("data");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonobject = jsonArray.getJSONObject(i);
            accountId =  String.valueOf(jsonobject.getInt("account_id"));
        }
        return accountId;
    }

    private JSONObject retrieveDataFromJSON(String json, String accountId) {
        JSONObject obj = new JSONObject(json);
        return obj.getJSONObject("data").getJSONObject(accountId);
    }

    private AchievementDto retrieveAchievementsFromJSON(String json, String accountId) {
        JSONObject data = retrieveDataFromJSON(json, accountId);

        int numberOfSniper = data.getJSONObject("achievements").getInt(sniperAchievement);
        int numberOfSupporter = data.getJSONObject("achievements").getInt(supporterAchievement);
        int numberOfDefender = data.getJSONObject("achievements").getInt(defenderAchievement);

        return new AchievementDto(numberOfSniper, numberOfSupporter, numberOfDefender);
    }

    private PersonalDataDto retrievePersonalDataFromJSON(String json, String accountId) {
        JSONObject data = retrieveDataFromJSON(json, accountId);

        int globalRating = data.getInt("global_rating");
        int createdAt = data.getInt("created_at");

        return new PersonalDataDto(globalRating, createdAt);
    }
}


