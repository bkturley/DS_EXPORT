package com.prairiefarms.export;

import javax.mail.MessagingException;

public class ExportReportApplication {

    private Email email;
    private IfsCleanup ifsCleanup;

    public ExportReportApplication(String reportId){
        this(new Email(reportId), new IfsCleanup());
    }

    ExportReportApplication(Email email, IfsCleanup ifsCleanup){
        this.email = email;
        this.ifsCleanup = ifsCleanup;
    }

    public void run(String[] args) throws MessagingException {
        email.send(args[1], args[2], args[3]);
        ifsCleanup.remove(args[0]);
    }
}
