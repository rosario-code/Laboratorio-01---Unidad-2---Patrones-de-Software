package com.globalfintech.reports.infrastructure.factory;

import com.globalfintech.reports.domain.factory.ReportComponentFactory;
import com.globalfintech.reports.domain.factory.ReportFooter;
import com.globalfintech.reports.domain.factory.ReportHeader;
import org.springframework.stereotype.Component;

@Component("USGAAPFactory")
public class USGAAPReportFactory implements ReportComponentFactory {
    @Override
    public ReportHeader createHeader() { return new USGAAPHeader(); }

    @Override
    public ReportFooter createFooter() { return new USGAAPFooter(); }
}
