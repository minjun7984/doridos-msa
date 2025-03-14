package kr.doridos.ticketservice.ticket.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.Id;
import kr.doridos.ticketservice.category.entity.Category;
import kr.doridos.ticketservice.place.entity.Place;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "tickets", createIndex = true)
@Setting(settingPath = "elasticsearch/ticket-setting.json")
@Mapping(mappingPath = "elasticsearch/ticket-mapping.json")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketDocument {

    @Id
    @Field(type = FieldType.Long)
    private Long id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Text)
    private String runningTime;

    @Field(type = FieldType.Date, format = {DateFormat.date_hour_minute_second_millis, DateFormat.epoch_millis})
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime startDate;

    @Field(type = FieldType.Date, format = {DateFormat.date_hour_minute_second_millis, DateFormat.epoch_millis})
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime endDate;

    @Field(type = FieldType.Text)
    private String place;

    @Field(type = FieldType.Keyword)
    private String category;

    @Builder
    public TicketDocument(Long id, String title, String content, String runningTime, LocalDateTime startDate, LocalDateTime endDate, String place, String category) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.runningTime = runningTime;
        this.startDate = startDate;
        this.endDate = endDate;
        this.place = place;
        this.category = category;
    }

    public static TicketDocument from(Ticket ticket, Category category, Place place) {
        return TicketDocument.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .content(ticket.getContent())
                .runningTime(ticket.getRunningTime())
                .startDate(ticket.getStartDate())
                .endDate(ticket.getEndDate())
                .place(place.getName())
                .category(category.getName())
                .build();
    }
}
