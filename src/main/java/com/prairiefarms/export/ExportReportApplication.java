package com.prairiefarms.export;

import javax.mail.MessagingException;
import java.util.Arrays;
import java.util.List;

class ExportReportApplication {

    private Email email;

    public ExportReportApplication(String reportId){
        this(new Email(reportId));
    }

    ExportReportApplication(Email email){
        this.email = email;
    }

    public void run(String[] args){
        //todo: validate input.
        List<String> emailRecipients = Arrays.asList(args[1].trim().split(" "));
        String emailSubjectLine = args[2];
        String emailMessageBodyText = args[3];
        email.send(emailRecipients, emailSubjectLine, emailMessageBodyText);
    }
}
