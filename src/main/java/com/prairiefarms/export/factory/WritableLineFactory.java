package com.prairiefarms.export.factory;

import com.prairiefarms.export.factory.products.WriteableLine;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

class WritableLineFactory {
    public WriteableLine newWritableLine(List<Pair<String, String>> cells, String recordType){
        return new WriteableLine(cells, recordType);
    }
}
