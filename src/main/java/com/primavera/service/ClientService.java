package com.primavera.service;

import com.primavera.dto.ActivityDto;
import com.primavera.dto.EpsDto;
import com.primavera.dto.ProjectDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientService {

    private final RestTemplate restTemplate;
    @Value("${p6.base-url}")
    private String baseUrl;
    @Value("${p6.username}")
    private String username;
    @Value("${p6.password}")
    private String password;
    @Value("${p6.database}")
    private String database;

    // ─── Auth ────────────────────────────────────────────────────────────────

    public String login() {
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

    public HttpHeaders sessionHeaders(String sessionCookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.COOKIE, sessionCookie);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    // ─── Projects ────────────────────────────────────────────────────────────

    public List<ProjectDto> getAllProjects(String sessionCookie) {
        ResponseEntity<ProjectDto[]> response = restTemplate.exchange(
                baseUrl + "/p6ws/restapi/project?Fields=ObjectId,Id,Name,Status,StartDate,FinishDate",
                HttpMethod.GET,
                new HttpEntity<>(sessionHeaders(sessionCookie)),
                ProjectDto[].class
        );
        return Arrays.asList(response.getBody());
    }

    public ProjectDto findProjectByName(String sessionCookie, String name) {
        ResponseEntity<ProjectDto[]> response = restTemplate.exchange(
                baseUrl + "/p6ws/restapi/project?Fields=ObjectId,Id,Name&Filter=Name='" + name + "'",
                HttpMethod.GET,
                new HttpEntity<>(sessionHeaders(sessionCookie)),
                ProjectDto[].class
        );
        ProjectDto[] body = response.getBody();
        return (body != null && body.length > 0) ? body[0] : null;
    }

    public ProjectDto createProject(String sessionCookie, String name, Integer epsObjectId) {
        Map<String, Object> payload = Map.of(
                "Name", name,
                "Id", name.replaceAll("\\s+", "-").toUpperCase(),
                "ParentEPSObjectId", epsObjectId
        );

        ResponseEntity<Integer[]> response = restTemplate.exchange(
                baseUrl + "/p6ws/restapi/project",
                HttpMethod.POST,
                new HttpEntity<>(List.of(payload), sessionHeaders(sessionCookie)),
                Integer[].class
        );

        Integer[] ids = response.getBody();
        if (ids == null || ids.length == 0) {
            throw new RuntimeException("P6 project creation returned no ObjectId");
        }

        ProjectDto created = new ProjectDto();
        created.setObjectId(String.valueOf(ids[0]));
        created.setName(name);
        log.info("Created P6 project '{}' objectId={}", name, ids[0]);
        return created;
    }

    // ─── EPS ─────────────────────────────────────────────────────────────────

    public EpsDto findEpsByName(String sessionCookie, String name) {
        ResponseEntity<EpsDto[]> response = restTemplate.exchange(
                baseUrl + "/p6ws/restapi/eps?Fields=ObjectId,Id,Name&Filter=Name='" + name + "'",
                HttpMethod.GET,
                new HttpEntity<>(sessionHeaders(sessionCookie)),
                EpsDto[].class
        );
        EpsDto[] body = response.getBody();
        return (body != null && body.length > 0) ? body[0] : null;
    }

    public EpsDto createEps(String sessionCookie, String name) {
        Map<String, Object> payload = Map.of(
                "Name", name,
                "Id", name.replaceAll("\\s+", "-").toUpperCase()
        );

        ResponseEntity<Integer[]> response = restTemplate.exchange(
                baseUrl + "/p6ws/restapi/eps",
                HttpMethod.POST,
                new HttpEntity<>(List.of(payload), sessionHeaders(sessionCookie)),
                Integer[].class
        );

        Integer[] ids = response.getBody();
        if (ids == null || ids.length == 0) {
            throw new RuntimeException("P6 EPS creation returned no ObjectId");
        }

        EpsDto created = new EpsDto();
        created.setObjectId(ids[0]);
        created.setName(name);
        log.info("Created P6 EPS '{}' objectId={}", name, ids[0]);
        return created;
    }

    // ─── Activities ──────────────────────────────────────────────────────────

    public List<ActivityDto> getActivitiesForProject(String sessionCookie, Integer projectObjectId) {
        ResponseEntity<ActivityDto[]> response = restTemplate.exchange(
                baseUrl + "/p6ws/restapi/activity?Fields=ObjectId,Id,Name,Status,ProjectObjectId"
                        + "&ProjectObjectId=" + projectObjectId,
                HttpMethod.GET,
                new HttpEntity<>(sessionHeaders(sessionCookie)),
                ActivityDto[].class
        );
        ActivityDto[] body = response.getBody();
        return body != null ? Arrays.asList(body) : List.of();
    }

    public void createActivity(String sessionCookie, ActivityDto activity) {
        Map<String, Object> payload = Map.of(
                "Id", activity.getId(),
                "Name", activity.getName(),
                "Status", activity.getStatus(),
                "ProjectObjectId", activity.getProjectObjectId()
        );
        restTemplate.exchange(
                baseUrl + "/p6ws/restapi/activity",
                HttpMethod.POST,
                new HttpEntity<>(List.of(payload), sessionHeaders(sessionCookie)),
                Integer[].class
        );
        log.debug("Created activity id='{}' name='{}'", activity.getId(), activity.getName());
    }

    public void updateActivity(String sessionCookie, ActivityDto activity) {
        Map<String, Object> payload = Map.of(
                "ObjectId", activity.getObjectId(),
                "Name", activity.getName(),
                "Status", activity.getStatus()
        );
        restTemplate.exchange(
                baseUrl + "/p6ws/restapi/activity",
                HttpMethod.PUT,
                new HttpEntity<>(List.of(payload), sessionHeaders(sessionCookie)),
                Void.class
        );
        log.debug("Updated activity objectId={} name='{}'", activity.getObjectId(), activity.getName());
    }
}
