package com.globalfintech.reports.domain.prototype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FinancialReportTemplate implements Prototype<FinancialReportTemplate> {
    private String templateId;
    private List<String> defaultSections;
    private Map<String, String> stylingConfig;

    public FinancialReportTemplate(String templateId, List<String> defaultSections, Map<String, String> stylingConfig) {
        this.templateId = templateId;
        this.defaultSections = defaultSections;
        this.stylingConfig = stylingConfig;
    }

    @Override
    public FinancialReportTemplate clone() {
        List<String> clonedSections = new ArrayList<>(this.defaultSections);
        Map<String, String> clonedStyles = new HashMap<>(this.stylingConfig);
        return new FinancialReportTemplate(this.templateId, clonedSections, clonedStyles);
    }

    public String getTemplateId() { return templateId; }
    public List<String> getDefaultSections() { return defaultSections; }
    public Map<String, String> getStylingConfig() { return stylingConfig; }

    public void applyDynamicParameters(Map<String, String> params) {
        if (params == null) {
            return;
        }
        params.forEach((key, value) -> {
            String variable = "{{" + key + "}}";
            defaultSections.replaceAll(section -> section.replace(variable, value));
            stylingConfig.replaceAll((style, styleValue) -> styleValue.replace(variable, value));
            if (stylingConfig.containsKey(key)) {
                stylingConfig.put(key, value);
            }
        });
    }
}
