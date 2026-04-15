package com.example.chat.airport.search;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface FlightSearchRepository extends ElasticsearchRepository<FlightDocument, String> {
}
