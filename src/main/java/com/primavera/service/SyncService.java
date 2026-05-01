package com.primavera.service;

import com.primavera.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class SyncService {

    private static final String DEFAULT_PROJECT_NAME = "Default Project";
    private static final String TEST_EPS_NAME = "Test EPS";

    private final ClientService clientService;
    private final DummyJsonService dummyJsonService;

    public SyncResultDto syncTodosToP6() {
        String sessionCookie = clientService.login();

        // 1. Fetch todos
        List<TodoDto> todos = dummyJsonService.fetchAllTodos();

        // 2. Ensure Test EPS exists
        EpsDto eps = clientService.findEpsByName(sessionCookie, TEST_EPS_NAME);
        if (eps == null) {
            eps = clientService.createEps(sessionCookie, TEST_EPS_NAME);
        } else {
            log.info("Found existing EPS '{}' objectId={}", TEST_EPS_NAME, eps.getObjectId());
        }

        // 3. Ensure Default Project exists under Test EPS
        ProjectDto project = clientService.findProjectByName(sessionCookie, DEFAULT_PROJECT_NAME);
        if (project == null) {
            project = clientService.createProject(sessionCookie, DEFAULT_PROJECT_NAME, eps.getObjectId());
        } else {
            log.info("Found existing project '{}' objectId={}", DEFAULT_PROJECT_NAME, project.getObjectId());
        }

        Integer projectObjectId = Integer.valueOf(project.getObjectId());

        // 4. Index existing activities by Id for O(1) dedup lookup
        List<ActivityDto> existing = clientService.getActivitiesForProject(sessionCookie, projectObjectId);
        Map<String, ActivityDto> existingById = new HashMap<>();
        for (ActivityDto a : existing) {
            existingById.put(a.getId(), a);
        }

        // 5. Create or update one activity per todo
        int created = 0;
        int updated = 0;

        for (TodoDto todo : todos) {
            String activityId = "TODO-" + todo.getId();
            String activityName = todo.getTodo();
            String activityStatus = todo.isCompleted() ? "Completed" : "Not Started";

            if (existingById.containsKey(activityId)) {
                ActivityDto toUpdate = existingById.get(activityId);
                toUpdate.setName(activityName);
                toUpdate.setStatus(activityStatus);
                clientService.updateActivity(sessionCookie, toUpdate);
                updated++;
            } else {
                ActivityDto toCreate = new ActivityDto();
                toCreate.setId(activityId);
                toCreate.setName(activityName);
                toCreate.setStatus(activityStatus);
                toCreate.setProjectObjectId(projectObjectId);
                clientService.createActivity(sessionCookie, toCreate);
                created++;
            }
        }

        log.info("Sync complete — created={} updated={}", created, updated);

        return SyncResultDto.builder()
                .projectId(project.getObjectId())
                .projectName(DEFAULT_PROJECT_NAME)
                .todosRead(todos.size())
                .activitiesCreated(created)
                .activitiesUpdated(updated)
                .build();
    }
}
