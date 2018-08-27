package com.prairiefarms.export.types;

import java.util.List;

public class Report {
    private String title;
    private List<ReportRow> section;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ReportRow> getSection() {
        return section;
    }

    public void setSection(List<ReportRow> section) {
        this.section = section;
    }
}
