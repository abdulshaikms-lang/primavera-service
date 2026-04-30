package com.primavera.controller;

import com.primavera.dto.ProjectDto;
import com.primavera.service.ExcelService;
import com.primavera.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(ProjectController.API_ROOT)
@RequiredArgsConstructor
public class ProjectController {

    public static final String API_ROOT ="/api/v1/projects";

    public final ProjectService projectService;
    public final ExcelService excelService;

    @GetMapping("")
    public ResponseEntity<List<ProjectDto>> getProjects() {
        return ResponseEntity.ok(projectService.getProjects());
    }

    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportProjectsToExcel() throws IOException {
        List<ProjectDto> projects = projectService.getProjects();

        ByteArrayInputStream excelFile = excelService.exportP6ProjectsToExcel(projects);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=p6_projects.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(excelFile));
    }
}