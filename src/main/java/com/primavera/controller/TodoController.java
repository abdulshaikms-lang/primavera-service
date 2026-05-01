package com.primavera.controller;

import com.primavera.dto.SyncResultDto;
import com.primavera.service.SyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(TodoController.API_ROOT)
@RequiredArgsConstructor
public class TodoController {

    public static final String API_ROOT = "/api/v1/todos";


    public final SyncService syncService;


    @PostMapping("/")
    public ResponseEntity<SyncResultDto> syncTodos() {
        return ResponseEntity.ok(syncService.syncTodosToP6());
    }
}