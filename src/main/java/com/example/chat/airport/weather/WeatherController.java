package com.example.chat.airport.weather;

import com.example.chat.airport.weather.dto.WeatherResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/weather")
    public ResponseEntity<WeatherResDto> getWeather(@RequestParam String airportCode) {
        return ResponseEntity.ok(weatherService.getWeather(airportCode));
    }
}
