package app.utils;

import app.exceptions.ApiException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Utils {

    public static String getPropertyValue(String propName, String resourceName) {

        // First try environment variables
        String value = System.getenv(propName);

        if (value != null) {
            return value.trim();
        }

        // Fall back to config.properties locally
        try (InputStream is = Utils.class.getClassLoader().getResourceAsStream(resourceName)) {

            if (is == null) {
                throw new ApiException(500,
                        String.format("Resource %s not found and environment variable %s is missing",
                                resourceName, propName));
            }

            Properties prop = new Properties();
            prop.load(is);

            value = prop.getProperty(propName);

            if (value != null) {
                return value.trim();
            }

            throw new ApiException(500,
                    String.format("Property %s not found in %s", propName, resourceName));

        } catch (IOException ex) {
            throw new ApiException(500,
                    String.format("Could not read property %s.", propName));
        }
    }
}