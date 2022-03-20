package com.lab2.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
public class PersonalDataDto {
    int globalRating;
    Date createdAt;

    public PersonalDataDto(int globalRating, long date) {
        this.globalRating = globalRating;
        createdAt = new Date(date * 1000);
    }
}
