package com.prairiefarms.export.factory.products;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.CellType;

import java.util.List;

public class writeableLine {
    private List<Pair<String, CellType>> cells;
    private String recordType;
    public writeableLine(List<Pair<String, CellType>> cells, String recordType){
        this.cells = cells;
        this.recordType = recordType;
    }

    public List<Pair<String, CellType>> getCells() {
        return cells;
    }

    public String getRecordType() {
        return recordType;
    }
}
