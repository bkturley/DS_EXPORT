package com.prairiefarms.export.factory;

import com.prairiefarms.export.factory.products.writeableLine;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.CellType;

import java.util.List;

public class WritableLineFactory {
    public writeableLine newWritableLine(List<Pair<String, CellType>> cells, String recordType){
        return new writeableLine(cells, recordType);
    }
}
