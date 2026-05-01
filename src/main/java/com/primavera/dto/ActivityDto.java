package com.primavera.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityDto {

    @JsonProperty("ObjectId")
    private Integer objectId;

    @JsonProperty("Id")
    private String id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("ProjectObjectId")
    private Integer projectObjectId;
}
