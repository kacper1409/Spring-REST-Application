package com.lab2.service;

import com.lab2.model.AchievementDto;
import com.lab2.model.PersonalDataDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

@Service
@Data
@Slf4j
public class BrowserRequestService {

    public static final String SNIPER_ACHIEVEMENT = "sniper";
    public static final String SUPPORTER_ACHIEVEMENT = "supporter";
    public static final String DEFENDER_ACHIEVEMENT = "defender";
    private static final String ERROR_MESSAGE = "Blad pobierania danych";

    private final WOTApiService wotApiService;

    public String getPlayerData(String nickname) {
        try {
            String accountId = retrieveAccountIdFromJSON(wotApiService.getPlayerId(nickname));

            CompletableFuture<String> achievements = wotApiService.getAchievements(accountId);
            CompletableFuture<String> personalData = wotApiService.getPersonalData(accountId);

            CompletableFuture.allOf(achievements, personalData).join();

            AchievementDto achievementDto = retrieveAchievementsFromJSON(achievements.get(), accountId);
            PersonalDataDto personalDataDto = retrievePersonalDataFromJSON(personalData.get(), accountId);

            return prepareHTMLTemplate(achievementDto, personalDataDto);
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            return ERROR_MESSAGE;
        }
    }

    private String prepareHTMLTemplate(AchievementDto achievementDto, PersonalDataDto personalDataDto) throws ParseException {

        int numberOfSniper = achievementDto.getNumberOfSniper();
        int numberOfSupporter = achievementDto.getNumberOfSupporter();
        int numberOfDefender = achievementDto.getNumberOfDefender();

        int age = getAccountAge(personalDataDto.getCreatedAt());

        float average = (float)(numberOfSniper + numberOfSupporter + numberOfDefender) / (float)age;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String parsedDate = format.format(personalDataDto.getCreatedAt());

        StringBuilder stringBuilder = new StringBuilder()
                .append("<h1>Rezultat:</h1>")
                .append("<h2>Konto zalozono: "          + parsedDate                        + "</h2>")
                .append("<h2>Ocena osobista: "          + personalDataDto.getGlobalRating() + "</h2>")
                .append("<h2>Ilosc odznaki sniper: "    + numberOfSniper                    + "</h2>")
                .append("<h2>Ilosc odznaki supporter: " + numberOfSupporter                 + "</h2>")
                .append("<h2>Ilosc odznaki defender: "  + numberOfDefender                  + "</h2>")
                .append("<h2>Srednia odznak na rok: "   + average                           + "</h2>");

        return stringBuilder.toString();
    }

    private int getAccountAge(Date createdAt) {
        Date today = new Date();
        return today.getYear() - createdAt.getYear();
    }


    /** JSON HANDLING METHODS **/

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

        int numberOfSniper = data.getJSONObject("achievements").getInt(SNIPER_ACHIEVEMENT);
        int numberOfSupporter = data.getJSONObject("achievements").getInt(SUPPORTER_ACHIEVEMENT);
        int numberOfDefender = data.getJSONObject("achievements").getInt(DEFENDER_ACHIEVEMENT);

        return new AchievementDto(numberOfSniper, numberOfSupporter, numberOfDefender);
    }

    private PersonalDataDto retrievePersonalDataFromJSON(String json, String accountId) {
        JSONObject data = retrieveDataFromJSON(json, accountId);

        int globalRating = data.getInt("global_rating");
        int createdAt = data.getInt("created_at");

        return new PersonalDataDto(globalRating, createdAt);
    }
}


