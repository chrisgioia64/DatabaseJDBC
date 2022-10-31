package app.config;

import java.io.File;

public class DatabaseApplicationFactory {

    public static MyDatabaseApplication createApp(Class<?> clazz) {
        MyDatabaseApplication app = new MyDatabaseApplication();
        if (clazz.isAnnotationPresent(DbFile.class)) {
            DbFile file = clazz.getAnnotation(DbFile.class);
            File f = new File(file.fileName());
            if(!f.exists()) {
                throw new IllegalArgumentException(
                        "The database file name does not exist: " + f.getName());
            }
            DatabaseProperties prop = new DatabaseProperties(file.fileName());
            app.setProp(prop);
            return app;
        } else {
            throw new IllegalArgumentException("No db file specified in the annotation");
        }
    }

}
