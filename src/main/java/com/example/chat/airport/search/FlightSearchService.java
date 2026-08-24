package com.example.chat.airport.search;

import com.example.chat.airport.search.dto.FlightSearchResDto;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import org.springframework.data.elasticsearch.core.query.ByQueryResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FlightSearchService {

    private final ElasticsearchOperations esOperations;

    // 복합 조건 검색
    // q: 항공편 번호(flightId) 또는 항공사명(airLine) 검색 키워드
    public List<FlightSearchResDto> search(String q, String date) {
        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();

        if (q != null && !q.isBlank()) {
            boolBuilder.should(
                    Query.of(qb -> qb.prefix(p -> p.field("flightId").value(q.toUpperCase()).boost(3.0f)))
            );
            boolBuilder.should(
                    Query.of(qb -> qb.match(m -> m.field("airLine").query(q).boost(2.0f)))
            );
            boolBuilder.should(
                    Query.of(qb -> qb.prefix(p -> p.field("airLine.keyword").value(q).boost(2.0f)))
            );
            boolBuilder.should(
                    Query.of(qb -> qb.fuzzy(f -> f.field("airLine").value(q).fuzziness("AUTO").boost(1.2f)))
            );
            boolBuilder.minimumShouldMatch("1");
        }

        // date로 필터링 - date 값이 존재한다면 이 값에 해당되는 데이터만 허용하도록
        if (date != null && !date.isBlank()) {
            boolBuilder.filter(
                    Query.of(qb -> qb.term(t -> t.field("searchDate").value(date)))
            );
        }

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(Query.of(qb -> qb.bool(boolBuilder.build())))
                .withSort(Sort.by(Sort.Direction.ASC, "scheduleDateTime"))
                .withMaxResults(1000)
                .build();

        SearchHits<FlightDocument> hits = esOperations.search(nativeQuery, FlightDocument.class);

        return hits.stream()
                .map(SearchHit::getContent)
                .map(FlightSearchResDto::from)
                .collect(Collectors.toList());
    }

    // 자동완성
    // flightId와 airLine prefix로만 가능
    public List<String> autocomplete(
            String prefix,
            String date
    ) {
        if (prefix == null || prefix.isBlank()) return List.of();

        // 결과를 담을 리스트
        List<String> suggestions = new ArrayList<>();

        // 검색 조건 생성
        // flightId(대문자 변환) 또는 airLine.keyword에서 입력한 prefix로 시작하는 값을 조회하며, 둘 중 하나만 일치해도 결과에 포함
        BoolQuery.Builder boolBuilder = new BoolQuery.Builder()
                .should(Query.of(q -> q.prefix(p -> p.field("flightId").value(prefix.toUpperCase()))))
                .should(Query.of(q -> q.prefix(p -> p.field("airLine.keyword").value(prefix))))
                .minimumShouldMatch("1");

        // 날짜 필터
        if (date != null && !date.isBlank()) { // 날짜를 넘겨줬다면 ( 해당 날짜의 항공편만 자동완성 )
            boolBuilder.filter(
                    Query.of(qb -> qb.term(t -> t.field("searchDate").value(date)))
            );
        }

        // 검색 조건과 결과 개수 제한을 정의하는 쿼리 객체
        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(Query.of(qb -> qb.bool(boolBuilder.build())))
                .withSort(Sort.by(Sort.Direction.ASC, "scheduleDateTime"))
                .withMaxResults(100) // 최대 100개
                .build();

        // 실제 Elasticsearch 검색 실행
        SearchHits<FlightDocument> hits = esOperations.search(nativeQuery, FlightDocument.class);
        hits.forEach(hit -> {
            FlightDocument doc = hit.getContent();
            suggestions.add(doc.getFlightId() + " | " + doc.getAirLine() + " | " + doc.getAirport() + " | " + doc.getScheduleDateTime());
        });

        return suggestions;
    }
}
