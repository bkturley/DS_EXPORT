package com.prairiefarms.export;

import java.util.List;

public class Line {
    private String type;
    private int precision;
    private int element;
    private boolean repeatable;
    private String line;
    private List<ReportColumn> columns;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public int getElement() {
        return element;
    }

    public void setElement(int element) {
        this.element = element;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public List<ReportColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<ReportColumn> columns) {
        this.columns = columns;
    }
}
