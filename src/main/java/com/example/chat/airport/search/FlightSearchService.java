package com.example.chat.airport.search;

import com.example.chat.airport.search.dto.FlightSearchResDto;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.RequiredArgsConstructor;
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

    // 복합 조건 + fuzzy 검색
    public List<FlightSearchResDto> search(String q, String terminal, String date, String airline) {
        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();

        // 키워드 fuzzy 검색 (flightId, airport 필드)
        if (q != null && !q.isBlank()) {
            boolBuilder.should(
                    Query.of(qb -> qb.fuzzy(f -> f.field("flightId").value(q).fuzziness("AUTO")))
            );
            boolBuilder.should(
                    Query.of(qb -> qb.match(m -> m.field("airport").query(q)))
            );
            boolBuilder.should(
                    Query.of(qb -> qb.match(m -> m.field("airLine").query(q)))
            );
            boolBuilder.minimumShouldMatch("1");
        }

        // 터미널 필터
        if (terminal != null && !terminal.isBlank()) {
            boolBuilder.filter(
                    Query.of(qb -> qb.term(t -> t.field("terminalid").value(terminal)))
            );
        }

        // 날짜 필터
        if (date != null && !date.isBlank()) {
            boolBuilder.filter(
                    Query.of(qb -> qb.term(t -> t.field("searchDate").value(date)))
            );
        }

        // 항공사 필터
        if (airline != null && !airline.isBlank()) {
            boolBuilder.filter(
                    Query.of(qb -> qb.term(t -> t.field("airLine.keyword").value(airline)))
            );
        }

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(Query.of(qb -> qb.bool(boolBuilder.build())))
                .withMaxResults(50)
                .build();

        SearchHits<FlightDocument> hits = esOperations.search(nativeQuery, FlightDocument.class);

        return hits.stream()
                .map(SearchHit::getContent)
                .map(FlightSearchResDto::from)
                .collect(Collectors.toList());
    }

    // 자동완성은 flightId와 airLine prefix로만 가능
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
                .withMaxResults(10) // 최대 10개
                .build();

        // 실제 Elasticsearch 검색 실행
        SearchHits<FlightDocument> hits = esOperations.search(nativeQuery, FlightDocument.class);
        hits.forEach(hit -> {
            FlightDocument doc = hit.getContent();
            suggestions.add(doc.getFlightId() + " | " + doc.getAirLine() + " | " + doc.getAirport() + " | " + doc.getScheduleDateTime());
        });

        return suggestions;
    }

    // 특정 날짜 + remark 조건으로 ES 문서 삭제
    public long deleteBySearchDateAndRemark(String searchDate, String remark) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(Query.of(qb -> qb.bool(b -> b
                        .filter(Query.of(f -> f.term(t -> t.field("searchDate").value(searchDate))))
                        .filter(Query.of(f -> f.term(t -> t.field("remark").value(remark))))
                )))
                .build();

        ByQueryResponse response = esOperations.delete(query, FlightDocument.class);
        return response.getDeleted();
    }
}
