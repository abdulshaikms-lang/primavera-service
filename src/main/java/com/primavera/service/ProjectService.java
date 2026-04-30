package com.primavera.service;


import com.primavera.dto.ProjectDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final RestTemplate restTemplate;
    @Value("${p6.base-url}")
    private String baseUrl;
    @Value("${p6.username}")
    private String username;
    @Value("${p6.password}")
    private String password;
    @Value("${p6.database}")
    private String database;

    private String login() {
        String credentials = Base64.getEncoder()
                .encodeToString((username + ":" + password).getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.set("authToken", credentials);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/p6ws/restapi/login?DatabaseName=" + database,
                HttpMethod.POST,
                new HttpEntity<>(headers),
                String.class
        );

        List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
        if (cookies != null) {
            for (String cookie : cookies) {
                if (cookie.startsWith("JSESSIONID=")) {
                    return cookie.split(";")[0];
                }
            }
        }
        throw new RuntimeException("P6 login failed — no session cookie in response");
    }

    public List<ProjectDto> getProjects() {
        String sessionCookie = login();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, sessionCookie);

        ResponseEntity<ProjectDto[]> response = restTemplate.exchange(
                baseUrl + "/p6ws/restapi/project?Fields=ObjectId,Id,Name,Status,StartDate,FinishDate",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ProjectDto[].class
        );

        return Arrays.asList(response.getBody());
    }
}
