package com.globalfintech.reports.infrastructure.web;

public record ReportRequest(String reportId, String title, String normativity, String templateKey, String format) {
}
