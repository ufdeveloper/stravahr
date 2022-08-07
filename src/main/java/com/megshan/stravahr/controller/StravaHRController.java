package com.megshan.stravahr.controller;

import com.megshan.stravahr.dto.Activity;
import com.megshan.stravahr.dto.GetTokenData;
import com.megshan.stravahr.dto.GetTokenResponse;
import com.megshan.stravahr.service.StravaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.StringTokenizer;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/v1")
public class StravaHRController {

    @Autowired
    private StravaService stravaService;

    @RequestMapping(path = "/token", method = RequestMethod.POST)
    @ResponseBody
    public GetTokenResponse getToken(@RequestBody GetTokenData getTokenData) {
        return stravaService.getToken(getTokenData.getCode());
    }

    @RequestMapping(path = "/activities", method = RequestMethod.GET)
    @ResponseBody
    public List<Activity> listActivities(HttpServletRequest request, @RequestParam(required = false) boolean weight) {
        return stravaService.listActivities(getBererTokenFromHeader(request), weight);
    }

    private String getBererTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        StringTokenizer stringTokenizer = new StringTokenizer(authHeader, " ");
        if (stringTokenizer.countTokens() != 2) {
            System.out.println("auth header not formatted correctly");
            // TODO - throw AuthenticationException
            throw new RuntimeException();
        }

        if (!stringTokenizer.nextElement().toString().toLowerCase().equals("bearer")) {
            System.out.println("auth header does not contain Bearer token");
            // TODO - throw AuthenticationException
            throw new RuntimeException();
        }

        return stringTokenizer.nextToken();
    }
}
