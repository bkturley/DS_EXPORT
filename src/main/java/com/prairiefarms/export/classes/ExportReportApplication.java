package com.prairiefarms.export.classes;

public class ExportReportApplication {

    public void run(String[] args){
        emailAttachmentOnIbmFilesystem(args);
        removeReportFilesFromDisk(args[0]);
    }

    private void emailAttachmentOnIbmFilesystem(String[] args) {
        try{
            String reportId = args[0];
            String recipients = args[1];
            String subjectLine = args[2];
            String messageBodyText = args[3];
            new Email(reportId).send(recipients, subjectLine, messageBodyText);
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    private void removeReportFilesFromDisk(String reportName) {
        try{
            new IfsCleanup().remove(reportName);
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }
}