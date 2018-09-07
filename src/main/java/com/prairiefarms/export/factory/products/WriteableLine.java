package com.prairiefarms.export.factory.products;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class WriteableLine {
    private List<Pair<String, String>> cells;
    private String recordType;
    public WriteableLine(List<Pair<String, String>> cells, String recordType){
        this.cells = cells;
        this.recordType = recordType;
    }

    public List<Pair<String, String>> getCells() {
        return cells;
    }

    public String getRecordType() {
        return recordType;
    }
}
