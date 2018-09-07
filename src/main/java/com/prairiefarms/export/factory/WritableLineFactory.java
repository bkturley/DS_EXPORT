package com.prairiefarms.export.factory;

import com.prairiefarms.export.factory.products.WriteableLine;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.CellType;

import java.util.List;

public class WritableLineFactory {
    public WriteableLine newWritableLine(List<Pair<String, String>> cells, String recordType){
        return new WriteableLine(cells, recordType);
    }
}
