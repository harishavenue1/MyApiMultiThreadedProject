package com.api.cucumber.utils;

import org.junit.Assert;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import static java.lang.System.getProperty;

public class PropertyUtils {

    private static Properties p = new Properties();
    static {
        try {

        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    private static void readProperties() throws IOException {
        p.load(new FileReader("src/test/resources/environment.properties"));
        String envName = getProperty("system.environment");

        if (envName.equals("SI")) {
            p.load(new FileReader("src/test/resources/application_si.properties"));
        } else {
            p.load(new FileReader("src/test/resources/application_dev.properties"));
        }
    }

    public static void loadProperties(String serviceName) {
        String envName = getProperty("system.environment");
        String servicePropertyFilePath = "src/test/resources/service.properties/" + envName + "/" + serviceName + ".properties";

        try {
            p.load(new FileReader(servicePropertyFilePath));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    public static String getProperty(String key) {
        return p.getProperty(key);
    }
}
