package com.globalfintech.reports.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.globalfintech.reports.domain.prototype.FinancialReportTemplate;
import com.globalfintech.reports.domain.repository.TemplateRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class RedisTemplateRepository implements TemplateRepository {

    private static final String KEY_PREFIX = "templates:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisTemplateRepository(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<FinancialReportTemplate> findByKey(String key) {
        String json = redisTemplate.opsForValue().get(KEY_PREFIX + key);
        if (json == null) {
            return Optional.empty();
        }
        try {
            TemplateData data = objectMapper.readValue(json, TemplateData.class);
            return Optional.of(new FinancialReportTemplate(data.templateId(), data.defaultSections(), data.stylingConfig()));
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void save(String key, FinancialReportTemplate template) {
        TemplateData data = new TemplateData(template.getTemplateId(), template.getDefaultSections(), template.getStylingConfig());
        try {
            redisTemplate.opsForValue().set(KEY_PREFIX + key, objectMapper.writeValueAsString(data));
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    public record TemplateData(String templateId, List<String> defaultSections, Map<String, String> stylingConfig) {
    }
}
