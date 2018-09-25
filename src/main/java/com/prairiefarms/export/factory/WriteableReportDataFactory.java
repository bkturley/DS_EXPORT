package com.prairiefarms.export.factory;

import com.prairiefarms.export.factory.products.WriteableReportData;
import com.prairiefarms.export.factory.products.TextLineList;
import com.prairiefarms.export.types.Report;
import com.prairiefarms.export.types.ReportColumn;
import com.prairiefarms.export.types.ReportRow;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class WriteableReportDataFactory {

    private WritableLineFactory writableLineFactory;
    private ReportFactory reportFactory;
    private TextLineList textLineList;

    public WriteableReportDataFactory(){
        this(new WritableLineFactory(), new ReportFactory(), new TextLineList());
    }

    WriteableReportDataFactory(WritableLineFactory writableLineFactory, ReportFactory reportFactory, TextLineList textLineList){
        this.writableLineFactory = writableLineFactory;
        this.reportFactory = reportFactory;
        this.textLineList = textLineList;
    }


    public WriteableReportData newWriteableReportData(String fileName) throws IOException {
        WriteableReportData returnMe = new WriteableReportData();
        List<String> textLines = textLineList.getLines(fileName);
        for (String textLine : textLines) {
            if (!textLine.isEmpty()) {
                boolean lineTypeWasDetermined = false;
                Report report = reportFactory.getReport(fileName); //move outside loop?
                for (ReportRow reportRow : report.getReportRows()) {
                    if(!lineTypeWasDetermined){
                        boolean positionalMatch = isPositionalMatch(textLine, reportRow);

                        boolean dataTypesMatch = true;
                        if (positionalMatch) {
                            dataTypesMatch = isDataTypeMatch(textLine, reportRow);
                        }
                        boolean recordMatchesKnownType = positionalMatch && dataTypesMatch;
                        if (recordMatchesKnownType) {
                            List<Pair<String, String>> cellWithDataTypeList = new ArrayList<>();
                            for (ReportColumn reportColumn : reportRow.getColumns()) {
                                String validateMe;
                                if(!"blank".equals(reportColumn.getType())){
                                    validateMe = textLine.substring(reportColumn.getPosition()[0] - 1, reportColumn.getPosition()[1]);
                                }else{
                                    validateMe = "";
                                }
                                cellWithDataTypeList.add(new ImmutablePair<>(validateMe, reportColumn.getType()));
                            }
                            returnMe.getData().add(writableLineFactory.newWritableLine(cellWithDataTypeList, reportRow.getName()));
                            lineTypeWasDetermined = true;
                        }
                    }
                }
                if(!lineTypeWasDetermined){
                    throw new IOException("This report lines type could not be determined: "
                            + textLine + "Likely cause is out of date JSON mapping.");
                }
            }
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
                                Integer.parseInt(validateMe.trim().replaceAll(",", ""));
                            } catch (NumberFormatException numberFormatException) {
                                dataTypesMatch = false;
                            }
                        case "double":
                            try {
                                Float.parseFloat(validateMe.trim().replaceAll(",", ""));
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
