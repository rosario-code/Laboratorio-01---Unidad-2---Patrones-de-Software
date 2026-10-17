package com.globalfintech.reports.infrastructure.factory;

import com.globalfintech.reports.domain.factory.ReportHeader;

class EUTaxHeader implements ReportHeader {
    @Override
    public String getFormattedHeader() {
        return "=== EUROPEAN UNION - EU TAX FINANCIAL STATEMENT ===";
    }
}
