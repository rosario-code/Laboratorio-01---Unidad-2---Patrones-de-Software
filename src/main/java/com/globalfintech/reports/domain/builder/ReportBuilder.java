package com.globalfintech.reports.domain.builder;

import com.globalfintech.reports.domain.factory.ReportComponentFactory;
import com.globalfintech.reports.domain.model.FinancialReport;
import com.globalfintech.reports.domain.prototype.FinancialReportTemplate;

public interface ReportBuilder {
    ReportBuilder reset();
    ReportBuilder setMetadata(String id, String title);
    ReportBuilder applyTemplate(FinancialReportTemplate template);
    ReportBuilder setComponents(ReportComponentFactory factory);
    ReportBuilder addSection(String content);
    ReportBuilder applyWatermark(String watermarkText);
    FinancialReport build();
}
