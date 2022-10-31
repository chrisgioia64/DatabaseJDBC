package app;

import app.config.DatabaseApplicationFactory;
import app.config.MyDatabaseApplication;
import app.config.StagingDatabaseConfiguration;

public class StagingApplicationRunner {

    public static void main(String[] args) {
        MyDatabaseApplication app
                = DatabaseApplicationFactory.createApp(StagingDatabaseConfiguration.class);

    }
}
