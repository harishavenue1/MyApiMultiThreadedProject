package com.api.cucumber.utils;

import java.io.File;

public class LogConfig {

    static {
        try {
            System.setProperty("logback.configurationFile", "src/test/resources/logback-test.xml");

            File logsDir = new File("logs");
            if (!logsDir.exists()) {
                boolean created = logsDir.mkdir();
                System.out.println("Logs directory created: "+ created);
            }

            // print diagnostic information
            System.out.println("Working Directory "+ System.getProperty("user.dir"));
            System.out.println("Logs Directory "+ logsDir.getAbsolutePath());
            System.out.println("Can Write to Logs Directory "+ logsDir.canWrite());

        } catch (Exception e) {
            System.err.println("Error initializing logging: "+ e.getMessage());
            e.printStackTrace();
        }
    }
}
