package com.primavera.service;


import com.primavera.dto.ProjectDto;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelService {

    public ByteArrayInputStream exportP6ProjectsToExcel(List<ProjectDto> projects) throws IOException {


        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("P6 Projects");

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        String[] headers = {"Object Id", "Project Id", "Name", "Status", "Start Date", "Finish Date"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowNum = 1;
        for (ProjectDto project : projects) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(project.getObjectId());
            row.createCell(1).setCellValue(project.getId());
            row.createCell(2).setCellValue(project.getName());
            row.createCell(3).setCellValue(project.getStatus());
            row.createCell(4).setCellValue(project.getStartDate());
            row.createCell(5).setCellValue(project.getFinishDate());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}

