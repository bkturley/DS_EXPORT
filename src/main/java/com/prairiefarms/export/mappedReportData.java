package com.prairiefarms.export;

import com.prairiefarms.export.types.Report;
import com.prairiefarms.export.types.ReportColumn;
import com.prairiefarms.export.types.ReportRow;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;

import java.util.ArrayList;
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
                            String validateMe;
                            if(!"blank".equals(reportColumn.getType())){
                                validateMe = textLine.substring(reportColumn.getPosition()[0] - 1, reportColumn.getPosition()[1]);
                            }else{
                                validateMe = "";
                            }
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
            if(!"blank".equals(reportColumn.getType())){
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
        }
        return dataTypesMatch;
    }

    private boolean isPositionalMatch(String textLine, ReportRow reportRow) {
        boolean positionalMatch = true;

        List<Integer> validPositions = getValidPositionList(reportRow);

        List<Integer> occupiedPositions = getOccupiedPositionList(textLine);

        if(!validPositions.containsAll(occupiedPositions)){
            positionalMatch = false;
        }

        return positionalMatch;
    }

    private List<Integer> getOccupiedPositionList(String textLine) {
        List<Integer> occupiedPositions = new ArrayList<>();
        for(int i = 0; i < textLine.length(); i++){
            if(!(' ' == (textLine.toCharArray()[i]))){
                occupiedPositions.add(i);
            }
        }
        return occupiedPositions;
    }

    private List<Integer> getValidPositionList(ReportRow reportRow) {
        List<Integer> validPositions = new ArrayList<>();
        for (ReportColumn reportColumn : reportRow.getColumns()) {
            if(!"blank".equals(reportColumn.getType())){
                for(int i = reportColumn.getPosition()[0]; i<=reportColumn.getPosition()[1]; i++){
                    validPositions.add(i-1);
                }
            }
        }
        return validPositions;
    }
}
