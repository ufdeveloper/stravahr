package com.megshan.stravahr.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "strava")
public class ApplicationProperties {

    private String baseUrl;
    private String v3BaseUrl;
    private String tokenUri;
    private String athleteActivitiesUri;
    private String detailedActivityUri;

    @Value("${clientId}")
    private String clientId;
    @Value("${clientSecret}")
    private String clientSecret;
}
