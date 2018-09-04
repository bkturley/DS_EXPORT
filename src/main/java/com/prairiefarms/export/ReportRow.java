package com.prairiefarms.export;

import java.util.List;


public class ReportRow {
    private String name;
    private String identifier;
    private boolean repeatable;
    private List<ReportColumn> columns;
    private String line;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public List<ReportColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<ReportColumn> columns) {
        this.columns = columns;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

}
