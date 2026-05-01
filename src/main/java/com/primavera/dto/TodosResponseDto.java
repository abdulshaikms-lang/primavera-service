package com.primavera.dto;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TodosResponseDto {
    private List<TodoDto> todos;
    private Integer total;
    private Integer skip;
    private Integer limit;
}
