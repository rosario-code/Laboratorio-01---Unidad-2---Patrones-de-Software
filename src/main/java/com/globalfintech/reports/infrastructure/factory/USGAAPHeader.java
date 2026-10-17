package com.globalfintech.reports.infrastructure.factory;

import com.globalfintech.reports.domain.factory.ReportHeader;

class USGAAPHeader implements ReportHeader {
    @Override
    public String getFormattedHeader() {
        return "=== US FINANCIAL REPORT - US GAAP STANDARDS ===";
    }
}
