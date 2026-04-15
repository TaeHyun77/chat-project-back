package com.example.chat.airport.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResDto {

    private String airportCode;
    private double temperature;
    private double windspeed;
    private int weathercode;
    private String time;
    private String description;

    public static String resolveDescription(int weathercode) {
        if (weathercode == 0) return "맑음";
        if (weathercode <= 3) return "흐림";
        if (weathercode <= 9) return "안개";
        if (weathercode <= 19) return "강수";
        if (weathercode <= 29) return "돌풍";
        if (weathercode <= 39) return "안개";
        if (weathercode <= 49) return "안개";
        if (weathercode <= 59) return "이슬비";
        if (weathercode <= 69) return "비";
        if (weathercode <= 79) return "눈";
        if (weathercode <= 89) return "소나기";
        return "뇌우";
    }
}
