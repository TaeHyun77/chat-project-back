package com.example.chat.airport.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class WeatherForecastResDto {
    private String airportCode;
    private String airportName;
    private List<HourlyWeather> forecasts;

    public static WeatherForecastResDto of(String airportCode, String airportName, List<HourlyWeather> forecasts) {
        return WeatherForecastResDto.builder()
                .airportCode(airportCode)
                .airportName(airportName)
                .forecasts(forecasts)
                .build();
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class HourlyWeather {
        private String time;
        private double temperature;
        private double precipitation;
        private int weatherCode;
        private String description;
    }
}
