package com.prairiefarms.export;

import com.prairiefarms.export.types.Report;
import com.prairiefarms.export.types.ReportColumn;
import com.prairiefarms.export.types.ReportRow;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class mappedReportData {

    public Map<Map<String, CellType>, String> getWriteableData(List<String> textLines, Report report) {

        Map<Map<String, CellType>, String> returnMe = new LinkedHashMap<>();
        for (String textLine : textLines) {
            if (!textLine.isEmpty()) {
                for (ReportRow reportRow : report.getReportRows()) {

                    Boolean positionalMatch = isPositionalMatch(textLine, reportRow);

                    Boolean dataTypesMatch = true;
                    if (positionalMatch) {
                        dataTypesMatch = isDataTypeMatch(textLine, reportRow);
                    }

                    boolean recordMatchesKnownType = positionalMatch && dataTypesMatch;
                    if (recordMatchesKnownType) {
                        Map<String, CellType> cellWithDataTypeMap = new LinkedHashMap<>();
                        for (ReportColumn reportColumn : reportRow.getColumns()) {
                            String validateMe = textLine.substring(reportColumn.getPosition()[0] - 1, reportColumn.getPosition()[1]);
                            cellWithDataTypeMap.put(validateMe, getCellType(reportColumn.getType()));
                        }
                        returnMe.put(cellWithDataTypeMap, reportRow.getName());
                    }
                }
            }
        }

        return returnMe;
    }

    private CellType getCellType(String type) {
        CellType returnMe;
        switch (type){
            case "integer":
            case "double":
                returnMe = CellType.NUMERIC;
                break;
            default:
                returnMe = CellType.STRING;
        }
        return returnMe;
    }

    private Boolean isDataTypeMatch(String textLine, ReportRow reportRow) {
        Boolean dataTypesMatch = true;
        for (ReportColumn reportColumn : reportRow.getColumns()) {
            String validateMe = textLine.substring(reportColumn.getPosition()[0] - 1, reportColumn.getPosition()[1]);
            if (StringUtils.isNotBlank(validateMe)) {
                switch (reportColumn.getType()) {
                    case "integer":
                        try {
                            Integer.parseInt(validateMe.trim());
                        } catch (NumberFormatException numberFormatException) {
                            dataTypesMatch = false;
                        }
                    case "double":
                        try {
                            Float.parseFloat(validateMe.trim());
                        } catch (NumberFormatException numberFormatException) {
                            dataTypesMatch = false;
                        }
                }
            }
        }
        return dataTypesMatch;
    }

    private Boolean isPositionalMatch(String textLine, ReportRow reportRow) {
        //build list of invalid areas
        Map<Integer, Integer> invalidPositions = new LinkedHashMap<>();
        Map<Integer, Integer> validPositions = new LinkedHashMap<>();
        int startingIndex = 0;
        for (ReportColumn reportColumn : reportRow.getColumns()) {
            int beginCharacterIndex = reportColumn.getPosition()[0] - 1; //rpg starts indexes at 1
            int endCharacterIndex = reportColumn.getPosition()[1] - 1; //rpg starts indexes at 1
            invalidPositions.put(startingIndex, beginCharacterIndex);
            validPositions.put(beginCharacterIndex, endCharacterIndex);
            startingIndex = endCharacterIndex + 1;
        }
        invalidPositions.put(startingIndex, startingIndex + textLine.substring(startingIndex).length());
        //test that characters are in position.
        Boolean positionalMatch = true;
        for (Map.Entry<Integer, Integer> entry : invalidPositions.entrySet()) {
            if (StringUtils.isNotBlank(textLine.substring(entry.getKey(), entry.getValue()))) {
                positionalMatch = false;
            }
        }
        return positionalMatch;
    }
}
