package app;

import app.config.DatabaseApplicationFactory;
import app.config.MyDatabaseApplication;
import app.config.StagingDatabaseConfiguration;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Log4j2
public class DatabaseScriptRunner {

    public static void main(String[] args) throws FileNotFoundException, SQLException {
        MyDatabaseApplication app = DatabaseApplicationFactory.createApp(StagingDatabaseConfiguration.class);
        Connection con = app.createConnection();
        log.info("Connection established....");

        ScriptRunner sr = new ScriptRunner(con);
        Reader reader = new BufferedReader(new FileReader("src/main/resources/data.sql"));
        sr.runScript(reader);
        log.info("Data addition successful");
    }

}
