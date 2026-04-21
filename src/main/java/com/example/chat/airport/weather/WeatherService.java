package com.example.chat.airport.weather;

import com.example.chat.airport.weather.dto.WeatherForecastResDto;
import com.example.chat.airport.weather.dto.WeatherForecastResDto.HourlyWeather;
import com.example.chat.airport.weather.dto.WeatherResDto;
import com.example.chat.exception.ChatException;
import com.example.chat.exception.ErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class WeatherService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private Map<String, double[]> airportCoords;

    private static final String OPEN_METEO_URL =
            "https://api.open-meteo.com/v1/forecast?latitude={lat}&longitude={lon}&current_weather=true";

    private static final String OPEN_METEO_HOURLY_URL =
            "https://api.open-meteo.com/v1/forecast?latitude={lat}&longitude={lon}"
                    + "&hourly=temperature_2m,precipitation,weather_code"
                    + "&forecast_hours=24&timezone=auto";

    @PostConstruct
    void loadAirportCoords() {
        try (InputStream is = new ClassPathResource("data/airport-coords.json").getInputStream()) {
            JsonNode root = objectMapper.readTree(is);
            Map<String, double[]> coords = new HashMap<>();

            for (JsonNode node : root) {
                String code = node.path("code").asText();
                double lat = node.path("latitude").asDouble();
                double lon = node.path("longitude").asDouble();
                coords.put(code, new double[]{lat, lon});
            }

            this.airportCoords = Map.copyOf(coords);
            log.debug("공항 좌표 데이터 {}건 로딩 완료", coords.size());
        } catch (Exception e) {
            log.error("공항 좌표 데이터 로딩 실패", e);
            throw new RuntimeException("공항 좌표 데이터 로딩 실패", e);
        }
    }

    // 현재 시점의 날씨 불러오기
    public WeatherResDto getWeather(String airportCode) {
        double[] coords = airportCoords.get(airportCode.toUpperCase());
        if (coords == null) {
            throw new IllegalArgumentException("지원하지 않는 공항 코드: " + airportCode);
        }

        String url = OPEN_METEO_URL
                .replace("{lat}", String.valueOf(coords[0]))
                .replace("{lon}", String.valueOf(coords[1]));

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode current = root.path("current_weather");

            double temperature = current.path("temperature").asDouble();
            double windspeed = current.path("windspeed").asDouble();
            int weathercode = current.path("weathercode").asInt();
            String time = current.path("time").asText();

            return WeatherResDto.builder()
                    .airportCode(airportCode.toUpperCase())
                    .temperature(temperature)
                    .windspeed(windspeed)
                    .weathercode(weathercode)
                    .time(time)
                    .description(WeatherResDto.resolveDescription(weathercode))
                    .build();

        } catch (Exception e) {
            log.error("날씨 조회 실패: airportCode={}", airportCode, e);
            throw new RuntimeException("날씨 정보 조회에 실패했습니다: " + airportCode, e);
        }
    }

    // 당일 시간별 24시간 예보 조회
    public WeatherForecastResDto getHourlyForecast(String airportCode, String airportName) {
        double[] coords = airportCoords.get(airportCode.toUpperCase());
        if (coords == null) {
            throw new ChatException(HttpStatus.BAD_REQUEST, ErrorCode.UNSUPPORTED_AIRPORT_CODE);
        }

        String url = OPEN_METEO_HOURLY_URL
                .replace("{lat}", String.valueOf(coords[0]))
                .replace("{lon}", String.valueOf(coords[1]));

        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode hourly = root.path("hourly");

            JsonNode times = hourly.path("time");
            JsonNode temperatures = hourly.path("temperature_2m");
            JsonNode precipitations = hourly.path("precipitation");
            JsonNode weatherCodes = hourly.path("weather_code");

            List<HourlyWeather> forecasts = new ArrayList<>();
            for (int i = 0; i < times.size(); i++) {
                int code = weatherCodes.get(i).asInt();
                forecasts.add(HourlyWeather.builder()
                        .time(times.get(i).asText())
                        .temperature(temperatures.get(i).asDouble())
                        .precipitation(precipitations.get(i).asDouble())
                        .weatherCode(code)
                        .description(WeatherResDto.resolveDescription(code))
                        .build());
            }

            return WeatherForecastResDto.of(airportCode.toUpperCase(), airportName, forecasts);
        } catch (ChatException e) {
            throw e;
        } catch (Exception e) {
            log.error("시간별 날씨 예보 조회 실패: airportCode={}", airportCode, e);
            throw new ChatException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.ERROR_TO_CALL_WEATHER_API);
        }
    }
}
