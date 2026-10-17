package com.globalfintech.reports.domain.model;

import java.util.ArrayList;
import java.util.List;

public class FinancialReport {
    private String id;
    private String title;
    private String headerContent;
    private String footerContent;
    private List<String> sections = new ArrayList<>();
    private String watermark;
    private byte[] content;
    private String contentType;

    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setHeaderContent(String headerContent) { this.headerContent = headerContent; }
    public void setFooterContent(String footerContent) { this.footerContent = footerContent; }
    public void addSection(String section) { this.sections.add(section); }
    public void setWatermark(String watermark) { this.watermark = watermark; }
    public void setContent(byte[] content) { this.content = content; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getHeaderContent() { return headerContent; }
    public String getFooterContent() { return footerContent; }
    public List<String> getSections() { return sections; }
    public String getWatermark() { return watermark; }
    public byte[] getContent() { return content; }
    public String getContentType() { return contentType; }
}
