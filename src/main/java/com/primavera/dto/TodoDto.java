package com.primavera.dto;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TodoDto {
    private Integer id;
    private String todo;
    private boolean completed;
    private Integer userId;
}

