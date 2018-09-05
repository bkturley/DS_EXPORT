package com.prairiefarms.export;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ReportAccess {
    private Configuration configuration = new Configuration();
    private ObjectMapper objectMapper = new ObjectMapper();
    private FileAccess fileAccess = new FileAccess();

    public Report getReport(String textFileName) throws IOException {
        Report returnME = null;
        String jsonName = getJsonName(textFileName);
        File jsonFile = fileAccess.getFile(configuration.getProperty("jsonMaps") + jsonName.trim() + ".json");
        List<Report> reports = objectMapper.readValue(jsonFile, new TypeReference<List<Report>>() {
        });

        for (Report report : reports) {
            if (textFileName.trim().toLowerCase().contains(report.getTitle().trim().toLowerCase())) {
                returnME = report;
                break;
            }
        }

        if (returnME == null) {
            throw new IOException("Report Conversion layout with title: " + jsonName + " not found in conversion file: " + jsonName + ".json");
        }
        return returnME;
    }

    private String getJsonName(String textFileName) {
        String[] tokens = textFileName.split("\\.");
        return tokens[tokens.length-2];
    }
}
