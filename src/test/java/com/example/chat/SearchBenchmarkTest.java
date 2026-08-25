/*
package com.example.chat;

import com.example.chat.airport.plane.Plane;
import com.example.chat.airport.plane.repository.PlaneRepository;
import com.example.chat.airport.search.FlightDocument;
import com.example.chat.airport.search.FlightSearchRepository;
import com.example.chat.airport.search.FlightSearchService;
import com.example.chat.airport.search.dto.FlightSearchResDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

@ActiveProfiles("test")
@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration"
})
@MockitoBean(types = KafkaTemplate.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SearchBenchmarkTest {

    @Autowired
    private PlaneRepository planeRepository;

    @Autowired
    private FlightSearchService flightSearchService;

    @Autowired
    private FlightSearchRepository flightSearchRepository;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    private static final int WARMUP_RUNS = 30;
    private static final int MEASURE_RUNS = 200;
    private static final String TODAY = "20260421";

    private static final int DUMMY_COUNT = 50_000;
    private static final int BATCH_SIZE = 1_000;

    private static final String[] AIRLINES = {"대한항공", "아시아나항공", "진에어", "제주항공", "티웨이항공", "에어부산", "에어서울"};
    private static final String[] AIRLINE_CODES = {"KE", "OZ", "LJ", "7C", "TW", "BX", "RS"};
    private static final String[] AIRPORTS = {"나리타", "하네다", "간사이", "푸동", "수완나품", "창이", "히드로", "샤를드골", "프랑크푸르트", "로스앤젤레스"};
    private static final String[] SEARCH_DATES = {"20260420", "20260421", "20260422", "20260423", "20260424", "20260425"};
    private static final String[] TERMINALS = {"P01", "P03"};
    private static final String[] REMARKS = {"출발", "탑승중", "지연", "결항"};

    private final List<Long> dummyPlaneIds = new ArrayList<>();

    @BeforeAll
    void setUp() {
        System.out.println("========================================");
        System.out.println("더미 데이터 " + DUMMY_COUNT + "건 생성 시작...");
        System.out.println("========================================");

        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<Plane> planeBatch = new ArrayList<>(BATCH_SIZE);
        List<FlightDocument> esBatch = new ArrayList<>(BATCH_SIZE);

        for (int i = 0; i < DUMMY_COUNT; i++) {
            int airlineIdx = random.nextInt(AIRLINES.length);
            String airLine = AIRLINES[airlineIdx];
            String airlineCode = AIRLINE_CODES[airlineIdx];
            String airport = AIRPORTS[random.nextInt(AIRPORTS.length)];
            String flightId = airlineCode + (random.nextInt(9000) + 100);
            String searchDate = SEARCH_DATES[random.nextInt(SEARCH_DATES.length)];
            String terminalid = TERMINALS[random.nextInt(TERMINALS.length)];
            String remark = REMARKS[random.nextInt(REMARKS.length)];
            int hour = random.nextInt(6, 24);
            int minute = random.nextInt(0, 60);
            String scheduleDateTime = String.format("%02d%02d", hour, minute);

            Plane plane = Plane.builder()
                    .searchDate(searchDate)
                    .flightId(flightId)
                    .airLine(airLine)
                    .airport(airport)
                    .airportCode(airlineCode)
                    .scheduleDateTime(scheduleDateTime)
                    .estimatedDateTime(scheduleDateTime)
                    .gatenumber(String.valueOf(random.nextInt(1, 50)))
                    .terminalid(terminalid)
                    .remark(remark)
                    .build();

            planeBatch.add(plane);

            if (planeBatch.size() >= BATCH_SIZE) {
                List<Plane> saved = planeRepository.saveAll(planeBatch);
                for (Plane savedPlane : saved) {
                    dummyPlaneIds.add(savedPlane.getId());
                    esBatch.add(toFlightDocument(savedPlane));
                }
                flightSearchRepository.saveAll(esBatch);
                planeBatch.clear();
                esBatch.clear();
            }
        }

        // 남은 데이터 처리
        if (!planeBatch.isEmpty()) {
            List<Plane> saved = planeRepository.saveAll(planeBatch);
            for (Plane savedPlane : saved) {
                dummyPlaneIds.add(savedPlane.getId());
                esBatch.add(toFlightDocument(savedPlane));
            }
            flightSearchRepository.saveAll(esBatch);
        }

        // ES 인덱스 refresh
        IndexOperations indexOps = elasticsearchOperations.indexOps(IndexCoordinates.of("flights"));
        indexOps.refresh();

        System.out.println("더미 데이터 생성 완료: MySQL " + dummyPlaneIds.size() + "건, ES 동기화 완료");
    }

    @AfterAll
    void tearDown() {
        System.out.println("========================================");
        System.out.println("더미 데이터 정리 시작...");
        System.out.println("========================================");

        // MySQL 삭제 (배치)
        for (int i = 0; i < dummyPlaneIds.size(); i += BATCH_SIZE) {
            List<Long> batch = dummyPlaneIds.subList(i, Math.min(i + BATCH_SIZE, dummyPlaneIds.size()));
            planeRepository.deleteAllByIdInBatch(batch);
        }

        // ES 삭제 (배치)
        List<FlightDocument> esDocs = new ArrayList<>();
        for (Long id : dummyPlaneIds) {
            // ES에서는 id 기반 삭제가 필요하므로 dummy doc으로 deleteAll 호출
        }
        // 전체 인덱스를 다시 빌드하는 대신, 테스트 환경이므로 deleteAll 사용
        flightSearchRepository.deleteAll();

        System.out.println("더미 데이터 정리 완료");
    }

    private FlightDocument toFlightDocument(Plane plane) {
        String suggest = String.join(" ",
                plane.getFlightId(),
                plane.getAirLine(),
                plane.getAirport(),
                plane.getAirportCode() != null ? plane.getAirportCode() : ""
        );

        return FlightDocument.builder()
                .id(plane.getFlightId() + "_" + plane.getScheduleDateTime())
                .planeId(String.valueOf(plane.getId()))
                .flightId(plane.getFlightId())
                .airLine(plane.getAirLine())
                .airport(plane.getAirport())
                .airportCode(plane.getAirportCode())
                .scheduleDateTime(plane.getScheduleDateTime())
                .estimatedDateTime(plane.getEstimatedDateTime())
                .gatenumber(plane.getGatenumber())
                .terminalid(plane.getTerminalid())
                .remark(plane.getRemark())
                .searchDate(plane.getSearchDate())
                .suggest(suggest)
                .build();
    }

    @Test
    void 항공사명_검색_벤치마크() {
        runBenchmark(
                "항공사명 검색 (\"대한\")",
                () -> planeRepository.searchByAirLine("대한"),
                () -> flightSearchService.searchByAirLineOnly("대한")
        );
    }

    @Test
    void 항공사명_날짜_복합_검색_벤치마크() {
        runBenchmark(
                "항공사 + 날짜 검색 (\"대한\" + \"" + TODAY + "\")",
                () -> planeRepository.searchByAirLineAndSearchDate("대한", TODAY),
                () -> flightSearchService.searchByAirLineAndDate("대한", TODAY)
        );
    }

    @Test
    void flightId_prefix_검색_벤치마크() {
        runBenchmark(
                "flightId prefix 검색 (\"KE\")",
                () -> planeRepository.searchByFlightIdPrefix("KE"),
                () -> flightSearchService.searchByFlightIdPrefix("KE")
        );
    }

    @Test
    void 공항명_검색_벤치마크() {
        runBenchmark(
                "공항명 검색 (\"나리타\")",
                () -> planeRepository.searchByAirport("나리타"),
                () -> flightSearchService.searchByAirportOnly("나리타")
        );
    }

    @Test
    void 날짜_단일_필터_벤치마크() {
        runBenchmark(
                "날짜 단일 필터 (\"" + TODAY + "\")",
                () -> planeRepository.findBySearchDate(TODAY),
                () -> flightSearchService.searchByDateOnly(TODAY)
        );
    }

    @Test
    void 복합_조건_검색_벤치마크() {
        runBenchmark(
                "복합 조건 검색 (\"대한\" + \"" + TODAY + "\" + \"P01\")",
                () -> planeRepository.searchComplex("대한", TODAY, "P01"),
                () -> flightSearchService.searchComplex("대한", TODAY, "P01")
        );
    }

    private <T> void runBenchmark(
            String scenarioName,
            Supplier<List<T>> mysqlQuery,
            Supplier<?> esQuery
    ) {
        System.out.println("\n========================================");
        System.out.println("시나리오: " + scenarioName);
        System.out.println("========================================");

        // 1) 결과 개수 확인
        List<T> mysqlSample = mysqlQuery.get();
        Object esSample = esQuery.get();

        int mysqlCount = mysqlSample != null ? mysqlSample.size() : -1;
        int esCount = extractSize(esSample);

        System.out.printf("MySQL 결과 수: %d%n", mysqlCount);
        System.out.printf("ES 결과 수   : %d%n", esCount);

        // 2) 워밍업
        for (int i = 0; i < WARMUP_RUNS; i++) {
            consume(mysqlQuery.get());
            consume(esQuery.get());
        }

        // 3) 측정
        long[] mysqlTimes = measure(mysqlQuery, MEASURE_RUNS);
        long[] esTimes = measure(esQuery, MEASURE_RUNS);

        BenchmarkStats mysqlStats = BenchmarkStats.from("MySQL", mysqlTimes);
        BenchmarkStats esStats = BenchmarkStats.from("Elasticsearch", esTimes);

        // 4) 출력
        System.out.println("----------------------------------------");
        mysqlStats.print();
        esStats.print();
        System.out.println("----------------------------------------");

        double avgRatio = mysqlStats.avgMs / esStats.avgMs;
        double p95Ratio = mysqlStats.p95Ms / esStats.p95Ms;

        System.out.printf("평균 기준 ES가 %.2fx 빠름%n", avgRatio);
        System.out.printf("p95 기준 ES가 %.2fx 빠름%n", p95Ratio);
    }

    private long[] measure(Supplier<?> query, int runs) {
        long[] times = new long[runs];

        for (int i = 0; i < runs; i++) {
            long start = System.nanoTime();
            Object result = query.get();
            long end = System.nanoTime();

            consume(result);
            times[i] = end - start;
        }

        return times;
    }

    private void consume(Object result) {
        if (result instanceof List<?> list) {
            // 결과를 한 번은 읽어줘서 측정 왜곡 방지
            int size = list.size();
            if (size > 0) {
                Object first = list.get(0);
                first.hashCode();
            }
        } else if (result != null) {
            result.hashCode();
        }
    }

    private int extractSize(Object result) {
        if (result instanceof List<?> list) {
            return list.size();
        }
        return -1;
    }

    private static class BenchmarkStats {
        private final String name;
        private final double avgMs;
        private final double minMs;
        private final double maxMs;
        private final double p95Ms;

        private BenchmarkStats(String name, double avgMs, double minMs, double maxMs, double p95Ms) {
            this.name = name;
            this.avgMs = avgMs;
            this.minMs = minMs;
            this.maxMs = maxMs;
            this.p95Ms = p95Ms;
        }

        static BenchmarkStats from(String name, long[] nanos) {
            long[] sorted = Arrays.copyOf(nanos, nanos.length);
            Arrays.sort(sorted);

            long total = 0;
            for (long nano : sorted) {
                total += nano;
            }

            double avgMs = total / (double) sorted.length / 1_000_000.0;
            double minMs = sorted[0] / 1_000_000.0;
            double maxMs = sorted[sorted.length - 1] / 1_000_000.0;

            int p95Index = (int) Math.ceil(sorted.length * 0.95) - 1;
            double p95Ms = sorted[Math.max(p95Index, 0)] / 1_000_000.0;

            return new BenchmarkStats(name, avgMs, minMs, maxMs, p95Ms);
        }

        void print() {
            System.out.printf(
                    "%-13s → avg: %8.3f ms | min: %8.3f ms | p95: %8.3f ms | max: %8.3f ms%n",
                    name, avgMs, minMs, p95Ms, maxMs
            );
        }
    }
}
*/
