package com.prairiefarms.export;

public class Export {

    public static void main(String[] args){
        new Email(args[0]).send(args[1]);
    }
}
