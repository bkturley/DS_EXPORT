package com.prairiefarms.export;

public class Export {

    public static void main(String[] args){

        try{
            new Email(args[0]).send(args[1]);
        }catch (Exception exception){

//            try {
//                //todo: logging framework
//                Configuration configuration = new Configuration();
//                File logFile = new IFSJavaFile(configuration.getProperty("workingDirectory" + new Date().toString() + "log.txt"));
//                logFile.createNewFile();
//                FileUtils.writeStringToFile(logFile, exception.toString(), "UTF-8");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }

    }
}
