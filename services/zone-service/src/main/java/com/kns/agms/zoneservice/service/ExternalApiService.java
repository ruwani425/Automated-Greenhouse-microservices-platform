package com.kns.agms.zoneservice.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ExternalApiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private String accessToken;

    private static final String BASE_URL = "http://104.211.95.241:8080/api";

    public void login() {
        String url = BASE_URL + "/auth/login";
        Map<String, String> payload = new HashMap<>();
        payload.put("username", "username");
        payload.put("password", "123456");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> body = response.getBody();
            this.accessToken = (String) body.get("accessToken");
        } else {
            throw new RuntimeException("Failed to login to external API");
        }
    }

    public String registerDevice(String zoneName) {
        if (accessToken == null) {
            login();
        }

        String url = BASE_URL + "/devices";
        Map<String, String> payload = new HashMap<>();
        payload.put("name", zoneName + "-Sensor");
        payload.put("zoneId", zoneName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> body = response.getBody();
            return (String) body.get("deviceId");
        } else {
            throw new RuntimeException("Failed to register device");
        }
    }
}
