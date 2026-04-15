package com.example.chat.airport.weather;

import com.example.chat.airport.weather.dto.WeatherResDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class WeatherService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // 주요 공항 IATA 코드 → 위경도 매핑
    private static final Map<String, double[]> AIRPORT_COORDS = Map.ofEntries(
            Map.entry("NRT", new double[]{35.7647, 140.3864}), // 도쿄 나리타
            Map.entry("HND", new double[]{35.5533, 139.7811}), // 도쿄 하네다
            Map.entry("KIX", new double[]{34.4347, 135.2440}), // 오사카 간사이
            Map.entry("PEK", new double[]{40.0799, 116.6031}), // 베이징 수도
            Map.entry("PVG", new double[]{31.1443, 121.8083}), // 상하이 푸동
            Map.entry("HKG", new double[]{22.3080, 113.9185}), // 홍콩
            Map.entry("BKK", new double[]{13.6811, 100.7470}), // 방콕 수완나품
            Map.entry("SIN", new double[]{1.3644, 103.9915}),  // 싱가포르 창이
            Map.entry("DXB", new double[]{25.2528, 55.3644}),  // 두바이
            Map.entry("LAX", new double[]{33.9416, -118.4085}), // LA
            Map.entry("JFK", new double[]{40.6413, -73.7781}), // 뉴욕 JFK
            Map.entry("LHR", new double[]{51.4700, -0.4543}),  // 런던 히드로
            Map.entry("CDG", new double[]{49.0097, 2.5479}),   // 파리 CDG
            Map.entry("FRA", new double[]{50.0379, 8.5622}),   // 프랑크푸르트
            Map.entry("SYD", new double[]{-33.9399, 151.1753}), // 시드니
            Map.entry("TPE", new double[]{25.0777, 121.2322}), // 타이페이 타오위안
            Map.entry("MNL", new double[]{14.5086, 121.0194}), // 마닐라
            Map.entry("KUL", new double[]{2.7456, 101.7099}),  // 쿠알라룸푸르
            Map.entry("DEL", new double[]{28.5562, 77.1000}),  // 뉴델리
            Map.entry("SVO", new double[]{55.9726, 37.4146})   // 모스크바 셰레메티예보
    );

    private static final String OPEN_METEO_URL =
            "https://api.open-meteo.com/v1/forecast?latitude={lat}&longitude={lon}&current_weather=true";

    // 10분 TTL 캐싱 (동일 공항 중복 호출 방지)
    @Cacheable(value = "weather", key = "#airportCode")
    public WeatherResDto getWeather(String airportCode) {
        double[] coords = AIRPORT_COORDS.get(airportCode.toUpperCase());
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
}
