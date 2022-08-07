package com.megshan.stravahr.service;

import com.megshan.stravahr.config.ApplicationProperties;
import com.megshan.stravahr.dto.Activity;
import com.megshan.stravahr.dto.GetTokenResponse;
import com.megshan.stravahr.dto.StravaActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestOperations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

@Service
public class StravaService {

    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String CODE = "code";
    private static final String GRANT_TYPE = "grant_type";
    private static final String GRANT_TYPE_AUTH_CODE = "authorization_code";

    @Autowired
    private RestOperations restOperations;

    @Autowired
    private ApplicationProperties applicationProperties;

    public GetTokenResponse getToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(CLIENT_ID, applicationProperties.getClientId());
        map.add(CLIENT_SECRET, applicationProperties.getClientSecret());
        map.add(CODE, code);
        map.add(GRANT_TYPE, GRANT_TYPE_AUTH_CODE);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);

        ResponseEntity<GetTokenResponse> response
                = restOperations.postForEntity(applicationProperties.getBaseUrl() + applicationProperties.getTokenUri(),
                requestEntity , GetTokenResponse.class);
        System.out.println("token response = " + response.getBody());

        return response.getBody();
    }

    public List<Activity> listActivities(String accessToken, boolean weight) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // TODO - add filters for fromDate and toDate
        // TODO - get date of activity and filter by activity type
        Map<String, String> params = new HashMap<>();
        params.put("numPerPage", "20");
        ResponseEntity<List<StravaActivity>> response = restOperations.exchange(
                applicationProperties.getV3BaseUrl() + applicationProperties.getAthleteActivitiesUri() + "?per_page={numPerPage}",
                HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<StravaActivity>>() {}, params);
        List<StravaActivity> stravaActivities = response.getBody();
        System.out.println("strava activities = " + stravaActivities);

        List<Activity> activities = new ArrayList<>();

        if (weight) {
            // get weight for each activity from its description.
            stravaActivities.forEach(stravaActivity -> {
                Activity detailedActivity = getDetailedActivity(stravaActivity, accessToken);
                if (detailedActivity != null) {
                    activities.add(detailedActivity);
                }
            });
        } else {
            // we already have the details we need, just pack them up and return the response.
            stravaActivities.forEach(stravaActivity -> {
                activities.add(Activity.builder()
                        .id(stravaActivity.getId())
                        .heartRate(stravaActivity.getHeartRate())
                        .elevationGain(stravaActivity.getElevationGain())
                        .averageSpeed(stravaActivity.getAverageSpeed())
                        .type(stravaActivity.getType())
                        .startDateLocal(stravaActivity.getStartDateLocal())
                        .build());
            });
        }

        System.out.println("activities with weight = " + activities);
        return activities;
    }

    private Activity getDetailedActivity(StravaActivity stravaActivity, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<StravaActivity> activityResponse = restOperations.exchange(
                String.format(applicationProperties.getV3BaseUrl() + applicationProperties.getDetailedActivityUri() + "/%s", stravaActivity.getId()), HttpMethod.GET, requestEntity, StravaActivity.class);
        StravaActivity stravaActivityWithHr = activityResponse.getBody();

        Activity activity = null;

        if (stravaActivityWithHr.getHeartRate() != null && !ObjectUtils.isEmpty(stravaActivityWithHr.getDescription())) {
            // parse weight
            // favoring StringTokenizer over String.contains("wt:") because not sure if weight is followed by space or EOL
            StringTokenizer tokenizer = new StringTokenizer(stravaActivityWithHr.getDescription(), " ");
            while (tokenizer.hasMoreElements()) {
                String token = (String) tokenizer.nextElement();
                if (token.startsWith("wt:")) {
                    Float weight = Float.valueOf(token.substring(3));
                    activity = Activity.builder()
                            .id(stravaActivity.getId())
                            .heartRate(stravaActivity.getHeartRate())
                            .elevationGain(stravaActivity.getElevationGain())
                            .averageSpeed(stravaActivity.getAverageSpeed())
                            .type(stravaActivity.getType())
                            .startDateLocal(stravaActivity.getStartDateLocal())
                            .weight(weight)
                            .build();
                }
            }
        }

        return activity;
    }
}
