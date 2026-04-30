package com.primavera.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectDto {

    @JsonProperty("Id")
    private String id;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("FinishDate")
    private String finishDate;
    @JsonProperty("ObjectId")
    private String objectId;
    @JsonProperty("StartDate")
    private String startDate;
    @JsonProperty("Status")
    private String status;
}