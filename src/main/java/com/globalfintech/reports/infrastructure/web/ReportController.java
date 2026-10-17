package com.globalfintech.reports.infrastructure.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globalfintech.reports.application.usecase.GenerateReportUseCase;
import com.globalfintech.reports.domain.model.FinancialReport;
import com.globalfintech.reports.infrastructure.security.DigitalSignatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private static final Logger audit = LoggerFactory.getLogger("AUDIT");

    private final GenerateReportUseCase generateReportUseCase;
    private final DigitalSignatureService signatureService;
    private final ObjectMapper objectMapper;

    public ReportController(GenerateReportUseCase generateReportUseCase,
                            DigitalSignatureService signatureService,
                            ObjectMapper objectMapper) {
        this.generateReportUseCase = generateReportUseCase;
        this.signatureService = signatureService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateReport(
            @RequestParam String reportId,
            @RequestParam String title,
            @RequestParam String normativity,
            @RequestParam String templateKey,
            @RequestParam(defaultValue = "JSON") String format,
            @RequestParam(required = false) String watermark,
            @RequestParam Map<String, String> params) {

        FinancialReport report = generateReportUseCase.execute(reportId, title, normativity, templateKey,
                format, watermark, params);
        String signature = signAndAudit(report, normativity, format);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Digital-Signature", signature);

        if (report.getContent() == null) {
            return ResponseEntity.ok().headers(headers).body(report);
        }
        String extension = format.equalsIgnoreCase("PDF") ? ".pdf" : ".xlsx";
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + reportId + extension + "\"");
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(report.getContentType()))
                .body(report.getContent());
    }

    @PostMapping("/batch")
    public ResponseEntity<String> generateBatchReports(@RequestBody List<ReportRequest> requests) {
        for (ReportRequest request : requests) {
            CompletableFuture
                    .supplyAsync(() -> generateReportUseCase.execute(request.reportId(), request.title(),
                            request.normativity(), request.templateKey(), request.format(), null, Map.of()))
                    .thenAccept(report -> signAndAudit(report, request.normativity(), request.format()))
                    .exceptionally(error -> {
                        audit.error("reportId={} error={}", request.reportId(), error.getMessage());
                        return null;
                    });
        }
        return ResponseEntity.accepted().body("Proceso batch iniciado: " + requests.size() + " reportes");
    }

    private String signAndAudit(FinancialReport report, String normativity, String format) {
        byte[] document = report.getContent() != null ? report.getContent() : toJson(report);
        String signature = signatureService.sign(document);
        audit.info("reportId={} normativity={} format={} signature={}", report.getId(), normativity, format, signature);
        return signature;
    }

    private byte[] toJson(FinancialReport report) {
        try {
            return objectMapper.writeValueAsBytes(report);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }
}
