package com.globalfintech.reports.infrastructure.factory;

import com.globalfintech.reports.domain.factory.ReportFooter;

class USGAAPFooter implements ReportFooter {
    @Override
    public String getLegalNotice() {
        return "Compliant with FASB Regulation. Subject to SEC Audit.";
    }
}
