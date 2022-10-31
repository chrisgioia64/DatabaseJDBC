package app.config;

import lombok.extern.log4j.Log4j2;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Log4j2
public class DatabaseProperties {

    private Properties prop;
    private String dbPropertiesFile;
    public static final String URL = "url";
    public static final String USER = "user";
    public static final String PASSWORD = "password";

    public DatabaseProperties(String dbPropertiesFile) {
        initializeProperties(dbPropertiesFile);
    }

    public String getProperty(String key) {
        return prop.getProperty(key);
    }

    private void initializeProperties(String dbPropertiesFile) {
        this.prop = new Properties();
        this.dbPropertiesFile = dbPropertiesFile;
        try {
            prop.load(new FileInputStream(dbPropertiesFile));
        } catch (IOException e) {
            log.error("Could not load database properties file: " + dbPropertiesFile);
            e.printStackTrace();
        }
    }

}
