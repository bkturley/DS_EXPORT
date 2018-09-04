package com.prairiefarms.export;

public class Export {

    private static ExportReportApplication exportReportApplication;

    public Export(){
    }

    Export(ExportReportApplication exportReportApplication){
        this.exportReportApplication = exportReportApplication;
    }

    public static void main(String[] args){
        try{
            if(exportReportApplication == null){
                exportReportApplication = new ExportReportApplication(args[0]);
            }
            exportReportApplication.run(args);
        }catch (java.lang.Exception exception){
            exception.printStackTrace();
        }
    }
}
