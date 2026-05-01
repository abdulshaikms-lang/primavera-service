package com.primavera.service;

import com.primavera.dto.ProjectDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PrimaveraService {

    private final ClientService clientService;

    public List<ProjectDto> getProjects() {
        String sessionCookie = clientService.login();
        return clientService.getAllProjects(sessionCookie);
    }
}
