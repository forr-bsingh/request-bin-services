package com.github.requestbin.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ops implements Serializable {
    private static final long serialVersionUID = -6050899848357744295L;
    private String identifier;
    private String operation;
    private transient Object request;
    private transient Object response;
    private transient Object headers;
    private String createdAt;

    public String getMessage() {
        long seconds = ChronoUnit.SECONDS.between(LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME), LocalDateTime.now(Clock.systemUTC()));
        if (seconds > 60) {
            long minutes = ChronoUnit.MINUTES.between(LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME), LocalDateTime.now(Clock.systemUTC()));
            if (minutes > 60) {
                long hours = ChronoUnit.HOURS.between(LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME), LocalDateTime.now(Clock.systemUTC()));
                if (hours > 24) {
                    return String.format("%d day(s) ago", ChronoUnit.DAYS.between(LocalDateTime.parse(createdAt, DateTimeFormatter.ISO_DATE_TIME), LocalDateTime.now(Clock.systemUTC())));
                }
                return String.format("%d hour(s) ago", hours);
            }
            return String.format("%d min(s) ago", minutes);
        }
        return String.format("%d second(s) ago", seconds);
    }

    public void setResponse(Object response) {
        this.response = response;
    }
}
