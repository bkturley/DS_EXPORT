package com.prairiefarms.export;

import com.prairiefarms.export.factory.WritableLineFactory;
import com.prairiefarms.export.factory.products.WriteableLine;
import com.prairiefarms.export.types.Report;
import com.prairiefarms.export.types.ReportColumn;
import com.prairiefarms.export.types.ReportRow;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.CellType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class mappedReportData {

    WritableLineFactory writableLineFactory = new WritableLineFactory();

    public List<WriteableLine> getWriteableData(List<String> textLines, Report report) throws IOException {
        List<WriteableLine> returnMe = new ArrayList<>();
        for (String textLine : textLines) {
            if (!textLine.isEmpty()) {
                boolean lineTypeWasDetermined = false;
                for (ReportRow reportRow : report.getReportRows()) {
                    if(!lineTypeWasDetermined){
                        boolean positionalMatch = isPositionalMatch(textLine, reportRow);

                        boolean dataTypesMatch = true;
                        if (positionalMatch) {
                            dataTypesMatch = isDataTypeMatch(textLine, reportRow);
                        }
                        boolean recordMatchesKnownType = positionalMatch && dataTypesMatch;
                        if (recordMatchesKnownType) {
                            List<Pair<String, CellType>> cellWithDataTypeList = new ArrayList<>();
                            for (ReportColumn reportColumn : reportRow.getColumns()) {
                                String validateMe;
                                if(!"blank".equals(reportColumn.getType())){
                                    validateMe = textLine.substring(reportColumn.getPosition()[0] - 1, reportColumn.getPosition()[1]);
                                }else{
                                    validateMe = "";
                                }
                                cellWithDataTypeList.add(new ImmutablePair<>(validateMe, getCellType(reportColumn.getType())));
                            }
                            returnMe.add(writableLineFactory.newWritableLine(cellWithDataTypeList, reportRow.getName()));
                            lineTypeWasDetermined = true;
                        }
                    }
                }
                if(lineTypeWasDetermined == false){
                    throw new IOException("This report lines type could not be determined: "
                            + textLine + "Likely cause is out of date JSON mapping.");
                }
            }
        }
        return returnMe;
    }

    private CellType getCellType(String type) {
        CellType returnMe;
        switch (type){
            case "string":
                returnMe = CellType.STRING;
                break;
            case "integer":
            case "double":
                returnMe = CellType.NUMERIC;
                break;
            case "blank":
                returnMe = CellType.BLANK;
                break;
            default:
                returnMe = CellType.STRING;
        }
        return returnMe;
    }

    private boolean isDataTypeMatch(String textLine, ReportRow reportRow) {
        boolean dataTypesMatch = true;
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
