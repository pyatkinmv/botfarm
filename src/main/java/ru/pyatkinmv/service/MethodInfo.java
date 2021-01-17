package ru.pyatkinmv.service;

import lombok.Getter;

import java.math.BigDecimal;

// TODO: This is not method info actually. The class contains rate
//  Refactor
@Getter
public enum MethodInfo {
    LIKE_PHOTO(20.0),
    LIKE_POST(20.0),
    SUBSCRIBE_ON_USER(50.0),
    SUBSCRIBE_ON_GROUP(0.01),
    POST_MAIN_PHOTO(0.04),
    POST_WALL_PHOTO(0.3),
    //  TODO: Not sure it's necessary
    //   POST_WALL_MESSAGE(0.3),
    POST_WALL_POST(0.3),
    REPOST(0.5);

    private final BigDecimal rate;

    MethodInfo(double rate) {
        this.rate = BigDecimal.valueOf(rate);
    }
}
