package com.globalfintech.reports.infrastructure.factory;

import com.globalfintech.reports.domain.factory.ReportFooter;

class IFRSFooter implements ReportFooter {
    @Override
    public String getLegalNotice() {
        return "Compliant with IASB Standards. Confidential Document.";
    }
}
