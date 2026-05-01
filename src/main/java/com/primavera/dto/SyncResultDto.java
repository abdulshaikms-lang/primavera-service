package com.primavera.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SyncResultDto {
    private String projectId;
    private String projectName;
    private int todosRead;
    private int activitiesCreated;
    private int activitiesUpdated;
}
