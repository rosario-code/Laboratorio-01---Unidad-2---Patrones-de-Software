package com.globalfintech.reports.infrastructure.factory;

import com.globalfintech.reports.domain.factory.ReportHeader;

class IFRSHeader implements ReportHeader {
    @Override
    public String getFormattedHeader() {
        return "=== GLOBAL BANK - IFRS FINANCIAL STATEMENT ===";
    }
}
