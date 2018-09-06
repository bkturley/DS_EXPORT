package com.prairiefarms.export;

import javax.mail.MessagingException;
import java.util.Arrays;
import java.util.List;

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
        //todo: validate input.
        String reportName = args[0];
        List<String> emailRecipients = Arrays.asList(args[1].trim().split(" "));
        String emailSubjectLine = args[2];
        String emailMessageBodyText = args[3];

        email.send(emailRecipients, emailSubjectLine, emailMessageBodyText);
        ifsCleanup.remove(reportName);
    }
}
