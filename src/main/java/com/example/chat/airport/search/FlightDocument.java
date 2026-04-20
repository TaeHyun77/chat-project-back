package com.example.chat.airport.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;
import org.springframework.data.elasticsearch.annotations.Setting;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "flights")
@Setting(settingPath = "elasticsearch/flight-index-settings.json")
public class FlightDocument {

    @Id
    private String id; // flightId + "_" + scheduleDateTime

    @Field(type = FieldType.Keyword)
    private String planeId;

    @Field(type = FieldType.Keyword)
    private String flightId;

    @MultiField(
        mainField = @Field(type = FieldType.Text, analyzer = "nori"),
        otherFields = @InnerField(suffix = "keyword", type = FieldType.Keyword)
    )
    private String airLine;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String airport;

    @Field(type = FieldType.Keyword)
    private String airportCode;

    @Field(type = FieldType.Keyword)
    private String scheduleDateTime;

    @Field(type = FieldType.Keyword)
    private String estimatedDateTime;

    @Field(type = FieldType.Keyword)
    private String gatenumber;

    @Field(type = FieldType.Keyword)
    private String terminalid;

    @Field(type = FieldType.Keyword)
    private String remark;

    @Field(type = FieldType.Keyword)
    private String searchDate;

    // 자동완성용 텍스트 필드 (flightId + airLine + airport + airportCode)
    @Field(type = FieldType.Text, analyzer = "nori")
    private String suggest;
}
