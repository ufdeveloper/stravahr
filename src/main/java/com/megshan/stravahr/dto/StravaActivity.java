package com.megshan.stravahr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StravaActivity {

    private Long id;

    @JsonProperty("average_heartrate")
    private Float heartRate;

    @JsonProperty("total_elevation_gain")
    private Float elevationGain; // meters

    @JsonProperty("average_speed")
    private Float averageSpeed; // meters/sec

    // TODO - use enum
    private String type; // https://developers.strava.com/docs/reference/#api-models-ActivityType

    @JsonProperty("start_date_local")
    private LocalDateTime startDateLocal;

    private String description;
}
