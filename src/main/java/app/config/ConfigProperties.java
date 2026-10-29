package app.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigProperties {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigProperties.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new RuntimeException("config.properties not found");
            }

            properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException("Could not load config.properties", e);
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}