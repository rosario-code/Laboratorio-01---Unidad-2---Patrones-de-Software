package com.globalfintech.reports.infrastructure.factory;

import com.globalfintech.reports.domain.factory.ReportFooter;

class EUTaxFooter implements ReportFooter {
    @Override
    public String getLegalNotice() {
        return "Compliant with EU Accounting Directive 2013/34/EU. Subject to EU Tax Authorities.";
    }
}
