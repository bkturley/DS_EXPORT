package com.prairiefarms.export;

import com.prairiefarms.export.access.LoggerAccess;

public class Export {

    private static ExportReportApplication exportReportApplication;

    Export(ExportReportApplication exportReportApplication){
        this.exportReportApplication = exportReportApplication;
    }

    public static void main(String[] args){
        try{
            new LoggerAccess().log("called with params: " + args[0] + " " + args[1] + " " + args[2] + " " + args[3] );

            if(exportReportApplication == null){
                exportReportApplication = new ExportReportApplication(args[0]);
            }
            exportReportApplication.run(args);
        }catch (java.lang.Exception exception){
            exception.printStackTrace();
            new LoggerAccess().log(exception);
        }
    }
}
