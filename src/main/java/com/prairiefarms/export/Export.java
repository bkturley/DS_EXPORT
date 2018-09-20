package com.prairiefarms.export;

import com.prairiefarms.export.access.LoggerAccess;

public class Export {

    private static ExportReportApplication exportReportApplication;
    private static LoggerAccess loggerAccess = new LoggerAccess();

    Export(ExportReportApplication exportReportApplication, LoggerAccess loggerAccess){
        this.exportReportApplication = exportReportApplication;
        this.loggerAccess = loggerAccess;
    }

    public static void main(String[] args){
        try{
            loggerAccess.log("called with params: " + args[0] + " " + args[1] + " " + args[2] + " " + args[3] );
            if(exportReportApplication == null){
                exportReportApplication = new ExportReportApplication(args[0]);
            }
            exportReportApplication.run(args);
        }catch (java.lang.Exception exception){
            exception.printStackTrace();
            loggerAccess.log(exception);
        }
    }
}
