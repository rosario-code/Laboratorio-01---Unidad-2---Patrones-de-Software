package com.globalfintech.reports.infrastructure.builder;

import com.globalfintech.reports.domain.builder.ReportBuilder;
import com.globalfintech.reports.domain.factory.ReportComponentFactory;
import com.globalfintech.reports.domain.model.FinancialReport;
import com.globalfintech.reports.domain.prototype.FinancialReportTemplate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("JSONBuilder")
@Scope("prototype")
public class ConcreteFinancialReportBuilder implements ReportBuilder {

    private FinancialReport report;

    public ConcreteFinancialReportBuilder() {
        this.reset();
    }

    @Override
    public ReportBuilder reset() {
        this.report = new FinancialReport();
        return this;
    }

    @Override
    public ReportBuilder setMetadata(String id, String title) {
        this.report.setId(id);
        this.report.setTitle(title);
        return this;
    }

    @Override
    public ReportBuilder applyTemplate(FinancialReportTemplate template) {
        for (String section : template.getDefaultSections()) {
            this.report.addSection("Template Section: " + section);
        }
        return this;
    }

    @Override
    public ReportBuilder setComponents(ReportComponentFactory factory) {
        this.report.setHeaderContent(factory.createHeader().getFormattedHeader());
        this.report.setFooterContent(factory.createFooter().getLegalNotice());
        return this;
    }

    @Override
    public ReportBuilder addSection(String content) {
        this.report.addSection(content);
        return this;
    }

    @Override
    public ReportBuilder applyWatermark(String watermarkText) {
        this.report.setWatermark(watermarkText);
        return this;
    }

    @Override
    public FinancialReport build() {
        FinancialReport result = this.report;
        this.reset();
        return result;
    }
}
