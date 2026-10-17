package com.globalfintech.reports.domain.repository;

import com.globalfintech.reports.domain.prototype.FinancialReportTemplate;

import java.util.Optional;

public interface TemplateRepository {
    Optional<FinancialReportTemplate> findByKey(String key);
    void save(String key, FinancialReportTemplate template);
}
