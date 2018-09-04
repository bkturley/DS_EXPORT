package com.prairiefarms.export;

import java.util.List;

public class Report {
    private String title;
    private List<ReportRow> reportRows;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ReportRow> getReportRows() {
        return reportRows;
    }

    public void setReportRows(List<ReportRow> reportRows) {
        this.reportRows = reportRows;
    }
}
