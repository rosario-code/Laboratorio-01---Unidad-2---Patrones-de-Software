package com.globalfintech.reports.infrastructure.builder;

import com.globalfintech.reports.domain.model.FinancialReport;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

@Component("EXCELBuilder")
@Scope("prototype")
public class ExcelReportBuilder extends ConcreteFinancialReportBuilder {

    @Override
    public FinancialReport build() {
        FinancialReport report = super.build();
        report.setContent(toExcel(report));
        report.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return report;
    }

    private byte[] toExcel(FinancialReport report) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Reporte");
            sheet.setColumnWidth(0, 90 * 256);
            int row = 0;
            if (report.getWatermark() != null) {
                sheet.getHeader().setCenter(report.getWatermark());
                sheet.createRow(row++).createCell(0).setCellValue(report.getWatermark());
            }
            sheet.createRow(row++).createCell(0).setCellValue(report.getHeaderContent());
            sheet.createRow(row++).createCell(0).setCellValue(report.getTitle());
            sheet.createRow(row++).createCell(0).setCellValue("ID: " + report.getId());
            for (String section : report.getSections()) {
                sheet.createRow(row++).createCell(0).setCellValue(section);
            }
            sheet.createRow(row).createCell(0).setCellValue(report.getFooterContent());
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
