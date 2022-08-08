package com.megshan.stravahr.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Activity {
    Long id;
    Float heartRate;
    Float weight;
    Float elevationGain; // meters
    Float averageSpeed; // min/mile
    String type;
    LocalDateTime startDateLocal;
}
