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

    // 자동완성 (flightId, airLine prefix)
    public List<String> autocomplete(String prefix) {
        if (prefix == null || prefix.isBlank()) return List.of();

        List<String> suggestions = new ArrayList<>();

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(Query.of(qb -> qb.bool(b -> b
                        .should(Query.of(q -> q.prefix(p -> p.field("flightId").value(prefix.toUpperCase()))))
                        .should(Query.of(q -> q.prefix(p -> p.field("airLine.keyword").value(prefix))))
                        .minimumShouldMatch("1")
                )))
                .withMaxResults(10)
                .build();

        SearchHits<FlightDocument> hits = esOperations.search(nativeQuery, FlightDocument.class);
        hits.forEach(hit -> {
            FlightDocument doc = hit.getContent();
            suggestions.add(doc.getFlightId() + " | " + doc.getAirLine() + " | " + doc.getAirport());
        });

        return suggestions;
    }
}
