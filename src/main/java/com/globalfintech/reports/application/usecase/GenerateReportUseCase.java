package com.globalfintech.reports.application.usecase;

import com.globalfintech.reports.domain.builder.ReportBuilder;
import com.globalfintech.reports.domain.factory.ReportComponentFactory;
import com.globalfintech.reports.domain.model.FinancialReport;
import com.globalfintech.reports.domain.prototype.FinancialReportTemplate;
import com.globalfintech.reports.domain.repository.TemplateRepository;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GenerateReportUseCase {

    private final Map<String, ReportComponentFactory> factoryMap;
    private final Map<String, FinancialReportTemplate> prototypeRegistry = new ConcurrentHashMap<>();
    private final TemplateRepository templateRepository;
    private final BeanFactory beanFactory;

    public GenerateReportUseCase(Map<String, ReportComponentFactory> factoryMap,
                                 TemplateRepository templateRepository,
                                 BeanFactory beanFactory) {
        this.factoryMap = factoryMap;
        this.templateRepository = templateRepository;
        this.beanFactory = beanFactory;
        this.initPrototypeRegistry();
    }

    private void initPrototypeRegistry() {
        FinancialReportTemplate monthlyTemplate = new FinancialReportTemplate(
                "MONTHLY_STD",
                List.of("Resumen Ejecutivo", "Balance General", "Estado de Resultados"),
                Map.of("theme", "DARK_BLUE", "fontSize", "11pt")
        );
        registerCustomTemplate("MONTHLY_STD", monthlyTemplate);
    }

    public FinancialReport execute(String reportId, String title, String normativity, String templateKey,
                                   String format, String watermark, Map<String, String> params) {
        ReportComponentFactory factory = factoryMap.get(normativity.toUpperCase() + "Factory");
        if (factory == null) {
            throw new IllegalArgumentException("Normativa legal no soportada: " + normativity);
        }

        FinancialReportTemplate clonedTemplate = findTemplate(templateKey).clone();
        clonedTemplate.applyDynamicParameters(params);

        String builderName = (format == null ? "JSON" : format.toUpperCase()) + "Builder";
        if (!beanFactory.containsBean(builderName)) {
            throw new IllegalArgumentException("Formato no soportado: " + format);
        }
        ReportBuilder reportBuilder = beanFactory.getBean(builderName, ReportBuilder.class);

        reportBuilder.reset()
                .setMetadata(reportId, title)
                .applyTemplate(clonedTemplate)
                .setComponents(factory)
                .addSection("Anexo de Riesgo Operativo - Datos Específicos del Cliente");
        if (watermark != null && !watermark.isBlank()) {
            reportBuilder.applyWatermark(watermark);
        }
        return reportBuilder.build();
    }

    private FinancialReportTemplate findTemplate(String templateKey) {
        FinancialReportTemplate template = prototypeRegistry.get(templateKey);
        if (template == null) {
            template = templateRepository.findByKey(templateKey)
                    .orElseThrow(() -> new IllegalArgumentException("Plantilla no encontrada: " + templateKey));
            prototypeRegistry.put(templateKey, template);
        }
        return template;
    }

    public void registerCustomTemplate(String key, FinancialReportTemplate template) {
        templateRepository.save(key, template);
        prototypeRegistry.put(key, template);
    }
}
