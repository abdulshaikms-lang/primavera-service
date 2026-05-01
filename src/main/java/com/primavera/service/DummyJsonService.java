package com.primavera.service;


import com.primavera.dto.TodoDto;
import com.primavera.dto.TodosResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DummyJsonService {

    private static final String TODOS_URL = "https://dummyjson.com/todos?limit=0";

    private final RestTemplate restTemplate;

    public List<TodoDto> fetchAllTodos() {
        ResponseEntity<TodosResponseDto> response = restTemplate.exchange(
                TODOS_URL,
                HttpMethod.GET,
                null,
                TodosResponseDto.class
        );

        TodosResponseDto body = response.getBody();
        if (body == null || body.getTodos() == null) {
            return List.of();
        }

        log.info("Fetched {} todos from dummyjson", body.getTodos().size());
        return body.getTodos();
    }
}

